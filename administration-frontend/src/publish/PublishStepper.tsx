import { Button, Container, DialogContentText, Paper, Step, StepLabel, Stepper } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useCallback, useRef, useState } from 'preact/hooks'
import { resetImportProcess } from '@/publish/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import { ActionConfirmDialog } from '@/components/ActionConfirmDialog.tsx'
import { StagedForm } from '@/publish/StagedForm.tsx'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'

export default function PublishStepper() {
  const { t } = useTranslation()
  const refreshRef = useRef<() => void>(null)
  const [isAbortDialogOpen, setIsAbortDialogOpen] = useState(false)

  const steps = ['import', 'process', 'publish'].map((s) => t(`local:publish.step.${s}`))

  const closeAbortDialog = useCallback(() => setIsAbortDialogOpen(false), [setIsAbortDialogOpen])
  const onAbort = () => setIsAbortDialogOpen(true)
  const onAbortConfirmed = useCallback(() => {
    closeAbortDialog()
    trackPromise(
      resetImportProcess().then(() => {
        if (refreshRef.current) {
          refreshRef.current()
        } else {
          console.error('Failed to refresh the form, missing reference.')
        }
      }),
      PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA
    ).then() // TODO error handle
  }, [refreshRef])

  return (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Stepper activeStep={0} sx={{ p: 2 }}>
        {steps.map((label, _) => {
          const stepProps: { completed?: boolean } = {}
          return (
            <Step key={label} {...stepProps}>
              <StepLabel>{label}</StepLabel>
            </Step>
          )
        })}
      </Stepper>

      <Paper className={'height-100'} sx={{ p: 2 }}>
        <PromiseArea area={PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA}>
          <StagedForm doRefresh={refreshRef} />
          <Button variant={'outlined'} color={'error'} style={{ float: 'right' }} onClick={onAbort}>
            {t('publish.button.abort')}
          </Button>
        </PromiseArea>
      </Paper>

      <ActionConfirmDialog
        isOpen={isAbortDialogOpen}
        onConfirm={onAbortConfirmed}
        onCancel={closeAbortDialog}
        title={t('publish.abort-dialog.title')}
      >
        <DialogContentText>{t('publish.abort-dialog.text')}</DialogContentText>
      </ActionConfirmDialog>
    </Container>
  )
}
