import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import { ResourceListEntry } from '@/model/ResourceListEntry.ts'

export class VersionSeriesListEntry extends ResourceListEntry implements PersistenceEntity {
  readonly id: string;
  [key: PropertyKey]: unknown

  constructor(jsonObj: any) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionSeriesListEntry')
    }
    super(jsonObj)

    this.id = this.identifier
  }
}
