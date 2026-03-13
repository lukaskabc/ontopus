import type { DefaultParams, PathPattern, RegexRouteParams, StringRouteParams } from 'wouter-preact'
import { useLocation as useWouterLocation, useRoute as useWouterRoute } from 'wouter-preact'

type RouteParamsFor<T extends DefaultParams | undefined, RoutePath extends PathPattern> = T extends DefaultParams
  ? T
  : RoutePath extends string
    ? StringRouteParams<RoutePath>
    : RegexRouteParams

type UseRouteResult<TParams extends DefaultParams> = { match: true; params: TParams } | { match: false; params: null }

export function useLocation() {
  const [location, navigate] = useWouterLocation()
  return { location, navigate }
}

export function useRoute<T extends DefaultParams | undefined = undefined, RoutePath extends PathPattern = PathPattern>(
  pattern: RoutePath
): UseRouteResult<RouteParamsFor<T, RoutePath>> {
  const [match, params] = useWouterRoute<T, RoutePath>(pattern)

  if (!match) {
    return { match: false, params: null }
  }

  return { match, params }
}
