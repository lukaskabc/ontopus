import { type PersistenceEntity } from '@/model/PersistenceEntity.ts'

export interface OntologyEntity extends PersistenceEntity {
  name: string
  ontologyUri: string
  versionInfo: string
  versionIri: string
}
