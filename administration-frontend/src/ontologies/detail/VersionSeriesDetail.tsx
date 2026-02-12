import type { FunctionComponent } from 'preact'
import { VersionSeriesDataSourceContext } from '@/ontologies/OntologyDataSource.ts'
import { useContext } from 'preact/hooks'
import { type StringRouteParams, useRoute } from 'wouter-preact'

function parseUri(params: StringRouteParams<any> | null) {
  if (params && params[0]) {
    return decodeURIComponent(params[0])
  }
  return null
}

export const VersionSeriesDetail: FunctionComponent = () => {
  console.debug('version series detail')
  const dataSource = useContext(VersionSeriesDataSourceContext)!
  console.assert(dataSource != null)
  const [_, params] = useRoute('/*')
  const ontologyURI = parseUri(params)

  return <></>
}
