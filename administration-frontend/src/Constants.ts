function withTrailingSlash(url: string) {
  if (!url.endsWith('/')) {
    url = url + '/'
  }
  return new URL(url)
}

const Constants = {
  BACKEND_URL: withTrailingSlash(import.meta.env.VITE_ONTOPUS_URL),
}

export default Constants
