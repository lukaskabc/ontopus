import { DatasetResponse } from '@/model/DcatResponseModel.ts'
import type { GenericObjectType } from '@rjsf/utils'
import { validateValue } from '@/model/ModelUtils.ts'

export class VersionArtifactResponse extends DatasetResponse {
  // TODO distributions
  readonly versionUri: string

  constructor(jsonObj: GenericObjectType) {
    super(jsonObj)
    this.versionUri = validateValue(jsonObj.versionUri, 'string', 'versionUri')
  }
}
