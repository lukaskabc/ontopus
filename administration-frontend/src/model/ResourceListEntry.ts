import type { MultilingualString } from '@/model/MultilingualString.ts'
import { validateDate, validateMultilingual, validateValue } from '@/model/ModelUtils.ts'
import type { GenericObjectType } from '@rjsf/utils'

export class ResourceListEntry {
  readonly identifier: string
  readonly title: MultilingualString
  readonly version: string
  readonly modifiedDate: Date

  constructor(jsonObj: GenericObjectType) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for ResourceListEntry')
    }
    if (jsonObj instanceof ResourceListEntry) {
      this.identifier = jsonObj.identifier
      this.title = jsonObj.title
      this.version = jsonObj.version
      this.modifiedDate = jsonObj.modifiedDate
    } else {
      this.identifier = validateValue(jsonObj.identifier, 'string', 'identifier')
      this.title = validateMultilingual(jsonObj.title, 'title')
      this.version = validateValue(jsonObj.version, 'string', 'version')
      this.modifiedDate = validateDate(jsonObj.modifiedDate, 'modifiedDate')
    }
  }
}
