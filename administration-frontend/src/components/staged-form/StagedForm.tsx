import { PromiseArea } from '@/components/PromiseArea.tsx'
import Form from '@rjsf/mui'
import type { FunctionComponent } from 'preact'
import type { JsonForm } from '@/model/JsonForm.ts'
import validator from '@rjsf/validator-ajv8'
import { useCallback, useEffect, useMemo, useState } from 'preact/hooks'
import type { FormEvent } from 'react'
import { useTranslation } from 'react-i18next'
import intlSchema from '@/components/staged-form/intlSchema.ts'
import type { RegistryWidgetsType, StrictRJSFSchema, UiSchema } from '@rjsf/utils'
import HeadingWidget from '@/components/staged-form/HeadingWidget.tsx'
import { loadJsonForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/components/staged-form/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import type { IChangeEvent } from '@rjsf/core'
import { Box } from '@mui/material'

const ONTOPUS_NEXT_FORM_URL_HEADER = 'ONTOPUS-Next-Form-Location'

export interface StagedFormData {
  jsonForm: JsonForm
  refreshForm: () => void
}

const WIDGETS: RegistryWidgetsType = {
  headingWidget: HeadingWidget,
}

/**
 * Supported JSON schema
 * https://json-schema.org/draft-07/json-schema-release-notes
 * https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema
 * @implNote Surround with suspense
 * @constructor
 */
export const StagedForm: FunctionComponent<StagedFormData> = ({ jsonForm }) => {
  const { i18n } = useTranslation()
  const [jsonSchema, setJsonSchema] = useState<StrictRJSFSchema>(jsonForm.jsonSchema)
  const [uiSchema, setUiSchema] = useState<UiSchema | undefined>(jsonForm.uiSchema || undefined)
  const [formData, setFormData] = useState<any | undefined>(jsonForm.formData || undefined)
  const [doLoadNext, setDoLoadNext] = useState<boolean>(false)
  const [isDisabled, setIsDisabled] = useState<boolean>(false)

  useEffect(() => {
    if (!doLoadNext) {
      return
    }
    setDoLoadNext(false)
    trackPromise(loadJsonForm(), STAGED_FORM_PROMISE_AREA)
      .then((form) => {
        setJsonSchema(form.jsonSchema)
        setUiSchema(form.uiSchema)
        setFormData(form.formData)
      })
      .catch((e) => {
        setDoLoadNext(true)
        console.error(e)
      })
  }, [doLoadNext])

  const onSubmit = useCallback(({ formData }: IChangeEvent, e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsDisabled(true)
    trackPromise(submitForm(formData), STAGED_FORM_PROMISE_AREA)
      // .then((res) => {
      //   if (res.headers) {
      //     setNextFormUrl(res.headers.get(ONTOPUS_NEXT_FORM_URL_HEADER) || nextFormUrl)
      //   }
      // })
      .then(() => setDoLoadNext(true))
      .catch((e) => {
        setIsDisabled(false)
        console.error(e)
      }) // TODO handle and show errors
  }, [])

  const localizedSchema = useMemo(() => intlSchema(jsonSchema, i18n), [jsonSchema])

  return (
    <>
      <Form
        schema={localizedSchema}
        uiSchema={uiSchema}
        formData={formData}
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
