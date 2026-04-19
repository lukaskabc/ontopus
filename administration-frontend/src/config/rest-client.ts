import Constants from '@/Constants.ts'
import { navigate } from 'wouter-preact/use-browser-location'
import {
  NotLoggedInError,
  OntopusProblemDetail,
  PromiseCanceledError,
  UnexpectedResponseStatusError,
} from '@/utils/errors.ts'
import i18n from '@/config/i18n.ts'
import { validateValue } from '@/model/ModelUtils.ts'
import type { GenericObjectType } from '@rjsf/utils'

export type RESTMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'

function getLang() {
  if (i18n.isInitialized && i18n.language) {
    return i18n.language
  }
  return 'en'
}

export function jsonBody(body: unknown): RequestInit {
  const bodyValue = typeof body === 'string' ? body : JSON.stringify(body)
  return {
    body: bodyValue,
    headers: { 'Content-Type': 'application/json' },
  }
}

export interface CancellablePromise<T> extends Promise<T> {
  abortController: AbortController
  abort: (reason?: unknown) => void

  then<TResult1 = T, TResult2 = never>(
    onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null,
    onrejected?: ((reason: unknown) => TResult2 | PromiseLike<TResult2>) | undefined | null
  ): CancellablePromise<TResult1 | TResult2>

  catch<TResult = never>(
    onrejected?: ((reason: unknown) => TResult | PromiseLike<TResult>) | undefined | null
  ): CancellablePromise<T | TResult>

  finally(onfinally?: (() => void) | undefined | null): CancellablePromise<T>
}

export function makeCancellable<T>(
  promise: Promise<T>,
  abortController: AbortController = new AbortController()
): CancellablePromise<T> {
  // Hard-bind the abort method so it doesn't lose context when detached
  const boundAbort = abortController.abort.bind(abortController)

  const cancellable = Object.assign(promise, {
    abortController,
    abort: boundAbort,
  }) as CancellablePromise<T>

  // Store the original methods
  const originalThen = cancellable.then.bind(cancellable)
  const originalCatch = cancellable.catch.bind(cancellable)
  const originalFinally = cancellable.finally.bind(cancellable)

  // Override them to recursively apply the controller and bound abort
  cancellable.then = function <TResult1 = T, TResult2 = never>(
    onfulfilled?: ((value: T) => TResult1 | PromiseLike<TResult1>) | undefined | null,
    onrejected?: ((reason: unknown) => TResult2 | PromiseLike<TResult2>) | undefined | null
  ) {
    return makeCancellable(originalThen(onfulfilled, onrejected), abortController)
  }

  cancellable.catch = function <TResult = never>(
    onrejected?: ((reason: unknown) => TResult | PromiseLike<TResult>) | undefined | null
  ) {
    return makeCancellable(originalCatch(onrejected), abortController)
  }

  cancellable.finally = function (onfinally?: (() => void) | undefined | null) {
    return makeCancellable(originalFinally(onfinally), abortController)
  }

  return cancellable
}

export function problemDetail(response: Response): Promise<never> {
  return response.json().then((json: GenericObjectType) => {
    const title = validateValue(json.title, 'string', 'Error title')
    const detail = validateValue(json.detail, 'string', 'Error detail')
    throw new OntopusProblemDetail(title, detail, response)
  })
}

const request = (
  method: RESTMethod,
  path: string,
  options: RequestInit = {},
  expectedStatus: number[] = [200],
  abortController: AbortController = new AbortController(),
  base = Constants.BACKEND_URL
): CancellablePromise<Response> => {
  const headers: HeadersInit = {
    'Accept-Language': `${getLang()}, *;q=0.6`,
    ...options.headers,
  }

  const promise = fetch(new URL(path, base), {
    credentials: 'include',
    ...options,
    method,
    signal: abortController.signal,
    headers,
  })
    .then((response): Promise<Response> => {
      if (response.status === 403) {
        navigate(`${Constants.BASE_URL}/login`, { replace: true })
        return Promise.reject(new NotLoggedInError())
      }

      if (!expectedStatus.includes(response.status)) {
        if (response.headers.get('Content-Type') === 'application/problem+json') {
          return problemDetail(response)
        }
        return Promise.reject(new UnexpectedResponseStatusError('Unexpected server response status', response))
      }

      return Promise.resolve(response)
    })
    .catch((error) => {
      // Catch DOMException 'AbortError' triggered by the AbortController
      if (error.name === 'AbortError' || abortController.signal.aborted) {
        throw new PromiseCanceledError()
      }
      // Re-throw any other errors
      throw error
    })

  return makeCancellable(promise, abortController)
}

export default request
