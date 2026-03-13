import request from '@/config/rest-client.ts'
import { ArrayPage, Pageable } from '@hallysonh/pageable'
import { toPageRequest } from '@/utils/RequestTypes.ts'
import { MuiModelListEntry } from '@/model/MuiModelListEntry.ts'
import type { GenericObjectType } from '@rjsf/utils'

export function fetchImportSources() {
  return request('GET', 'import/source').then((response) => response.json())
}

export function findAllVersionSeries(pageable: Pageable, filter?: string[]): Promise<ArrayPage<MuiModelListEntry>> {
  const options = toPageRequest(pageable)
  filter?.forEach((val) => options.append('filter', val))
  return request('GET', 'series?' + options.toString())
    .then((response) => response.json())
    .then((data: GenericObjectType) => {
      const page = data.page
      const pageable = new Pageable(page.page, page.size)
      return new ArrayPage<unknown>(data.content, page.totalElements, pageable)
    })
    .then((page) => page.map((data) => new MuiModelListEntry(data)))
}

export function parseUri(identifier?: string) {
  if (identifier) {
    return decodeURIComponent(identifier)
  }
  return null
}
