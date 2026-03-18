import { type FunctionComponent } from 'preact'
import DeleteIcon from '@mui/icons-material/Delete'
import type { FormFile } from '@/model/FormFile.ts'
import IconButton from '@mui/material/IconButton'

export interface WithOnDelete {
  onDelete: (file: FormFile) => void
}

export interface TreeItemActionsProps extends WithOnDelete {
  file?: FormFile
}

interface FileProps {
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
