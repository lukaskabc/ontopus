import type { FunctionComponent } from 'preact'
import { useEffect, useState } from 'preact/hooks'
import { useLocation } from 'wouter-preact'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import { Container, Paper } from '@mui/material'
import { findVersionSeries } from '@/ontologies/detail/series/actions.ts'
import type { ResourceDetailProps } from '@/ontologies/ResourceEntryList.tsx'
import type { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { parseUri } from '@/ontologies/actions.ts'
import VersionArtifactList from '@/ontologies/detail/series/VersionArtifactList.tsx'
import VersionSeriesResponseDetail from '@/ontologies/detail/series/VersionSeriesResponseDetail.tsx'

export interface VersionSeriesDetailProps extends ResourceDetailProps<VersionSeriesListEntry> {}

export const VersionSeriesDetail: FunctionComponent<VersionSeriesDetailProps> = ({ dataSource, identifier }) => {
  const [__, navigate] = useLocation()
  const versionSeriesUri = parseUri(identifier)

  const [versionSeries, setVersionSeries] = useState<VersionSeriesResponse | null>(null)

  useEffect(() => {
    if (!versionSeriesUri) {
      navigate('/')
      return
    }
    findVersionSeries(versionSeriesUri).then(setVersionSeries)
  }, [dataSource, navigate, versionSeriesUri, setVersionSeries])

  return (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Paper sx={{ p: 2, mb: 2 }}>
        <VersionSeriesResponseDetail versionSeries={versionSeries} />
      </Paper>
      <VersionArtifactList versionSeriesUri={versionSeriesUri} />
    </Container>
  )
}
