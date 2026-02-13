import request from '@/config/rest-client.ts'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'

export function findVersionSeries(identifier: string): Promise<VersionSeriesResponse> {
  const params = new URLSearchParams({ identifier })

  return request('GET', 'ontology?' + params.toString())
    .then((response) => response.json())
    .then((data) => new VersionSeriesResponse(data))
}
