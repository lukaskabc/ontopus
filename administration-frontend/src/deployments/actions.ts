import request from '@/config/rest-client.ts'

export function fetchImportSources() {
  return request('GET', 'import/source').then((response) => response.json())
}
