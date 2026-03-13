import Constants from '@/Constants.ts'
import { navigate } from 'wouter-preact/use-browser-location'

export type RESTMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

export function jsonBody(body: unknown): RequestInit {
  const bodyValue = typeof body === 'string' ? body : JSON.stringify(body)
  return {
    body: bodyValue,
    headers: { 'Content-Type': 'application/json' },
  }
}

const request = (
  method: RESTMethod,
  path: string,
  options: RequestInit = {},
  expectedStatus: number[] = [200],
  base = Constants.BACKEND_URL
): Promise<Response> =>
  fetch(
    new URL(path, base),
    Object.assign(
      {
        credentials: 'include',
        method,
      },
      options
    )
  ).then((response): Promise<Response> => {
    if (response.status === 403) {
      console.debug('Not logged in')
      navigate('/login', { replace: true })
    }
    if (!expectedStatus.includes(response.status)) {
      return Promise.reject(response) // TODO type errors
    }
    return Promise.resolve(response)
  })

export default request
