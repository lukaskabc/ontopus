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
import { ItemTypeIcon } from '@/publish/components/file_list/TreeItemTypeIcon.tsx'
import { TreeItemActions } from '@/publish/components/file_list/TreeItemActions.tsx'
import { type ItemType, ItemTypeEnum } from '@/publish/components/file_list/ItemType.ts'
import type { FormFile } from '@/model/FormFile.ts'
import Box from '@mui/material/Box'

export interface FormFileListProps {
  files: FormFile[]
  onDelete: (file: FormFile) => void
}

interface ExtendedItemProperties extends TreeViewDefaultItemModelProperties {
  type: ItemType
  file?: FormFile
  onDelete: (file: FormFile) => void
  children?: ExtendedItemProperties[]
}

function findTreeRoot(label: string, tree: ExtendedItemProperties[]): ExtendedItemProperties | null {
  for (const rootNode of tree) {
    if (rootNode.label === label) {
      return rootNode
    }
  }

  return null
}

function findTreeNodeChild(label: string, node: ExtendedItemProperties): ExtendedItemProperties | null {
  if (node && node.children) {
    for (const child of node.children) {
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

function insertToTree(file: FormFile, tree: ExtendedItemProperties[], onDelete: (file: FormFile) => void) {
  const path = splitPath(file.path)
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
    current = { id: label, label, children: [], type, onDelete, file: fileValue }
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
      found = { id: currentPath, label, children: [], type, onDelete, file: fileValue }
      current.children?.push(found)
    }
    current = found
  }
}

/**
 * Tree list of files
 * <p>
 * Allows to list and remove selected files
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

  const model = useTreeItemModel<ExtendedItemProperties>(itemId)

  if (!model) return null

  const { type, file, onDelete } = model

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

        <TreeItemContent {...getContentProps()} sx={{ paddingLeft: '14px' }}>
          <TreeItemLabel {...getLabelProps()} />
          {type === ItemTypeEnum.FILE && <TreeItemActions file={file} onDelete={onDelete} />}
          <TreeItemDragAndDropOverlay {...getDragAndDropOverlayProps()} />
        </TreeItemContent>
        {children && <TreeItemGroupTransition {...getGroupTransitionProps()} />}
      </TreeItemRoot>
    </TreeItemProvider>
  )
})

export default function FormFileList({ files, onDelete }: FormFileListProps) {
  const treeData: ExtendedItemProperties[] = useMemo(() => {
    const result: ExtendedItemProperties[] = []

    if (files) {
      for (const f of files) {
        insertToTree(f, result, onDelete)
      }
    }
    return result
  }, [files, onDelete])

  return <RichTreeView items={treeData} slots={{ item: CustomTreeItem }} />
}
