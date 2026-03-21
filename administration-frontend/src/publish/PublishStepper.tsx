import { useTranslation } from 'react-i18next'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useCallback, useEffect, useState } from 'preact/hooks'
import { trackPromise } from '@/utils/hooks.ts'
import { ActionConfirmDialog } from '@/components/ActionConfirmDialog.tsx'
import { StagedForm } from '@/publish/StagedForm.tsx'
import { type RouteComponentProps, type StringRouteParams } from 'wouter-preact'
import { parseUri } from '@/ontologies/actions.ts'
import { resetImportProcess } from '@/publish/actions.ts'
import type { PUBLISH_STEPPER_ROUTE } from '@/Constants.ts'
import Button from '@mui/material/Button'
import DialogContentText from '@mui/material/DialogContentText'
import Paper from '@mui/material/Paper'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'

export type PublishStepperProps = RouteComponentProps<StringRouteParams<typeof PUBLISH_STEPPER_ROUTE>>

export default function PublishStepper({ params }: PublishStepperProps) {
  const { t } = useTranslation()
  const versionSeriesIdentifier = parseUri(params?.versionSeriesIdentifier)

  const [stagedFormElementKey, setStagedFormElementKey] = useState(0)
  const [isAbortDialogOpen, setIsAbortDialogOpen] = useState(false)

  // const steps = ['import', 'process', 'publish'].map((s) => t(`local:publish.step.${s}`))
  // TODO publish steps

  const closeAbortDialog = useCallback(() => setIsAbortDialogOpen(false), [setIsAbortDialogOpen])
  const onAbort = useCallback(() => setIsAbortDialogOpen(true), [setIsAbortDialogOpen])

  // updating the key of an element forces the element to reset its state
  const resetJsonForm = useCallback(() => setStagedFormElementKey((k) => k + 1), [setStagedFormElementKey])

  const onImportProcessReset = useCallback(() => {
    trackPromise(
      resetImportProcess(versionSeriesIdentifier).then(resetJsonForm),
      PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA
    ).then() // TODO error handle
  }, [resetJsonForm, versionSeriesIdentifier])

  const onAbortConfirmed = useCallback(() => {
    closeAbortDialog()
    onImportProcessReset()
  }, [closeAbortDialog, onImportProcessReset])

  // reset import process on version series change
  useEffect(() => {
    onImportProcessReset()
  }, [onImportProcessReset, versionSeriesIdentifier])

  return (
    <>
      {/*<Stepper activeStep={0} sx={{ p: 2 }}>*/}
      {/*  {steps.map((label) => {*/}
      {/*    const stepProps: { completed?: boolean } = {}*/}
      {/*    return (*/}
      {/*      <Step key={label} {...stepProps}>*/}
      {/*        <StepLabel>{label}</StepLabel>*/}
      {/*      </Step>*/}
      {/*    )*/}
      {/*  })}*/}
      {/*</Stepper>*/}

      <Paper sx={{ p: 2 }}>
        <PromiseArea area={PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA}>
          <StagedForm key={'PublishStepper-StagedForm' + stagedFormElementKey} resetForm={onImportProcessReset} />
          <Button
            variant={'outlined'}
            color={'error'}
            style={{ display: 'block', marginLeft: 'auto' }}
            onClick={onAbort}
          >
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
    </>
  )
}
