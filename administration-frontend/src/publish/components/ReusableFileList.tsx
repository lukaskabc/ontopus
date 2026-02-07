import type { ReusableFile } from '@/model/ReusableFile.ts'
import { useMemo } from 'preact/hooks'
import type { TreeViewDefaultItemModelProperties } from '@mui/x-tree-view/models'
import {
  RichTreeView,
  TreeItemContent,
  TreeItemDragAndDropOverlay,
  TreeItemGroupTransition,
  TreeItemLabel,
  type TreeItemProps,
  TreeItemProvider,
  TreeItemRoot,
  useTreeItem,
  useTreeItemModel,
  useTreeItemUtils,
} from '@mui/x-tree-view'
import { makeEnum } from '@/utils/Enum.ts'
import { type ElementType, forwardRef, type Ref } from 'preact/compat'
import { Box, IconButton, type SvgIconProps } from '@mui/material'
import FolderIcon from '@mui/icons-material/Folder'
import OpenFolderIcon from '@mui/icons-material/FolderOpen'
import ArticleIcon from '@mui/icons-material/Article'
import { Fragment } from 'preact'

const ROOT_NODE_LABEL = '<root>'

export type ReusableFileListProps = {
  files?: ReusableFile[]
}

const ItemType = makeEnum(
  {
    FILE: 'FILE',
    DIRECTORY: 'DIRECTORY',
  },
  'ItemType'
)
export type ItemType = (typeof ItemType)[keyof typeof ItemType]

interface ExtendedItemProperties extends TreeViewDefaultItemModelProperties {
  type?: ItemType
}

function findTreeRoot(label: string, tree: ExtendedItemProperties[]): ExtendedItemProperties | null {
  for (let rootNode of tree) {
    if (rootNode.label === label || ROOT_NODE_LABEL === rootNode.label) {
      return rootNode
    }
  }

  return null
}

function findTreeNodeChild(label: string, node: ExtendedItemProperties): ExtendedItemProperties | null {
  if (node && node.children) {
    for (let child of node.children) {
      if (child.label === label) {
        return child
      }
    }
  }
  return null
}

function insertToTree(file: ReusableFile, tree: ExtendedItemProperties[]) {
  const path = file.fileName?.split('/')
  if (!path || path.length === 0) {
    console.error('Unable to process file', file)
    return
  }

  let currentPath = path[0]
  let current: ExtendedItemProperties | null = findTreeRoot(path[0], tree)
  if (!current) {
    const isFileInRoot = path.length === 1
    const type = isFileInRoot ? ItemType.FILE : ItemType.DIRECTORY
    const label = path[0] === '' ? ROOT_NODE_LABEL : path[0]
    current = { id: label, label, children: [], type }
    tree.push(current)
  }
  for (let i = 1; i < path.length; i++) {
    const label = path[i]
    currentPath += '/' + label

    let found = findTreeNodeChild(label, current)
    if (!found) {
      const isLast = i === path.length - 1
      const type = isLast ? ItemType.FILE : ItemType.DIRECTORY
      found = { id: currentPath, label, children: [], type }
      current.children?.push(found)
    }
    current = found
  }
}

type ItemIconType = ElementType<SvgIconProps> | typeof Fragment

function resolveIconType(type?: ItemType): [ItemIconType, ItemIconType] {
  if (type === ItemType.FILE) {
    return [ArticleIcon, ArticleIcon]
  } else if (type === ItemType.DIRECTORY) {
    return [FolderIcon, OpenFolderIcon]
  }
  return [Fragment, Fragment]
}

const CustomTreeItem = forwardRef(function CustomTreeItem(
  { id, itemId, label, disabled, children }: TreeItemProps,
  ref: Ref<HTMLLIElement>
) {
  const {
    getRootProps,
    getContentProps,
    getLabelProps,
    getGroupTransitionProps,
    getDragAndDropOverlayProps,
    getContextProviderProps,
    status,
  } = useTreeItem({ id, itemId, children, label, disabled, rootRef: ref })

  const { interactions } = useTreeItemUtils({
    itemId,
    children,
  })

  const type = useTreeItemModel<ExtendedItemProperties>(itemId)?.type

  const handleClick = (event: MouseEvent) => {
    interactions.handleExpansion(event)
  }

  const [ItemIcon, OpenItemIcon] = resolveIconType(type)

  return (
    <TreeItemProvider {...getContextProviderProps()}>
      <TreeItemRoot {...getRootProps({ sx: { position: 'relative', marginLeft: '24px' } })}>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            flexGrow: 1,
            width: '24px',
            height: 'calc(100% - 12px)',
            position: 'absolute',
            left: '-24px',
            top: '6px',
          }}
        >
          {status.expanded ? (
            <>
              <IconButton onClick={handleClick} aria-label="collapse item" size="small">
                <ItemIcon sx={{ fontSize: '14px' }} />
              </IconButton>
              <Box sx={{ flexGrow: 1, borderLeft: '1px solid' }} />
            </>
          ) : (
            <IconButton onClick={handleClick} aria-label="Expand item" size="small">
              <OpenItemIcon sx={{ fontSize: '14px' }} />
            </IconButton>
          )}
        </Box>

        <TreeItemContent {...getContentProps()} sx={{ paddingLeft: '14px' }}>
          <TreeItemLabel {...getLabelProps()} />
          <TreeItemDragAndDropOverlay {...getDragAndDropOverlayProps()} />
        </TreeItemContent>
        {children && <TreeItemGroupTransition {...getGroupTransitionProps()} />}
      </TreeItemRoot>
    </TreeItemProvider>
  )
})

export default function ReusableFileList({ files }: ReusableFileListProps) {
  const treeData: ExtendedItemProperties[] = useMemo(() => {
    const result: ExtendedItemProperties[] = []
    if (files) {
      for (let f of files) {
        insertToTree(f, result)
      }
    }
    return result
  }, [files])

  return <RichTreeView items={treeData} slots={{ item: CustomTreeItem }} defaultExpandedItems={[ROOT_NODE_LABEL]} />
}
