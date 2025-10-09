import request from '@/config/rest-client.ts'
import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'

export const STAGED_FORM_PROMISE_AREA = 'STAGED_FORM_PROMISE_AREA'
const NEXT_FORM_RETRY_DELAY = 2000 // 2s

export function resetImportProcess(): Promise<Response> {
  return request('POST', '/import/initialize', {}, [204])
}

function mapToJson(value: any) {
  if (Array.isArray(value) || typeof value === 'object') {
    return JSON.stringify(value)
  }
  return value
}

export interface FileWithFieldName {
  name: string
  file: File
}

function compileDataForRequest(formData: any, files: FileWithFieldName[]): string | FormData {
  const data = new FormData()
  Object.keys(formData).forEach((key: string) => {
    data.append(key, mapToJson(formData[key]))
  })
  files.forEach((file) => {
    data.append(file.name, file.file)
  })
  return data
}

export function submitForm(formData: any, files: FileWithFieldName[]) {
  return request('POST', '/import', { body: compileDataForRequest(formData, files) }, [204, 202, 200])
}

/**
 * Requests the current JSON form.
 *
 * Automatically resets the import process if server requires it.
 *
 * Automatically re-requests the form if server didn't return anything yet.
 *
 * Returns Promise resolved with the JSON form or rejected with null
 * and cleanup function.
 */
export function loadJsonForm() {
  let repeat = true

  const cleanup = () => {
    repeat = false
  }

  const promise: Promise<JsonForm> = new Promise((resolve, reject) => {
    const task = () => {
      if (repeat) {
        request('GET', '/import', {}, [200])
          .then((response) => {
            if (repeat) {
              return response.json()
            }
            throw new Error('Promise canceled')
          })
          .then(makeJsonForm)
          .then(resolve)
          .catch((res) => {
            if (res instanceof Response) {
              switch (res.status) {
                case 205:
                  // TODO move import process initialization to publish stepper
                  // initialize based on navigation with or without version series
                  // Throw error here and redirect in staged form
                  resetImportProcess()
                    .then(() => setTimeout(task, NEXT_FORM_RETRY_DELAY))
                    .catch(console.error) // ERROR handling??
                  return
                case 204:
                  setTimeout(task, NEXT_FORM_RETRY_DELAY)
                  return
              }
            }

            reject(null)
          })
      } else {
        reject(null)
      }
    }
    task()
  })

  return {
    promise,
    cleanup,
  }
}
