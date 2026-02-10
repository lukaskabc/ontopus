import { ActionAwareReusableFile } from '@/model/ReusableFile.ts'
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
import { forwardRef, type Ref } from 'preact/compat'
import { Box } from '@mui/material'
import { ItemTypeIcon } from '@/publish/components/reusable_file_list/TreeItemTypeIcon.tsx'
import { TreeItemActions } from '@/publish/components/reusable_file_list/TreeItemActions.tsx'
import { type ItemType, ItemTypeEnum } from '@/publish/components/reusable_file_list/ItemType.ts'

export type ReusableFileListProps = {
  files: ActionAwareReusableFile[]
  onDelete: (file: ActionAwareReusableFile) => void
  onUpdate: (file: ActionAwareReusableFile) => void
}

interface ExtendedItemProperties extends TreeViewDefaultItemModelProperties {
  type: ItemType
  file?: ActionAwareReusableFile
  onDelete: (file: ActionAwareReusableFile) => void
  onUpdate: (file: ActionAwareReusableFile) => void
  children?: ExtendedItemProperties[]
}

function findTreeRoot(label: string, tree: ExtendedItemProperties[]): ExtendedItemProperties | null {
  for (let rootNode of tree) {
    if (rootNode.label === label) {
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

function splitPath(path?: string) {
  if (!path) {
    return []
  }

  if (path.startsWith('/')) {
    return path.substring(1).split('/')
  }
  return path.split('/')
}

function insertToTree(
  file: ActionAwareReusableFile,
  tree: ExtendedItemProperties[],
  onDelete: (file: ActionAwareReusableFile) => void,
  onUpdate: (file: ActionAwareReusableFile) => void
) {
  const path = splitPath(file.reusableFile.fileName)
  if (!path || path.length === 0) {
    console.error('Unable to process file', file)
    return
  }

  let currentPath = path[0]
  let current: ExtendedItemProperties | null = findTreeRoot(path[0], tree)
  if (!current) {
    const isFileInRoot = path.length === 1
    const type = isFileInRoot ? ItemTypeEnum.FILE : ItemTypeEnum.DIRECTORY
    const label = path[0]
    const fileValue = isFileInRoot ? file : undefined
    current = { id: label, label, children: [], type, onDelete, onUpdate, file: fileValue }
    tree.push(current)
  }
  for (let i = 1; i < path.length; i++) {
    const label = path[i]
    currentPath += '/' + label

    let found = findTreeNodeChild(label, current)
    if (!found) {
      const isLast = i === path.length - 1
      const type = isLast ? ItemTypeEnum.FILE : ItemTypeEnum.DIRECTORY
      const fileValue = isLast ? file : undefined // do not pass file to folder entried
      found = { id: currentPath, label, children: [], type, onDelete, onUpdate, file: fileValue }
      current.children?.push(found)
    }
    current = found
  }
}

function getOpacity(file?: ActionAwareReusableFile) {
  if (file?.isDeleted) {
    return 0.5
  }
  return undefined
}

/**
 * Tree list of reusable files
 * <p>Allows to list, remove and overwrite reusable files that may also be present on the server</p>
 */
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

  const { type, file, onDelete, onUpdate } = useTreeItemModel<ExtendedItemProperties>(itemId)!

  const opacity = getOpacity(file)

  const handleClick = (event: MouseEvent) => {
    interactions.handleExpansion(event)
  }

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
          <ItemTypeIcon isExpanded={status.expanded} handleClick={handleClick} type={type} />
        </Box>

        <TreeItemContent {...getContentProps()} sx={{ paddingLeft: '14px', opacity }}>
          <TreeItemLabel {...getLabelProps()} />
          {type === ItemTypeEnum.FILE && <TreeItemActions file={file} onDelete={onDelete} onUpdate={onUpdate} />}
          <TreeItemDragAndDropOverlay {...getDragAndDropOverlayProps()} />
        </TreeItemContent>
        {children && <TreeItemGroupTransition {...getGroupTransitionProps()} />}
      </TreeItemRoot>
    </TreeItemProvider>
  )
})

export default function ReusableFileList({ files, onDelete, onUpdate }: ReusableFileListProps) {
  const treeData: ExtendedItemProperties[] = useMemo(() => {
    const result: ExtendedItemProperties[] = []

    if (files) {
      for (let f of files) {
        insertToTree(f, result, onDelete, onUpdate)
      }
    }
    return result
  }, [files])

  return <RichTreeView items={treeData} slots={{ item: CustomTreeItem }} />
}
