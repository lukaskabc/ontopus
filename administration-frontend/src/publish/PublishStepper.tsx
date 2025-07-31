import { useLocation } from 'wouter-preact'
import { Container, Paper, Step, StepLabel, Stepper, Typography } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { StagedForm } from '@/components/staged-form/StagedForm.tsx'
import { loadSourceForm } from '@/publish/actions.ts'
import { useEffect, useState } from 'preact/hooks'
import { trackPromise } from 'react-promise-tracker'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import type StagedJsonForm from '@/model/JsonForm.ts'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'

export default function PublishStepper() {
  const { t } = useTranslation()
  const [_, navigate] = useLocation()
  const [sourceImportForm, setSourceImportForm] = useState<StagedJsonForm | null>(null)
  const importSource = history.state.importSource

  useEffect(() => {
    trackPromise(
      loadSourceForm(importSource).then(
        (data): Promise<StagedJsonForm | null> =>
          new Promise((resolve, reject) => {
            if (data == null) {
              reject(null)
            } else {
              resolve(data)
            }
          })
      ),
      PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA
    )
      .then(setSourceImportForm)
      .catch(console.error)
  }, [importSource])

  if (!history.state?.importSource) {
    navigate('/', { replace: true })
    return <></>
  }

  const steps = ['import', 'process', 'publish'].map((s) => t(`local:publish.step.${s}`))

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
        <Typography variant={'h4'} component={'h1'} sx={{ mb: 2 }}>
          {t(importSource)}
        </Typography>
        <PromiseArea area={PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA}>
          {sourceImportForm && <StagedForm initialForm={sourceImportForm} />}
        </PromiseArea>
      </Paper>
    </Container>
  )
}
