import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import type { MultilingualString } from '@/model/MultilingualString.ts'
import { validateDate, validateMultilingual, validateValue } from '@/model/ModelUtils.ts'

export class VersionSeriesListEntry implements PersistenceEntity {
  readonly id: string
  readonly identifier: string
  readonly title: MultilingualString
  readonly description: MultilingualString
  readonly version: string
  readonly modifiedDate: Date;
  [key: PropertyKey]: unknown

  constructor(jsonObj: any) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionSeriesListEntry')
    }

    this.identifier = validateValue(jsonObj.identifier, 'string', 'identifier')
    this.id = this.identifier
    this.title = validateMultilingual(jsonObj.title, 'title')
    this.description = validateMultilingual(jsonObj.description, 'description')
    this.version = validateValue(jsonObj.version, 'string', 'version')

    this.modifiedDate = validateDate(jsonObj.modifiedDate, 'modifiedDate')
  }
}
