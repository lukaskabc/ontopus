import { type ElementType } from 'react'
import { type SvgIconProps } from '@mui/material'
import { Fragment, type FunctionComponent } from 'preact'
import FolderIcon from '@mui/icons-material/Folder'
import OpenFolderIcon from '@mui/icons-material/FolderOpen'
import ArticleIcon from '@mui/icons-material/Article'
import { type ItemType, ItemTypeEnum } from '@/publish/components/file_list/ItemType.ts'

import Box from '@mui/material/Box'
import IconButton from '@mui/material/IconButton'

type ItemIconType = ElementType<SvgIconProps> | typeof Fragment

function resolveIconType(type?: ItemType): [ItemIconType, ItemIconType] {
  if (type === ItemTypeEnum.FILE) {
    return [ArticleIcon, ArticleIcon]
  } else if (type === ItemTypeEnum.DIRECTORY) {
    return [FolderIcon, OpenFolderIcon]
  }
  return [Fragment, Fragment]
}

interface ItemTypeIconProps {
  isExpanded: boolean
  handleClick: (event: MouseEvent) => void
  type?: ItemType
}
export const ItemTypeIcon: FunctionComponent<ItemTypeIconProps> = (props) => {
  const { isExpanded, handleClick, type } = props
  const [ItemIcon, OpenItemIcon] = resolveIconType(type)
  if (isExpanded) {
    return (
      <>
        <IconButton onClick={handleClick} aria-label="collapse item" size="small">
          <ItemIcon sx={{ fontSize: '14px' }} />
        </IconButton>
        <Box sx={{ flexGrow: 1, borderLeft: '1px solid' }} />
      </>
    )
  } else {
    return (
      <IconButton onClick={handleClick} aria-label="Expand item" size="small">
        <OpenItemIcon sx={{ fontSize: '14px' }} />
      </IconButton>
    )
  }
}
