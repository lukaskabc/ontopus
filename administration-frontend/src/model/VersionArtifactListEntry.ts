import { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import type { PersistenceEntity } from '@/model/PersistenceEntity.ts'

export class VersionArtifactListEntry extends ResourceListEntry implements PersistenceEntity {
  readonly id: string;
  [key: PropertyKey]: unknown

  constructor(jsonObj: any) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionArtifactListEntry')
    }
    super(jsonObj)

    this.id = this.identifier
  }
}
