import type { ComponentChildren, FunctionalComponent } from 'preact'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogActions from '@mui/material/DialogActions'
import { useTranslation } from 'react-i18next'
import Button, { type ButtonOwnProps } from '@mui/material/Button'
import DialogContent from '@mui/material/DialogContent'

export interface ActionConfirmDialogProps {
  isOpen: boolean
  onConfirm: () => void
  onCancel: () => void
  title: string
  children: ComponentChildren
  positiveText?: string
  positiveColor?: ButtonOwnProps['color']
  negativeText?: string
  negativeColor?: ButtonOwnProps['color']
}

export const ActionConfirmDialog: FunctionalComponent<ActionConfirmDialogProps> = (props) => {
  const { t } = useTranslation()
  const positiveText = props.positiveText || t('confirm')
  const negativeText = props.negativeText || t('cancel')
  const positiveColor = props.positiveColor || 'primary'
  const negativeColor = props.negativeColor || 'primary'
  return (
    <Dialog open={props.isOpen} onClose={props.onCancel}>
      <DialogTitle>{props.title}</DialogTitle>
      <DialogContent>{props.children}</DialogContent>
      <DialogActions>
        <Button onClick={props.onConfirm} color={positiveColor}>
          {positiveText}
        </Button>
        <Button onClick={props.onCancel} color={negativeColor}>
          {negativeText}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
