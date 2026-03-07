import { DatasetResponse } from '@/model/DcatResponseModel.ts'

export class VersionArtifactResponse extends DatasetResponse {
  // TODO distributions

  constructor(jsonObj: any) {
    super(jsonObj)
  }
}
