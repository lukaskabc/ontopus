import { type FunctionComponent } from 'preact'
import CloudUploadIcon from '@mui/icons-material/CloudUpload'
import CloudDoneIcon from '@mui/icons-material/CloudDone'
import { ActionAwareReusableFile } from '@/model/ReusableFile.ts'
import type { OverridableComponent } from '@mui/material/OverridableComponent'
import { IconButton, type SvgIconPropsColorOverrides, Tooltip } from '@mui/material'
import { useTranslation } from 'react-i18next'
import ClearIcon from '@mui/icons-material/Clear'
import DeleteIcon from '@mui/icons-material/Delete'
import UndoIcon from '@mui/icons-material/Undo'
import { useCallback } from 'preact/hooks'

export type WithOnDelete = {
  onDelete: (file: ActionAwareReusableFile) => void
}

export type TreeItemActionsProps = {
  file?: ActionAwareReusableFile
} & WithOnDelete

type FileProps = {
  file: ActionAwareReusableFile
}

const StateIcon: FunctionComponent<FileProps> = ({ file }) => {
  const { t } = useTranslation()
  let Icon: OverridableComponent<any> | null = null
  let color: SvgIconPropsColorOverrides = 'primary'
  let tooltipKey: string = 'publish.reusable-file.action.tooltip.'
  if (file.isUploadAvailable) {
    Icon = CloudUploadIcon
    tooltipKey += 'upload'
  } else if (file.isDeleted) {
    Icon = ClearIcon
    tooltipKey += 'delete'
  } else if (file.isServerAvailable) {
    Icon = CloudDoneIcon
    tooltipKey += 'server'
  }
  if (Icon) {
    return (
      <Tooltip title={t(tooltipKey)}>
        <Icon color={color} />
      </Tooltip>
    )
  }

  return null
}

const DeleteRestoreButton: FunctionComponent<FileProps & WithOnDelete> = ({ file, onDelete }) => {
  const onRestore = useCallback(() => {
    file.restore()
  }, [file])
  const sx = { padding: 0 }
  if (file.isDeleted && file.isServerAvailable) {
    return (
      <IconButton onClick={onRestore} sx={sx}>
        <UndoIcon />
      </IconButton>
    )
  }
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

  return (
    <>
      <StateIcon file={file} />
      <DeleteRestoreButton file={file} onDelete={onDelete} />
    </>
  )
}
