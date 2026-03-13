import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import { ResourceListEntry } from '@/model/ResourceListEntry.ts'

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
