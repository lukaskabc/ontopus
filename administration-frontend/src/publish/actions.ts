import request, { type CancellablePromise, makeCancellable } from '@/config/rest-client.ts'
import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'
import {
  ImportProcessNotInitializedError,
  OntopusError,
  PromiseCanceledError,
  UnexpectedResponseStatusError,
  UnknownError,
} from '@/utils/errors.ts'
import type { GenericObjectType } from '@rjsf/utils'

export function resetImportProcess(versionSeriesIdentifier: string | null): CancellablePromise<Response> {
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

export function compileDataForRequest(formData: GenericObjectType, files: FileWithFieldName[]): string | FormData {
  const data = new FormData()
  if (typeof formData === 'object') {
    Object.keys(formData).forEach((key: string) => {
      data.append(key, JSON.stringify(formData[key]))
    })
  } else {
    data.append('data', formData as unknown as string)
  }
  files.forEach((file) => {
    if (file && file.name && file.file) {
      data.append(file.name, file.file)
    }
  })
  return data
}

export function submitForm(formData: GenericObjectType, files: FileWithFieldName[]) {
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
export function loadJsonForm(): CancellablePromise<JsonForm> {
  const abortController = new AbortController()

  const promise = new Promise<JsonForm>((resolve, reject) => {
    const task = () => {
      if (!abortController.signal.aborted) {
        request('GET', '/import', {}, [200], abortController)
          .then((response) => {
            if (!abortController.signal.aborted) {
              return response.json()
            }
            throw new PromiseCanceledError()
          })
          .then(makeJsonForm)
          .then(resolve)
          .catch((error) => {
            if (error instanceof UnexpectedResponseStatusError) {
              const res = error.payload
              switch (res.status) {
                case 205:
                  reject(new ImportProcessNotInitializedError())
                  return
                case 204: // no content (still processing)
                case 409: // conflict (still processing)
                  setTimeout(task, NEXT_FORM_RETRY_DELAY)
                  return
                default:
                  reject(error)
                  return
              }
            } else if (error instanceof OntopusError) {
              reject(error)
            } else {
              reject(new UnknownError('Unknown object returned', error))
            }
          })
      } else {
        reject(new PromiseCanceledError())
      }
    }
    task()
  })
  return makeCancellable(promise, abortController)
}
