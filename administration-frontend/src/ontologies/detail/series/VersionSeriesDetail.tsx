import type { FunctionComponent } from 'preact'
import { useEffect, useState } from 'preact/hooks'
import { useLocation } from 'wouter-preact'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import { Paper, Typography } from '@mui/material'
import { findVersionSeries } from '@/ontologies/detail/series/actions.ts'
import { parseUri } from '@/ontologies/actions.ts'
import VersionArtifactList from '@/ontologies/detail/series/VersionArtifactList.tsx'
import VersionSeriesResponseDetail from '@/ontologies/detail/series/VersionSeriesResponseDetail.tsx'

export interface VersionSeriesDetailProps {
  identifier?: string
}

export const VersionSeriesDetail: FunctionComponent<VersionSeriesDetailProps> = ({ identifier }) => {
  const [__, navigate] = useLocation()
  const versionSeriesUri = parseUri(identifier)

  const [versionSeries, setVersionSeries] = useState<VersionSeriesResponse | null>(null)

  useEffect(() => {
    if (!versionSeriesUri) {
      navigate('/')
      return
    }
    findVersionSeries(versionSeriesUri).then(setVersionSeries)
  }, [navigate, versionSeriesUri, setVersionSeries])

  return (
    <>
      <Typography variant={'h3'}>Version Series</Typography>
      <Paper sx={{ p: 2, mt: 2, mb: 5 }}>
        <VersionSeriesResponseDetail versionSeries={versionSeries} />
      </Paper>
      <VersionArtifactList versionSeriesUri={versionSeriesUri} />
    </>
  )
}
