import { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import type { PersistenceEntity } from '@/model/PersistenceEntity.ts'
import type { GenericObjectType } from '@rjsf/utils'

export class VersionArtifactListEntry extends ResourceListEntry implements PersistenceEntity {
  readonly id: string;
  [key: PropertyKey]: unknown

  constructor(jsonObj: GenericObjectType) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionArtifactListEntry')
    }
    super(jsonObj)

    this.id = this.identifier
  }
}
