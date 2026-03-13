import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'
import request from '@/config/rest-client.ts'
import { compileDataForRequest, type FileWithFieldName } from '@/publish/actions.ts'

export function loadSettingsForm(identifier: string): Promise<JsonForm> {
  return request('GET', '/settings/' + identifier).then(makeJsonForm)
  // TODO error handling?
}

export function submitSettingsForm(identifier: string, formData: any, files: FileWithFieldName[]) {
  return request('POST', '/settings/' + identifier, { body: compileDataForRequest(formData, files) })
}
