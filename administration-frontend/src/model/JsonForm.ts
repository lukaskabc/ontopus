import type { GenericObjectType, StrictRJSFSchema, UiSchema } from '@rjsf/utils'

export interface JsonForm {
  jsonSchema: StrictRJSFSchema
  uiSchema?: UiSchema
  formData?: GenericObjectType
}

export function makeJsonForm(json: GenericObjectType): JsonForm {
  if (json['jsonSchema']) {
    const jsonSchema = json['jsonSchema']
    const uiSchema = json['uiSchema'] || undefined
    const formData = json['formData'] || undefined
    return {
      jsonSchema,
      uiSchema,
      formData,
    }
  }
  throw new Error('Unable to construct JsonForm from ' + json)
}
