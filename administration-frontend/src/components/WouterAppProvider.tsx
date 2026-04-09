import type { AppProviderProps, Navigate } from '@toolpad/core'
import { AppProvider, type Router } from '@toolpad/core/AppProvider'
import { Link, type LinkProps, useLocation, useSearchParams } from 'wouter-preact'
import { useCallback, useMemo } from 'preact/hooks'
import { forwardRef } from 'preact/compat'
import { createContext } from 'preact'

const WouterLink = forwardRef<HTMLAnchorElement, LinkProps>((props, ref) => {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-expect-error
  return <Link ref={ref} {...props} />
})

export const MuiRouterContext = createContext<Router | null>(null)

export default function (props: AppProviderProps) {
  const [pathname, navigateWouter] = useLocation()
  const [searchParams] = useSearchParams()

  const navigateImpl = useCallback<Navigate>(
    (url, { history = 'auto' } = {}) => {
      if (history === 'auto' || history === 'push') {
        return navigateWouter(url)
      }
      if (history === 'replace') {
        return navigateWouter(url, { replace: true })
      }
      throw new Error(`Invalid history option: ${history}`)
    },
    [navigateWouter]
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
    <MuiRouterContext.Provider value={routerImpl}>
      <AppProvider router={routerImpl} {...props} />
    </MuiRouterContext.Provider>
  )
}
