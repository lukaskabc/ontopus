import { validateValue } from '@/model/ModelUtils.ts'
import { DatasetResponse } from '@/model/DcatResponseModel.ts'
import type { GenericObjectType } from '@rjsf/utils'

/**
 * Corresponding to Java VersionSeriesResponse.
 */
export class VersionSeriesResponse extends DatasetResponse {
  readonly last: string
  readonly first: string

  constructor(jsonObj: GenericObjectType) {
    super(jsonObj)

    this.last = validateValue(jsonObj.last, 'string', 'last')
    this.first = validateValue(jsonObj.first, 'string', 'first')
  }
}
