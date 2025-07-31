import type { StrictRJSFSchema, UiSchema } from '@rjsf/utils'

export default interface StagedJsonForm extends JsonForm {
  submitPath: string
  nextPath?: string
}

export interface JsonForm {
  jsonSchema: StrictRJSFSchema
  uiSchema?: UiSchema
}
