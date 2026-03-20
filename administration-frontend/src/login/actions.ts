import Constants from '@/Constants.ts'
import type { AuthResponse } from '@toolpad/core'
import request, { type CancellablePromise } from '@/config/rest-client.ts'

export function submitLoginForm(formData: FormData): Promise<AuthResponse> {
  return new Promise((resolve, reject) => {
    fetch(new URL('login', Constants.BACKEND_URL), {
      method: 'POST',
      credentials: 'include',
      body: formData,
    })
      .then((response) => {
        if (response.status === 200) {
          resolve({})
        } else {
          reject({
            error: response.statusText,
          })
        }
      })
      .catch((error) => {
        console.error(error)
        reject({
          error,
        })
      })
  })
}

export function authPing(): CancellablePromise<boolean> {
  return request('GET', 'auth-ping')
    .then((response) => response.status === 200)
    .catch(() => false)
}
