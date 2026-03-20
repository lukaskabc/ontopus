export function withTrailingSlashString(url: string) {
  if (!url.endsWith('/')) {
    url = url + '/'
  }
  return url
}

export function withTrailingSlash(url: string) {
  const withTrailing = withTrailingSlashString(url)
  return new URL(withTrailing)
}

export function withoutTrailingSlash(url: string) {
  if (url.endsWith('/')) {
    url = url.slice(0, -1)
  }
  return url
}

const Constants = {
  BASE_URL: withoutTrailingSlash(import.meta.env.BASE_URL),
  BACKEND_URL: withTrailingSlash(import.meta.env.VITE_ONTOPUS_URL),
}

export const PUBLISH_STEPPER_ROUTE = '/publish/:versionSeriesIdentifier?'

export default Constants
