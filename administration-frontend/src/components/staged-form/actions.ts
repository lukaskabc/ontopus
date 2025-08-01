import request from '@/config/rest-client.ts'
import type StagedJsonForm from '@/model/JsonForm.ts'
import { makeStagedJsonFormAsync } from '@/model/JsonForm.ts'

export const STAGED_FORM_PROMISE_AREA = 'STAGED_FORM_PROMISE_AREA'
const NEXT_FORM_RETRY_DELAY = 2000 // 2s

export function submitForm(data: FormData | string, path: string) {
  return request('POST', path, { body: data }, [202])
}

async function makeNextFormRequest(path: string) {
  const response = await request('GET', path)
  if (response.status === 200) {
    return makeStagedJsonFormAsync(response.json())
  }
  throw response // reject
}

export function loadNextForm(path: string) {
  let repeat = true

  const cleanup = () => {
    repeat = false
  }

  const promise: Promise<StagedJsonForm> = new Promise((resolve, reject) => {
    const task = () => {
      if (repeat) {
        makeNextFormRequest(path)
          .then(resolve)
          .catch((res?: Response) => {
            if (!res || !res.status || res.status === 204) {
              setTimeout(task, NEXT_FORM_RETRY_DELAY)
            } else {
              reject(null)
            }
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
