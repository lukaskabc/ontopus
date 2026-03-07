import request from '@/config/rest-client.ts'
import { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'

export function findVersionArtifact(
  versionArtifactUri: string,
  versionSeriesUri: string
): Promise<VersionArtifactResponse> {
  const params = new URLSearchParams({ artifact: versionArtifactUri, series: versionSeriesUri })

  return request('GET', 'series/artifacts?' + params.toString())
    .then((response) => response.json())
    .then((data) => new VersionArtifactResponse(data))
}
