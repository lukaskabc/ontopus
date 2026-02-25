import { PromiseArea } from '@/components/PromiseArea.tsx'
import Form from '@rjsf/mui'
import type { IChangeEvent } from '@rjsf/core'
import RjsfForm from '@rjsf/core'
import { createRef, type FunctionComponent, type RefObject, type TargetedEvent } from 'preact'
import validator from '@rjsf/validator-ajv8'
import { useCallback, useEffect, useMemo, useState } from 'preact/hooks'
import { useTranslation } from 'react-i18next'
import type { RegistryFieldsType, RegistryWidgetsType, StrictRJSFSchema, UiSchema } from '@rjsf/utils'
import { type FileWithFieldName, loadJsonForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/publish/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import { Box } from '@mui/material'
import intlSchema from '@/publish/utils/intlSchema.ts'
import HeadingWidget from '@/publish/widgets/HeadingWidget.tsx'
import FileField from '@/publish/fields/FileField.tsx'
import VersionUriField from '@/publish/fields/VersionUriField.tsx'

const WIDGETS: RegistryWidgetsType = {
  headingWidget: HeadingWidget,
}

const FIELDS: RegistryFieldsType = {
  fileField: FileField,
  versionUriField: VersionUriField,
}

function resolveFiles(form: RjsfForm | null): FileWithFieldName[] {
  const fileList: FileWithFieldName[] = []
  const formElement = form?.formElement as RefObject<HTMLElement>
  if (formElement?.current) {
    const fileInputs = formElement.current.querySelectorAll('input[type="file"]')
    fileInputs.forEach((input) => {
      const name = input.getAttribute('name') || ''
      const fInput = input as any
      if (fInput?.files) {
        for (const file of fInput.files as FileList) {
          fileList.push({ name, file })
        }
      }
    })
  }
  return fileList
}

export type StagedFormProps = {
  doRefresh: RefObject<() => void>
}

/**
 * Supported JSON schema
 * https://json-schema.org/draft-07/json-schema-release-notes
 * https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema
 * @implNote Surround with suspense
 * @constructor
 */
export const StagedForm: FunctionComponent<StagedFormProps> = ({ doRefresh }) => {
  const { i18n } = useTranslation()
  const [jsonSchema, setJsonSchema] = useState<StrictRJSFSchema>()
  const [uiSchema, setUiSchema] = useState<UiSchema>()
  const [formData, setFormData] = useState<any>()
  const [loadScheme, setLoadScheme] = useState<boolean>(true)
  const [isDisabled, setIsDisabled] = useState<boolean>(false)
  const formRef = createRef<RjsfForm>()

  useEffect(() => {
    doRefresh.current = () => setLoadScheme(true)
  }, [doRefresh, setLoadScheme])

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
        setUiSchema(result.uiSchema)
        setFormData(result.formData)
        setIsDisabled(false)
      })
      .catch((e) => {
        if (e == null) return
        console.error(e)
      }) // TODO handle error
      .finally(() => setLoadScheme(false))
    return cleanup
  }, [loadScheme, formRef])

  const onSubmit = useCallback(
    ({ formData }: IChangeEvent, e: TargetedEvent<HTMLFormElement>) => {
      e.preventDefault()
      setIsDisabled(true)
      const fileList = resolveFiles(formRef.current)

      trackPromise(submitForm(formData, fileList), STAGED_FORM_PROMISE_AREA)
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
    },
    [formRef]
  )

  const localizedSchema = useMemo(() => intlSchema(jsonSchema, i18n), [jsonSchema])

  // using controlled form for form data propagation when the new JSON form and possibly new form data are loaded
  const onChange = useCallback(
    (e: IChangeEvent) => {
      setFormData(e.formData)
    },
    [setFormData]
  )

  return (
    <>
      {localizedSchema && (
        <Form
          ref={formRef}
          schema={localizedSchema}
          uiSchema={uiSchema}
          formData={formData}
          onChange={onChange}
          validator={validator}
          liveValidate={true}
          onSubmit={onSubmit}
          widgets={WIDGETS}
          fields={FIELDS}
          disabled={isDisabled}
        />
      )}
      <Box sx={{ my: 4 }}>
        <PromiseArea area={STAGED_FORM_PROMISE_AREA} />
      </Box>
    </>
  )
}
