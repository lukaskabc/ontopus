import { makeEnum } from '@/utils/Enum.ts'

export const ItemTypeEnum = makeEnum(
  {
    FILE: 'FILE',
    DIRECTORY: 'DIRECTORY',
  },
  'ItemType'
)
export type ItemType = (typeof ItemTypeEnum)[keyof typeof ItemTypeEnum]
