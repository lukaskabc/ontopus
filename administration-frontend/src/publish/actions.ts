import request from '@/config/rest-client.ts'
import type StagedJsonForm from '@/model/JsonForm.ts'

export async function loadSourceForm(importSource: string): Promise<StagedJsonForm> {
  const encoded = encodeURIComponent(importSource)
  const res = await request('GET', `import/source/${encoded}`)
  return await res.json()
}
