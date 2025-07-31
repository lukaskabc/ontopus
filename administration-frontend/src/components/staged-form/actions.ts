import request from '@/config/rest-client.ts'
import { makeStagedJsonFormAsync } from '@/model/JsonForm.ts'

export const STAGED_FORM_PROMISE_AREA = 'STAGED_FORM_PROMISE_AREA'
const NEXT_FORM_RETRY_DELAY = 2000 // 2s

export async function submitForm(data: FormData | string, path: string): Promise<Response> {
  // const headers = typeof data === 'string' ? { 'Content-Type': 'application/json' } : undefined
  const response = await request('POST', path, {
    body: data,
    // headers: headers,
  })
  if (response.ok) {
    return response.json()
  }
  throw response // reject
}

async function makeNextFormRequest(path: string) {
  const response = await request('POST', path)
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

  const promise = new Promise((resolve, reject) => {
    const task = () => {
      if (repeat) {
        makeNextFormRequest(path)
          .then(resolve)
          .catch(() => {
            setTimeout(task, NEXT_FORM_RETRY_DELAY)
          })
      }
      reject(null)
    }
    task()
  })

  return {
    promise,
    cleanup,
  }
}
