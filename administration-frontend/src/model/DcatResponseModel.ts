import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import type { MultilingualString } from '@/model/MultilingualString.ts'
import { validateDate, validateMultilingual, validateNullableValue, validateValue } from '@/model/ModelUtils.ts'

/**
 * Base class corresponding to Java EntityResponse.
 */
export class EntityResponse implements PersistenceEntity {
  readonly id: string
  readonly uri: string
  readonly identifier: string;
  [key: PropertyKey]: unknown

  constructor(jsonObj: any) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for EntityResponse')
    }

    this.uri = validateValue(jsonObj.uri, 'string', 'uri')
    this.identifier = validateValue(jsonObj.identifier, 'string', 'identifier')
    this.id = this.identifier
  }
}

/**
 * Corresponding to Java ResourceResponse.
 */
export class ResourceResponse extends EntityResponse {
  readonly description: MultilingualString
  readonly title: MultilingualString
  readonly releaseDate: Date
  readonly modifiedDate: Date
  readonly languages: string[]
  readonly previousVersion: string
  readonly version: string

  constructor(jsonObj: any) {
    super(jsonObj)

    this.title = validateMultilingual(jsonObj.title, 'title')
    this.description = validateMultilingual(jsonObj.description, 'description')
    this.version = validateValue(jsonObj.version, 'string', 'version')
    this.previousVersion = validateNullableValue(jsonObj.previousVersion, 'string', 'previousVersion')

    this.releaseDate = validateDate(jsonObj.releaseDate, 'releaseDate')
    this.modifiedDate = validateDate(jsonObj.modifiedDate, 'modifiedDate')

    if (!Array.isArray(jsonObj.languages) && jsonObj.languages != null) {
      throw new Error("Invalid data: field 'languages' must be an array")
    }

    this.languages = jsonObj.languages?.map((lang: any) => String(lang))
  }
}

/**
 * Corresponding to Java DatasetResponse.
 */
export class DatasetResponse extends ResourceResponse {
  readonly series: string

  constructor(jsonObj: any) {
    super(jsonObj)
    this.series = validateNullableValue(jsonObj.series, 'string', 'series')
  }
}
