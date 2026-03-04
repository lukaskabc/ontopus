import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'
import { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import { VersionArtifactListEntry } from '@/model/VersionArtifactListEntry.ts'
import { validateValue } from '@/model/ModelUtils.ts'

export class VersionSeriesListEntry extends ResourceListEntry implements PersistenceEntity {
  readonly id: string
  readonly members: VersionArtifactListEntry[];
  [key: PropertyKey]: unknown

  constructor(jsonObj: any) {
    if (!jsonObj || typeof jsonObj !== 'object') {
      throw new Error('Invalid data: Expected a JSON object for VersionSeriesListEntry')
    }
    super(jsonObj)

    this.id = this.identifier

    this.members = validateValue(jsonObj.members, 'array', 'members').map(
      (member: any) => new VersionArtifactListEntry(member)
    )
  }
}
