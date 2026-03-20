import { type JsonForm, makeJsonForm } from '@/model/JsonForm.ts'
import request, { type CancellablePromise } from '@/config/rest-client.ts'
import { compileDataForRequest, type FileWithFieldName } from '@/publish/actions.ts'
import type { GenericObjectType } from '@rjsf/utils'

export function loadSettingsForm(identifier: string): CancellablePromise<JsonForm> {
  return request('GET', '/settings/' + identifier)
    .then((response) => response.json())
    .then(makeJsonForm)
  // TODO error handling?
}

export function submitSettingsForm(identifier: string, formData: GenericObjectType, files: FileWithFieldName[]) {
  return request('POST', `/settings/${identifier}`, { body: compileDataForRequest(formData, files) })
}
