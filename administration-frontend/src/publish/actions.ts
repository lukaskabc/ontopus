import request from '@/config/rest-client.ts'
import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'
import {
  ImportProcessNotInitializedError,
  OntopusError,
  PromiseCanceledError,
  UnexpectedResponseStatusError,
  UnknownError,
} from '@/utils/errors.ts'

export function resetImportProcess(versionSeriesIdentifier: string | null): Promise<Response> {
  const params = new URLSearchParams()
  if (versionSeriesIdentifier) {
    params.append('series', versionSeriesIdentifier)
  }

  return request('POST', '/import/initialize?' + params.toString(), {}, [204])
}

export const STAGED_FORM_PROMISE_AREA = 'STAGED_FORM_PROMISE_AREA'
const NEXT_FORM_RETRY_DELAY = 2000 // 2s

export interface FileWithFieldName {
  name: string
  file: File
}

function compileDataForRequest(formData: any, files: FileWithFieldName[]): string | FormData {
  const data = new FormData()
  if (typeof formData === 'object' && !(formData instanceof Array)) {
    Object.keys(formData).forEach((key: string) => {
      data.append(key, JSON.stringify(formData[key]))
    })
  } else {
    data.append('data', formData)
  }
  files.forEach((file) => {
    if (file && file.name && file.file) {
      data.append(file.name, file.file)
    }
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
            throw new PromiseCanceledError()
          })
          .then(makeJsonForm)
          .then(resolve)
          .catch((res) => {
            if (res instanceof Response) {
              switch (res.status) {
                case 205:
                  reject(new ImportProcessNotInitializedError())
                  return
                case 204: // no content (still processing)
                case 409: // conflict (still processing)
                  setTimeout(task, NEXT_FORM_RETRY_DELAY)
                  return
                default:
                  reject(new UnexpectedResponseStatusError(`Unexpected response status: ${res.status}`, res))
                  return
              }
            } else if (res instanceof OntopusError) {
              reject(res)
            } else {
              reject(new UnknownError('Returned object is not a response', res))
            }
          })
      } else {
        reject(new PromiseCanceledError())
      }
    }
    task()
  })

  return {
    promise,
    cleanup,
  }
}
