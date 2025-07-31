import request from '@/config/rest-client.ts'

export function SubmitForm(form: FormData, path: string) {
  return request('POST', path, {
    body: form,
  })
}
