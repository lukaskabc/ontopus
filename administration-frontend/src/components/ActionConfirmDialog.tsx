import type { ComponentChildren, FunctionalComponent } from 'preact'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import { Button, DialogContent } from '@mui/material'
import DialogActions from '@mui/material/DialogActions'
import { useTranslation } from 'react-i18next'

export type ActionConfirmDialogProps = {
  isOpen: boolean
  onConfirm: () => void
  onCancel: () => void
  title: string
  children: ComponentChildren
  positiveText?: string
  negativeText?: string
}

export const ActionConfirmDialog: FunctionalComponent<ActionConfirmDialogProps> = (props) => {
  const { t } = useTranslation()
  const positiveText = props.positiveText || t('confirm')
  const negativeText = props.negativeText || t('cancel')
  return (
    <Dialog open={props.isOpen} onClose={props.onCancel}>
      <DialogTitle>{props.title}</DialogTitle>
      <DialogContent>{props.children}</DialogContent>
      <DialogActions>
        <Button onClick={props.onConfirm}>{positiveText}</Button>
        <Button onClick={props.onCancel}>{negativeText}</Button>
      </DialogActions>
    </Dialog>
  )
}
