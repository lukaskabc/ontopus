import Constants from '@/Constants.ts'
import { navigate } from 'wouter-preact/use-browser-location'

export type RESTMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

const request = (
  method: RESTMethod,
  path: string,
  options: RequestInit = {},
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
  ).then((response) => {
    if (response.status === 403) {
      console.debug('Not logged in')
      navigate('/login', { replace: true })
      return null
    }
    if (response.status !== 200) {
      throw new Error(response)
    }
    return response
  })

export default request
