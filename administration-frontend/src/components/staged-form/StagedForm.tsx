import { PromiseArea } from '@/components/PromiseArea.tsx'
import Form from '@rjsf/mui'
import type { FunctionComponent } from 'preact'
import type StagedJsonForm from '@/model/JsonForm.ts'
import validator from '@rjsf/validator-ajv8'
import { useCallback, useEffect, useMemo, useState } from 'preact/hooks'
import type { FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import intlSchema from '@/components/staged-form/intlSchema.ts'
import type { RegistryWidgetsType, StrictRJSFSchema, UiSchema } from '@rjsf/utils'
import HeadingWidget from '@/components/staged-form/HeadingWidget.tsx'
import { loadNextForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/components/staged-form/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import type { IChangeEvent } from '@rjsf/core'
import { Box } from '@mui/material'

const ONTOPUS_NEXT_FORM_URL_HEADER = 'ONTOPUS-Next-Form-Location'

export interface StagedFormData {
  initialForm: StagedJsonForm
}

const WIDGETS: RegistryWidgetsType = {
  headingWidget: HeadingWidget,
}

function compileDataForRequest(formData: any): string | FormData {
  const data = new FormData()
  Object.keys(formData).forEach((key: string) => {
    data.append(key, formData[key])
  })
  return data
}

/**
 * Supported JSON schema
 * https://json-schema.org/draft-07/json-schema-release-notes
 * https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema
 * @implNote Surround with suspense
 * @constructor
 */
export const StagedForm: FunctionComponent<StagedFormData> = ({ initialForm }) => {
  const { i18n } = useTranslation()
  const [jsonSchema, setJsonSchema] = useState<StrictRJSFSchema>(initialForm.jsonSchema)
  const [uiSchema, setUiSchema] = useState<UiSchema | undefined>(initialForm.uiSchema || undefined)
  const [submitPath, setSubmitPath] = useState<string>(initialForm.submitPath)
  const [nextFormUrl, setNextFormUrl] = useState<string | undefined>(initialForm.nextPath || undefined)
  const [doLoadNext, setDoLoadNext] = useState(false)
  const [initialFormData, setInitialFormData] = useState(initialForm.formData || undefined)
  const [isDisabled, setIsDisabled] = useState<boolean>(false)

  useEffect(() => {
    if (!doLoadNext || !nextFormUrl) {
      return
    }
    setInitialFormData(undefined)
    const { promise, cleanup } = loadNextForm(nextFormUrl)
    trackPromise(promise, STAGED_FORM_PROMISE_AREA)
      .then((nextForm) => {
        setJsonSchema(nextForm.jsonSchema)
        setUiSchema(nextForm.uiSchema)
        setSubmitPath(nextForm.submitPath)
        setNextFormUrl(nextForm.nextPath)
      })
      .catch(console.error) // TODO: show error
      .finally(() => {
        setDoLoadNext(false)
        setIsDisabled(false)
      })
    return cleanup
  }, [doLoadNext, nextFormUrl])

  const onSubmit = useCallback(
    ({ formData }: IChangeEvent, e: FormEvent<HTMLFormElement>) => {
      e.preventDefault()
      setIsDisabled(true)
      const data = compileDataForRequest(formData)
      trackPromise(submitForm(data, submitPath), STAGED_FORM_PROMISE_AREA)
        .then((res) => {
          if (res.headers) {
            setNextFormUrl(res.headers.get(ONTOPUS_NEXT_FORM_URL_HEADER) || nextFormUrl)
          }
        })
        .then(() => setDoLoadNext(true))
        .catch((e) => {
          setIsDisabled(false)
          console.error(e)
        }) // TODO handle and show errors
    },
    [submitPath, nextFormUrl]
  )
  const localizedSchema = useMemo(() => intlSchema(jsonSchema, i18n), [jsonSchema])

  return (
    <>
      <Form
        schema={localizedSchema}
        uiSchema={uiSchema}
        formData={initialFormData}
        validator={validator}
        liveValidate={true}
        onSubmit={onSubmit}
        widgets={WIDGETS}
        disabled={isDisabled}
      />
      <Box sx={{ mt: 4 }}>
        <PromiseArea area={STAGED_FORM_PROMISE_AREA} />
      </Box>
    </>
  )
}
