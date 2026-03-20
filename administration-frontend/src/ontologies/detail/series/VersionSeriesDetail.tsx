import { useEffect, useState } from 'preact/hooks'
import { useLocation } from '@/utils/hooks.ts'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import { findVersionSeries } from '@/ontologies/detail/series/actions.ts'
import { parseUri } from '@/ontologies/actions.ts'
import VersionArtifactList from '@/ontologies/detail/series/VersionArtifactList.tsx'
import VersionSeriesResponseDetail from '@/ontologies/detail/series/VersionSeriesResponseDetail.tsx'

import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'

export interface VersionSeriesDetailProps {
  identifier?: string
}

export default function VersionSeriesDetail({ identifier }: VersionSeriesDetailProps) {
  const { navigate } = useLocation()
  const versionSeriesUri = parseUri(identifier)

  const [versionSeries, setVersionSeries] = useState<VersionSeriesResponse | null>(null)

  useEffect(() => {
    if (!versionSeriesUri) {
      navigate('/')
      return
    }
    return findVersionSeries(versionSeriesUri).then(setVersionSeries).abort
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
