import type { AuthResponse } from '@toolpad/core'
import request, { type CancellablePromise } from '@/config/rest-client.ts'

export function submitLoginForm(formData: FormData, t: (key: string) => string): CancellablePromise<AuthResponse> {
  return request('POST', 'login', { credentials: 'include', body: formData })
    .then((response): AuthResponse => {
      if (response.status === 200) {
        return {}
      } else {
        throw {
          type: 'error',
          error: response.statusText,
        }
      }
    })
    .catch((): AuthResponse => {
      throw {
        type: 'error',
        error: t('login.error'),
      }
    })
}

export function authPing(): CancellablePromise<boolean> {
  return request('GET', 'auth-ping')
    .then((response) => response.status === 200)
    .catch(() => false)
}
