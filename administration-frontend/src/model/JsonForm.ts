import type { StrictRJSFSchema, UiSchema } from '@rjsf/utils'

export interface JsonForm {
  jsonSchema: StrictRJSFSchema
  uiSchema?: UiSchema
  formData?: any
}

export function makeJsonForm(json: any): JsonForm {
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
