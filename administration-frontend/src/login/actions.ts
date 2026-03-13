import Constants from '@/Constants.ts'
import type { AuthResponse } from '@toolpad/core'
import request from '@/config/rest-client.ts'

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

export function authPing(): Promise<boolean> {
  return new Promise((resolve) => {
    request('GET', 'auth-ping')
      .then((response) => resolve(response.status === 200))
      .catch(() => resolve(false))
  })
}
