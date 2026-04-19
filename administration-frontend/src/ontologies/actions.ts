import request, { type CancellablePromise } from '@/config/rest-client.ts'
import { ArrayPage, Pageable } from '@hallysonh/pageable'
import { toPageRequest } from '@/utils/RequestTypes.ts'
import { MuiModelListEntry, OntopusOptionEntry } from '@/model/MuiModelListEntry.ts'
import type { GenericObjectType } from '@rjsf/utils'
import { UnknownError } from '@/utils/errors.ts'
import { makeJsonForm } from '@/model/JsonForm.ts'
import { compileDataForRequest, type FileWithFieldName } from '@/publish/actions.ts'

export function findAllVersionSeries(
  pageable: Pageable,
  filter?: string[]
): CancellablePromise<ArrayPage<MuiModelListEntry>> {
  const options = toPageRequest(pageable)
  filter?.forEach((val) => options.append('filter', val))
  return request('GET', 'series?' + options.toString())
    .then((response) => response.json())
    .then((data: GenericObjectType) => {
      const page = data.page
      const pageable = new Pageable(page.page, page.size)
      return new ArrayPage<unknown>(data.content, page.totalElements, pageable)
    })
    .then((page) => page.map((data) => new MuiModelListEntry(data)))
}

export function parseBase64Uri(base64EncodedUri?: string) {
  if (base64EncodedUri) {
    const uriEncoded = atob(base64EncodedUri)
    return decodeURIComponent(uriEncoded)
  }
  return null
}

export function encodeBase64Uri(plainUri: string) {
  const uriEncoded = encodeURIComponent(plainUri)
  return btoa(uriEncoded)
}

export function findSeriesOptions(identifiers: string[]): CancellablePromise<Map<string, OntopusOptionEntry[]>> {
  const options = new URLSearchParams()
  identifiers.forEach((id) => options.append('series', id))
  return request('GET', 'series/options?' + options.toString())
    .then((response) => response.json())
    .then((response) => {
      if (typeof response == 'object') {
        const result = new Map<string, OntopusOptionEntry[]>()
        for (const optionId in response) {
          const optionsList = response[optionId]
          if (Array.isArray(optionsList)) {
            const data = (optionsList as GenericObjectType[]).map((option) => new OntopusOptionEntry(option))
            result.set(optionId, data)
          }
        }
        return result
      }
      throw new UnknownError('Unexpected response from server', response)
    })
}

export function loadSeriesOptionForm(series: string, formId: string) {
  const params = new URLSearchParams({ series })
  return request('GET', `/series/options/${formId}?` + params.toString())
    .then((response) => response.json())
    .then(makeJsonForm)
}

export function submitSeriesOptionForm(
  series: string,
  formId: string,
  formData: GenericObjectType,
  files: FileWithFieldName[]
) {
  const params = new URLSearchParams({ series })
  return request('POST', `/series/options/${formId}?` + params.toString(), {
    body: compileDataForRequest(formData, files),
  })
}
