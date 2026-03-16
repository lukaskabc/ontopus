import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import { validateValue } from '@/model/ModelUtils.ts'
import type { GenericObjectType } from '@rjsf/utils'

export class MuiModelListEntry extends ResourceListEntry implements PersistenceEntity {
  readonly id: string;
  [key: PropertyKey]: unknown

  constructor(jsonObj: unknown) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionSeriesListEntry')
    }
    super(jsonObj)

    this.id = this.identifier
  }
}

export class OntopusOptionEntry {
  readonly label: string
  readonly optionIdentifier: string

  constructor(jsonObj: GenericObjectType) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for OntopusOptionEntry')
    }
    this.label = validateValue(jsonObj.label, 'string', 'label')
    this.optionIdentifier = validateValue(jsonObj.optionIdentifier, 'string', 'optionIdentifier')
  }
}

export class MuiModelListEntryWithOptions extends MuiModelListEntry {
  readonly ontopusOptions: OntopusOptionEntry[]

  constructor(jsonObj: unknown, ontopusOptions: OntopusOptionEntry[]) {
    super(jsonObj)
    this.ontopusOptions = ontopusOptions
  }
}
