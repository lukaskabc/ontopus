import request from '@/config/rest-client.ts'
import type StagedJsonForm from '@/model/JsonForm.ts'

export function loadSourceForm(importSource: string): Promise<StagedJsonForm> {
  const params = new URLSearchParams({ source: importSource })
  return request('GET', 'import/source/form?' + params).then((res) =>
    res.json()
  )
}
