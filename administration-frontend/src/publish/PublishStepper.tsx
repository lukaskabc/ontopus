import { Container, Paper, Step, StepLabel, Stepper } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { StagedForm } from '@/components/staged-form/StagedForm.tsx'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'

export default function PublishStepper() {
  const { t } = useTranslation()

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
        <PromiseArea area={PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA}>
          <StagedForm />
        </PromiseArea>
      </Paper>
    </Container>
  )
}
