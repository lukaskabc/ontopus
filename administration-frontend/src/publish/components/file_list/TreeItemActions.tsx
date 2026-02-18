import { type FunctionComponent } from 'preact'
import { IconButton } from '@mui/material'
import DeleteIcon from '@mui/icons-material/Delete'
import type { FormFile } from '@/model/FormFile.ts'

export type WithOnDelete = {
  onDelete: (file: FormFile) => void
}

export type TreeItemActionsProps = {
  file?: FormFile
} & WithOnDelete

type FileProps = {
  file: FormFile
}

const DeleteButton: FunctionComponent<FileProps & WithOnDelete> = ({ file, onDelete }) => {
  const sx = { padding: 0 }
  return (
    <IconButton onClick={onDelete.bind(null, file)} sx={sx}>
      <DeleteIcon />
    </IconButton>
  )
}

export const TreeItemActions: FunctionComponent<TreeItemActionsProps> = ({ file, onDelete }) => {
  if (!file) {
    return null
  }

  return <DeleteButton file={file} onDelete={onDelete} />
}
