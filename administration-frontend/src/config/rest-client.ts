import Constants from '@/Constants.ts'
import { navigate } from 'wouter-preact/use-browser-location'
import { NotLoggedInError, PromiseCanceledError, UnexpectedResponseStatusError } from '@/utils/errors.ts'
import i18n from '@/config/i18n.ts'

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
  const boundAbort = () => {
    abortController.abort.bind(abortController)
  }

  const cancellable = Object.assign(promise, {
    abortController,
    abort: boundAbort,
  }) as CancellablePromise<T>

  // Store the original methods
  const originalThen = cancellable.then.bind(cancellable)
  const originalCatch = cancellable.catch.bind(cancellable)
  const originalFinally = cancellable.finally.bind(cancellable)

  // Override them to recursively apply the controller and bound abort
  cancellable.then = (...args) => makeCancellable(originalThen(...args), abortController)
  cancellable.catch = (...args) => makeCancellable(originalCatch(...args), abortController)
  cancellable.finally = (...args) => makeCancellable(originalFinally(...args), abortController)

  return cancellable
}

const request = (
  method: RESTMethod,
  path: string,
  options: RequestInit = {},
  expectedStatus: number[] = [200],
  abortController: AbortController = new AbortController(),
  base = Constants.BACKEND_URL
): CancellablePromise<Response> => {
  const headers: HeadersInit = Object.assign(
    {
      'Accept-Language': getLang() + ', *;q=0.6',
    } as HeadersInit,
    options.headers
  )
  const promise = fetch(
    new URL(path, base),
    Object.assign(
      {
        credentials: 'include',
        method,
        signal: abortController.signal,
        headers,
      },
      options
    )
  ).then((response): Promise<Response> => {
    if (abortController.signal.aborted) {
      return Promise.reject(new PromiseCanceledError())
    }
    if (response.status === 403) {
      navigate(Constants.BASE_URL + '/login', { replace: true })
      return Promise.reject(new NotLoggedInError())
    }
    if (!expectedStatus.includes(response.status)) {
      return Promise.reject(new UnexpectedResponseStatusError('Unexpected server response status', response))
    }
    return Promise.resolve(response)
  })
  return makeCancellable(promise, abortController)
}

export default request
