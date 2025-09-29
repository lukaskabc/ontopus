import request from '@/config/rest-client.ts'
import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'

export const STAGED_FORM_PROMISE_AREA = 'STAGED_FORM_PROMISE_AREA'
// const NEXT_FORM_RETRY_DELAY = 2000 // 2s

export async function loadJsonForm(): Promise<JsonForm> {
  const response = await request('GET', '/import')
  if (response.status === 200) {
    const json = await response.json()
    return makeJsonForm(json)
  }
  throw response
}

export async function resetImportProcess(): Promise<void> {
  const response = await request('POST', '/import/initialize')
  if (response.status === 200) {
    return
  }
  throw response
}

function compileDataForRequest(formData: any): string | FormData {
  const data = new FormData()
  Object.keys(formData).forEach((key: string) => {
    data.append(key, formData[key])
  })
  return data
}

export function submitForm(formData: any) {
  return request('POST', '/import', { body: compileDataForRequest(formData) }, [204, 202, 200])
}

//
// export function submitForm(data: FormData | string, path: string) {
//   return request('POST', path, { body: data }, [202])
// }
//
// async function makeNextFormRequest(path: string) {
//   const response = await request('GET', path)
//   if (response.status === 200) {
//     return makeStagedJsonFormAsync(response.json())
//   }
//   throw response // reject
// }
//
// export function loadNextForm(path: string) {
//   let repeat = true
//
//   const cleanup = () => {
//     repeat = false
//   }
//
//   const promise: Promise<StagedJsonForm> = new Promise((resolve, reject) => {
//     const task = () => {
//       if (repeat) {
//         makeNextFormRequest(path)
//           .then(resolve)
//           .catch((res?: Response) => {
//             if (!res || !res.status || res.status === 204) {
//               setTimeout(task, NEXT_FORM_RETRY_DELAY)
//             } else {
//               reject(null)
//             }
//           })
//       } else {
//         reject(null)
//       }
//     }
//     task()
//   })
//
//   return {
//     promise,
//     cleanup,
//   }
// }
