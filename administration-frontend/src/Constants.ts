function withTrailingSlash(url: string) {
  if (!url.endsWith('/')) {
    url = url + '/'
  }
  return new URL(url)
}

function withoutTrailingSlash(url: string) {
  if (url.endsWith('/')) {
    url = url.slice(0, -1)
  }
  return url
}

const Constants = {
  BASE_URL: withoutTrailingSlash(import.meta.env.BASE_URL),
  BACKEND_URL: withTrailingSlash(import.meta.env.VITE_ONTOPUS_URL),
}

export default Constants
