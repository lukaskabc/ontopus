import type { JsonForm } from '@/model/JsonForm.ts'
import { useCallback, useEffect, useMemo, useState } from 'preact/hooks'
import intlSchema from '@/publish/utils/intlSchema.ts'
import { useTranslation } from 'react-i18next'
import Form from '@rjsf/mui'
import validator from '@rjsf/validator-ajv8'
import type {
  GenericObjectType,
  RegistryFieldsType,
  RegistryWidgetsType,
  StrictRJSFSchema,
  UiSchema,
} from '@rjsf/utils'
import type { default as RjsfForm, IChangeEvent } from '@rjsf/core'
import { type FileWithFieldName } from '@/publish/actions.ts'
import { createRef, type RefObject, type TargetedEvent } from 'preact'
import TypographyField from '@/publish/fields/TypographyField.tsx'
import './JsonFormElement.scss'
import FileField from '@/publish/fields/FileField.tsx'

import VersionUriField from '@/publish/fields/VersionUriField.tsx'
import MultilingualStringField from '@/publish/fields/MultilingualStringField.tsx'
import AutocompleteWidget from '@/publish/widgets/AutocompleteWidget.tsx'

const WIDGETS: RegistryWidgetsType = {
  autocompleteWidget: AutocompleteWidget,
}

const FIELDS: RegistryFieldsType = {
  fileField: FileField,
  versionUriField: VersionUriField,
  multilingualStringField: MultilingualStringField,
  typographyField: TypographyField,
}

function resolveFiles(form: RjsfForm | null): FileWithFieldName[] {
  const fileList: FileWithFieldName[] = []
  const formElement = form?.formElement as RefObject<HTMLElement>
  if (formElement?.current) {
    const fileInputs = formElement.current.querySelectorAll('input[type="file"]')
    fileInputs.forEach((input) => {
      const name = input.getAttribute('name') || ''
      const fInput = input as HTMLInputElement
      if (fInput?.files) {
        for (const file of fInput.files as FileList) {
          fileList.push({ name, file })
        }
      }
    })
  }
  return fileList
}

export interface JsonFormElementProps {
  jsonForm: JsonForm | null
  onSubmit: (formData: GenericObjectType, files: FileWithFieldName[]) => Promise<unknown>
}

/**
 * Supported JSON schema
 * https://json-schema.org/draft-07/json-schema-release-notes
 * https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/uiSchema
 */
export default function JsonFormElement({ jsonForm, onSubmit }: JsonFormElementProps) {
  const { i18n } = useTranslation()
  const [jsonSchema, setJsonSchema] = useState<StrictRJSFSchema>()
  const [uiSchema, setUiSchema] = useState<UiSchema>()
  const [formData, setFormData] = useState<GenericObjectType>()
  const [isDisabled, setIsDisabled] = useState(false)
  const [formElementKey, setFormElementKey] = useState(0)
  const formRef = createRef<RjsfForm>()

  useEffect(() => {
    if (!jsonForm) {
      return
    }
    setJsonSchema(jsonForm.jsonSchema)
    setUiSchema(jsonForm.uiSchema)
    setFormData(jsonForm.formData)
    // force form element reset
    setFormElementKey((k) => k + 1)
  }, [jsonForm])

  const localizedSchema = useMemo(() => intlSchema(jsonSchema, i18n), [i18n, jsonSchema])

  // using controlled form for form data propagation when the new JSON form and possibly new form data are loaded
  const onChange = useCallback(
    (e: IChangeEvent) => {
      setFormData(e.formData)
    },
    [setFormData]
  )

  const onFormSubmit = useCallback(
    ({ formData }: IChangeEvent, e: TargetedEvent<HTMLFormElement>) => {
      e.preventDefault()
      setIsDisabled(true)
      const fileList = resolveFiles(formRef.current)

      onSubmit(formData, fileList).finally(() => {
        setIsDisabled(false)
      })
    },
    [formRef, onSubmit]
  )

  const uiSchemaWithOptionsOverrides = useMemo(() => {
    const newUiSchema = Object.assign({}, uiSchema)
    newUiSchema['ui:submitButtonOptions'] = {
      props: {
        className: 'JsonFormElement-Form-Submit',
      },
    }
    return newUiSchema
  }, [uiSchema])

  return (
    <>
      {jsonForm && localizedSchema && (
        <Form
          key={formElementKey}
          ref={formRef}
          schema={localizedSchema}
          uiSchema={uiSchemaWithOptionsOverrides}
          formData={formData}
          onChange={onChange}
          validator={validator}
          liveValidate={true}
          onSubmit={onFormSubmit}
          widgets={WIDGETS}
          fields={FIELDS}
          disabled={isDisabled}
        />
      )}
    </>
  )
}
