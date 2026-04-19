import { useTranslation } from 'react-i18next'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useCallback, useEffect, useState } from 'preact/hooks'
import { trackPromise } from '@/utils/hooks.ts'
import { ActionConfirmDialog } from '@/components/ActionConfirmDialog.tsx'
import { StagedForm } from '@/publish/StagedForm.tsx'
import { type RouteComponentProps, type StringRouteParams } from 'wouter-preact'
import { parseBase64Uri } from '@/ontologies/actions.ts'
import { resetImportProcess } from '@/publish/actions.ts'
import type { PUBLISH_STEPPER_ROUTE } from '@/Constants.ts'
import Button from '@mui/material/Button'
import DialogContentText from '@mui/material/DialogContentText'
import Paper from '@mui/material/Paper'
import AlertErrorsStack, { useThrowError } from '@/components/AlertErrorsStack.tsx'
import Stack from '@mui/material/Stack'
import Typography from '@mui/material/Typography'
import ReadOnlyField from '@/components/ReadOnlyField.tsx'
import { useSessionStorageState } from '@toolpad/core'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'
const LOCAL_STORAGE_PUBLISH_STEPPER_VERSION_SERIES_URI_KEY = 'LOCAL_STORAGE_PUBLISH_STEPPER_VERSION_SERIES_URI_KEY'

export type PublishStepperProps = RouteComponentProps<StringRouteParams<typeof PUBLISH_STEPPER_ROUTE>>

export default function PublishStepper({ params }: PublishStepperProps) {
  const { t } = useTranslation()
  const versionSeriesIdentifier = parseBase64Uri(params?.versionSeriesIdentifier)

  const [stagedFormElementKey, setStagedFormElementKey] = useState(0)
  const [isAbortDialogOpen, setIsAbortDialogOpen] = useState(false)
  const [lastVersionSeriesIdentifier, setLastVersionSeriesIdentifier] = useSessionStorageState<string | null>(
    LOCAL_STORAGE_PUBLISH_STEPPER_VERSION_SERIES_URI_KEY,
    null
  )

  const throwError = useThrowError()
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
    ).catch(throwError)
  }, [resetJsonForm, versionSeriesIdentifier, throwError])

  const onAbortConfirmed = useCallback(() => {
    closeAbortDialog()
    onImportProcessReset()
  }, [closeAbortDialog, onImportProcessReset])

  // reset import process on version series change
  useEffect(() => {
    if (versionSeriesIdentifier != lastVersionSeriesIdentifier) {
      setLastVersionSeriesIdentifier(versionSeriesIdentifier)
      onImportProcessReset()
    }
  }, [onImportProcessReset, versionSeriesIdentifier, lastVersionSeriesIdentifier, setLastVersionSeriesIdentifier])

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
        <Stack direction={'column'} sx={{ mb: 2 }}>
          <Typography variant={'h4'} component={'h4'}>
            {t(lastVersionSeriesIdentifier == null ? 'publish.new-ontology' : 'publish.existing-ontology')}
          </Typography>
          {lastVersionSeriesIdentifier && (
            <ReadOnlyField
              label={t('entity.dataset.detail.dcat-identifier')}
              value={lastVersionSeriesIdentifier}
              isCode={true}
            />
          )}
        </Stack>
        <AlertErrorsStack>
          <PromiseArea area={PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA}>
            <StagedForm key={'PublishStepper-StagedForm' + stagedFormElementKey} resetForm={onImportProcessReset}>
              <Button
                variant={'outlined'}
                color={'error'}
                style={{ display: 'block', marginLeft: 'auto' }}
                onClick={onAbort}
              >
                {t('publish.button.abort')}
              </Button>
            </StagedForm>
          </PromiseArea>
        </AlertErrorsStack>
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
