import type { StrictRJSFSchema, UiSchema } from '@rjsf/utils'

export default interface StagedJsonForm extends JsonForm {
  submitPath: string
  nextPath?: string
}

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

export function makeStagedJsonFormAsync(jsonPromise: Promise<any>) {
  return jsonPromise.then(makeStagedJsonForm)
}

export function makeStagedJsonForm(json: any): StagedJsonForm {
  if (json['submitPath'] && json['jsonSchema']) {
    const submitPath = json['submitPath']
    const nextPath = json['nextPath'] || undefined
    return {
      submitPath,
      nextPath,
      ...makeJsonForm(json),
    }
  }
  throw new Error('Unable to construct StagedJsonForm from ' + json)
}
