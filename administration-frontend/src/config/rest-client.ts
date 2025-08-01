import Constants from '@/Constants.ts'
import { navigate } from 'wouter-preact/use-browser-location'

export type RESTMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

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
      return Promise.reject(response)
    }
    return Promise.resolve(response)
  })

export default request
