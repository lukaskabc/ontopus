import type { AppProviderProps, Navigate } from '@toolpad/core'
import { AppProvider, type Router } from '@toolpad/core/AppProvider'
import { Link, Router as WRouter, useLocation, useSearchParams } from 'wouter-preact'
import { useCallback, useMemo } from 'preact/hooks'
import { forwardRef } from 'preact/compat'

const WouterLink = forwardRef<HTMLAnchorElement, any>((props, ref) => {
  return <Link ref={ref} {...props} />
})

export default function (props: AppProviderProps) {
  const [pathname, navigate] = useLocation()
  const [searchParams] = useSearchParams()

  const navigateImpl = useCallback<Navigate>(
    (url, { history = 'auto' } = {}) => {
      if (history === 'auto' || history === 'push') {
        return navigate(url)
      }
      if (history === 'replace') {
        return navigate(url, { replace: true })
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
    <WRouter>
      <AppProvider router={routerImpl} {...props} />
    </WRouter>
  )
}
