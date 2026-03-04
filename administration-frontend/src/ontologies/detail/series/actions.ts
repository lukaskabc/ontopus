import request from '@/config/rest-client.ts'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import { VersionArtifactListEntry } from '@/model/VersionArtifactListEntry.ts'
import { ArrayPage, Pageable } from '@hallysonh/pageable'
import { toPageRequest } from '@/utils/RequestTypes.ts'

export function findVersionSeries(identifier: string): Promise<VersionSeriesResponse> {
  const params = new URLSearchParams({ series: identifier })

  return request('GET', 'series?' + params.toString())
    .then((response) => response.json())
    .then((data) => new VersionSeriesResponse(data))
}

export function findVersionArtifactsForVersionSeries(
  versionSeriesUri: string,
  pageable: Pageable,
  filter?: string[]
): Promise<ArrayPage<VersionArtifactListEntry>> {
  const options = toPageRequest(pageable)
  options.append('series', versionSeriesUri)
  filter?.forEach((val) => options.append('filter', val))
  return request('GET', `series/artifact?` + options.toString())
    .then((response) => response.json())
    .then((data: any) => {
      const page: any = data.page
      const pageable = new Pageable(page.page, page.size)
      return new ArrayPage<any>(data.content, page.totalElements, pageable)
    })
    .then((page) => page.map((data) => new VersionArtifactListEntry(data)))
}
