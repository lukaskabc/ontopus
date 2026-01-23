import type { ReusableFile } from '@/model/ReusableFile.ts'
import { useMemo } from 'preact/hooks'
import type { TreeViewDefaultItemModelProperties } from '@mui/x-tree-view/models'
import { RichTreeView } from '@mui/x-tree-view'

export type ReusableFileListProps = {
  files?: ReusableFile[]
}

function findTreeRoot(
  label: string,
  tree: TreeViewDefaultItemModelProperties[]
): TreeViewDefaultItemModelProperties | null {
  for (let rootNode of tree) {
    if (rootNode.label === label) {
      return rootNode
    }
  }

  return null
}

function findTreeNodeChild(
  label: string,
  node: TreeViewDefaultItemModelProperties
): TreeViewDefaultItemModelProperties | null {
  if (node && node.children) {
    for (let child of node.children) {
      if (child.label === label) {
        return child
      }
    }
  }
  return null
}

function insertToTree(file: ReusableFile, tree: TreeViewDefaultItemModelProperties[]) {
  const path = file.fileName?.split('/')
  if (!path || path.length === 0) {
    console.error('Unable to process file', file)
    return
  }

  let currentPath = path[0]
  let current: TreeViewDefaultItemModelProperties | null = findTreeRoot(path[0], tree)
  if (!current) {
    current = { id: path[0], label: path[0], children: [] }
    tree.push(current)
  }
  for (let i = 1; i < path.length; i++) {
    const label = path[i]
    currentPath += '/' + label

    let found = findTreeNodeChild(label, current)
    if (!found) {
      found = { id: currentPath, label, children: [] }
      current.children?.push(found)
    }
    current = found
  }
}

export default function ReusableFileList({ files }: ReusableFileListProps) {
  const treeData: TreeViewDefaultItemModelProperties[] = useMemo(() => {
    const result: TreeViewDefaultItemModelProperties[] = []
    if (files) {
      for (let f of files) {
        insertToTree(f, result)
      }
    }
    return result
  }, [files])

  return <RichTreeView items={treeData} />
}
