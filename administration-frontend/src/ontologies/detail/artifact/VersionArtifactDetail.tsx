import type { FunctionComponent } from 'preact'
import { useEffect, useState } from 'preact/hooks'
import { parseUri } from '@/ontologies/actions.ts'
import { useLocation } from 'wouter-preact'
import type { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'
import { findVersionArtifact } from '@/ontologies/detail/artifact/actions.ts'
import { Paper, Typography } from '@mui/material'
import VersionArtifactResponseDetail from '@/ontologies/detail/artifact/VersionArtifactResponseDetail.tsx'

export interface VersionArtifactDetailProps {
  identifier?: string
  versionSeriesIdentifier: string
}

export const VersionArtifactDetail: FunctionComponent<VersionArtifactDetailProps> = ({
  identifier,
  versionSeriesIdentifier,
}) => {
  const [__, navigate] = useLocation()
  const versionSeriesUri = parseUri(versionSeriesIdentifier)
  const versionArtifactUri = parseUri(identifier)

  const [versionArtifact, setVersionArtifact] = useState<VersionArtifactResponse | null>(null)

  useEffect(() => {
    if (!versionSeriesUri || !versionArtifactUri) {
      navigate('/')
      return
    }
    findVersionArtifact(versionArtifactUri, versionSeriesUri).then(setVersionArtifact)
  }, [navigate, versionSeriesUri, setVersionArtifact])

  return (
    <>
      <Typography variant={'h3'}>Version Artifact</Typography>
      <Paper sx={{ p: 2, mt: 2 }}>
        <VersionArtifactResponseDetail versionArtifact={versionArtifact} />
      </Paper>
    </>
  )
}
