import type { RJSFSchema, UiSchema } from '@rjsf/utils'

export default interface StagedJsonForm extends JsonForm {
  submitPath: string
  nextPath?: string
}

export interface JsonForm {
  jsonSchema: RJSFSchema
  uiSchema?: UiSchema
}
