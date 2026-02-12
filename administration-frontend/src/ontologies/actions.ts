import request from '@/config/rest-client.ts'
import { ArrayPage, Pageable } from '@hallysonh/pageable'
import { toPageRequest } from '@/utils/RequestTypes.ts'
import { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'

export function fetchImportSources() {
  return request('GET', 'import/source').then((response) => response.json())
}

export function findVersionSeries(pageable: Pageable, filter?: string[]): Promise<ArrayPage<VersionSeriesListEntry>> {
  const options = toPageRequest(pageable)
  filter?.forEach((val) => options.append('filter', val))
  return request('GET', 'ontologies?' + options.toString())
    .then((response) => response.json())
    .then((data: any) => {
      const page: any = data.page
      const pageable = new Pageable(page.page, page.size)
      return new ArrayPage<any>(data.content, page.totalElements, pageable)
    })
    .then((page) => page.map((data) => new VersionSeriesListEntry(data)))
}
