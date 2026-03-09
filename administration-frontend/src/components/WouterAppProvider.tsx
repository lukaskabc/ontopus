import type { AppProviderProps, Navigate } from '@toolpad/core'
import { AppProvider, type Router } from '@toolpad/core/AppProvider'
import { Link, Router as WRouter, useLocation, useSearchParams } from 'wouter-preact'
import { useCallback, useMemo } from 'preact/hooks'
import { forwardRef } from 'preact/compat'
import Constants from '@/Constants.ts'
import { createContext } from 'preact'

const WouterLink = forwardRef<HTMLAnchorElement, any>((props, ref) => {
  return <Link ref={ref} {...props} />
})

export const MuiRouterContext = createContext<Router | null>(null)

function appendBaseUrl(path: string | URL): string {
  if (typeof path === 'object') {
    path = path.toString()
  }
  if (path.startsWith(Constants.BASE_URL)) {
    return path
  }
  return Constants.BASE_URL + path
}

export default function (props: AppProviderProps) {
  const [pathname, navigate] = useLocation()
  const [searchParams] = useSearchParams()

  const navigateImpl = useCallback<Navigate>(
    (url, { history = 'auto' } = {}) => {
      if (history === 'auto' || history === 'push') {
        return navigate(appendBaseUrl(url))
      }
      if (history === 'replace') {
        return navigate(appendBaseUrl(url), { replace: true })
      }
      throw new Error(`Invalid history option: ${history}`)
    },
    [navigate]
  )

  const routerImpl = useMemo<Router>(
    () => ({
      pathname,
      searchParams,
      navigate: navigateImpl,
      WouterLink,
    }),
    [pathname, searchParams, navigateImpl]
  )
  return (
    <WRouter base={Constants.BASE_URL}>
      <MuiRouterContext.Provider value={routerImpl}>
        <AppProvider router={routerImpl} {...props} />
      </MuiRouterContext.Provider>
    </WRouter>
  )
}
