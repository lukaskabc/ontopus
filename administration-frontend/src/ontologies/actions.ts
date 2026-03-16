import request from '@/config/rest-client.ts'
import { ArrayPage, Pageable } from '@hallysonh/pageable'
import { toPageRequest } from '@/utils/RequestTypes.ts'
import { MuiModelListEntry, OntopusOptionEntry } from '@/model/MuiModelListEntry.ts'
import type { GenericObjectType } from '@rjsf/utils'
import { UnknownError } from '@/utils/errors.ts'

export function fetchImportSources() {
  return request('GET', 'import/source').then((response) => response.json())
}

export function findAllVersionSeries(pageable: Pageable, filter?: string[]): Promise<ArrayPage<MuiModelListEntry>> {
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

export function parseUri(identifier?: string) {
  if (identifier) {
    return decodeURIComponent(identifier)
  }
  return null
}

export function findSeriesOptions(identifiers: string[]): Promise<Map<string, OntopusOptionEntry[]>> {
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
            const data = (optionsList as Array<GenericObjectType>).map((option) => new OntopusOptionEntry(option))
            result.set(optionId, data)
          }
        }
        return result
      }
      throw new UnknownError('Unexpected response from server', response)
    })
}
