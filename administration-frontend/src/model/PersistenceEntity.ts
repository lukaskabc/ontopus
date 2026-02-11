import type { DataModel } from '@toolpad/core'

export interface PersistenceEntity extends DataModel {
  /**
   * URI identifying the entity
   */
  identifier: string
}
