import { PromiseArea } from '@/components/PromiseArea.tsx'
import Form from '@rjsf/mui'
import type { FunctionComponent } from 'preact'
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
import { Box, Button } from '@mui/material'
import ReusableFileWidget from '@/components/staged-form/ReusableFileWidget.tsx'

const WIDGETS: RegistryWidgetsType = {
  headingWidget: HeadingWidget,
  htmlFileWidget: ReusableFileWidget,
}

/**
 * Supported JSON schema
 * https://json-schema.org/draft-07/json-schema-release-notes
 * https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema
 * @implNote Surround with suspense
 * @constructor
 */
export const StagedForm: FunctionComponent<{}> = () => {
  const { t, i18n } = useTranslation()
  const [jsonSchema, setJsonSchema] = useState<StrictRJSFSchema>()
  const [uiSchema, setUiSchema] = useState<UiSchema>()
  const [formData, setFormData] = useState<any>()
  const [loadScheme, setLoadScheme] = useState<boolean>(true)
  const [isDisabled, setIsDisabled] = useState<boolean>(false)
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
    if (!loadScheme) {
      return
    }

    const { promise, cleanup } = loadJsonForm()

    trackPromise(promise, STAGED_FORM_PROMISE_AREA)
      .then((result) => {
        setJsonSchema(result.jsonSchema)
        setFormData(result.formData)
        setUiSchema(result.uiSchema)
        setFormData(result.formData)
        setIsDisabled(false)
      })
      .catch(console.error) // TODO handle error
      .finally(() => setLoadScheme(false))
    return cleanup
  }, [loadScheme])

  console.debug(jsonSchema, uiSchema, formData)

  const onSubmit = useCallback(({ formData }: IChangeEvent, e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsDisabled(true)
    trackPromise(submitForm(formData), STAGED_FORM_PROMISE_AREA)
      // .then((res) => {
      //   if (res.headers) {
      //     setNextFormUrl(res.headers.get(ONTOPUS_NEXT_FORM_URL_HEADER) || nextFormUrl)
      //   }
      // })
      .then(() => setLoadScheme(true))
      .catch((e) => {
        setIsDisabled(false)
        console.error(e)
      }) // TODO handle and show errors
  }, [])

  const localizedSchema = useMemo(() => intlSchema(jsonSchema, i18n), [jsonSchema])

  return (
    <>
      {localizedSchema && (
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
      )}
      <Box sx={{ my: 4 }}>
        <PromiseArea area={STAGED_FORM_PROMISE_AREA} />
      </Box>
      <Button variant={'outlined'} color={'error'} style={{ float: 'right' }}>
        {t('publish.button.abort')}
      </Button>
    </>
  )
}
