import { useLocation } from 'wouter-preact'
import { Container, Paper, Step, StepLabel, Stepper } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { useCallback, useEffect, useState } from 'preact/hooks'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import type { JsonForm } from '@/model/JsonForm.ts'
import { trackPromise } from 'react-promise-tracker'
import { loadJsonForm, resetImportProcess } from '@/components/staged-form/actions.ts'
import { StagedForm } from '@/components/staged-form/StagedForm.tsx'

const PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA = 'PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA'

export default function PublishStepper() {
  const { t } = useTranslation()
  const [_, navigate] = useLocation()
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [doLoadJsonForm, setDoLoadJsonForm] = useState<boolean>(true)
  /*
  {
    submitPath: '',
    jsonSchema: JSON.parse(
      '{"$schema": "http://json-schema.org/draft-07/schema#","type": "object","$translationRoot": "ontopus.plugin.git.importForm","properties": {"repositoryUrl": {"type": "string","format": "uri"},"branch": {"type": "string"},"authText": {"type": "string"},"username": {"type": "string"},"password": {"type": "string"}},"required": ["repositoryUrl"],"dependencies": {"password": ["username"],"username": ["password"]}}'
    ),
  }
   */

  // load JSON form when doLoadJsonForm is true
  // if the endpoint returns 205, initialize new import process and set doLoadJsonForm to true again
  useEffect(() => {
    // TODO remove and move to the StagedForm
    if (!doLoadJsonForm) {
      return
    }
    setDoLoadJsonForm(false)

    trackPromise(
      loadJsonForm(), // TODO error?
      PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA
    )
      .then(setJsonForm)
      .catch((e) => {
        if (e?.status === 205) {
          // 205 Reset Content
          trackPromise(
            resetImportProcess(), // TODO error?
            PUBLISH_STEPPER_IMPORT_FORM_PROMISE_AREA
          )
            .then(() => setDoLoadJsonForm(true))
            .catch(console.error)
        } else {
          console.error(e)
        }
      })
  }, [doLoadJsonForm])

  const refreshForm = useCallback(() => setDoLoadJsonForm(true), [doLoadJsonForm])

  console.debug(jsonForm)

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
          {jsonForm && <StagedForm jsonForm={jsonForm} refreshForm={refreshForm} />}
        </PromiseArea>
      </Paper>
    </Container>
  )
}
