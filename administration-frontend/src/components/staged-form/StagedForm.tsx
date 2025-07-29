import { PromiseArea } from '@/components/PromiseArea.tsx'
import Form from '@rjsf/mui'
import type { FunctionComponent } from 'preact'
import type StagedJsonForm from '@/model/JsonForm.ts'
import validator from '@rjsf/validator-ajv8'
import { useCallback, useMemo } from 'preact/hooks'
import type { FormEvent } from 'react'
import type { IChangeEvent } from '@rjsf/core'
import { useTranslation } from 'react-i18next'
import intlSchema from '@/components/staged-form/intlSchema.ts'
import type { RegistryWidgetsType } from '@rjsf/utils'
import HeadingWidget from '@/components/staged-form/HeadingWidget.tsx'

export interface StagedFormData {
  form: StagedJsonForm
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
export const StagedForm: FunctionComponent<StagedFormData> = ({ form }) => {
  const { i18n } = useTranslation()
  const onSubmit = useCallback(
    ({ formData }: IChangeEvent, e: FormEvent<any>) => {
      console.debug('The form was submitted', formData, e)
    },
    []
  )
  const localizedSchema = useMemo(
    () => intlSchema(form.jsonSchema, i18n),
    [form.jsonSchema]
  )
  
  return (
    // TODO register custom widgets https://rjsf-team.github.io/react-jsonschema-form/docs/api-reference/form-props#widgets
    <PromiseArea area={'stagedForm'}>
      <Form
        schema={localizedSchema}
        uiSchema={form.uiSchema || undefined}
        validator={validator}
        method={'POST'}
        action={form.submitPath}
        liveValidate={true}
        onSubmit={onSubmit}
        widgets={WIDGETS}
      />
    </PromiseArea>
  )
}
