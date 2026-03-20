import { useEffect, useState } from 'preact/hooks'
import { parseUri } from '@/ontologies/actions.ts'
import { useLocation } from '@/utils/hooks.ts'
import type { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'
import { findVersionArtifact } from '@/ontologies/detail/artifact/actions.ts'
import VersionArtifactResponseDetail from '@/ontologies/detail/artifact/VersionArtifactResponseDetail.tsx'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'

export interface VersionArtifactDetailProps {
  identifier?: string
  versionSeriesIdentifier: string
}

export default function VersionArtifactDetail({ identifier, versionSeriesIdentifier }: VersionArtifactDetailProps) {
  const { navigate } = useLocation()
  const versionSeriesUri = parseUri(versionSeriesIdentifier)
  const versionArtifactUri = parseUri(identifier)

  const [versionArtifact, setVersionArtifact] = useState<VersionArtifactResponse | null>(null)

  useEffect(() => {
    if (!versionSeriesUri || !versionArtifactUri) {
      navigate('/')
      return
    }
    return findVersionArtifact(versionArtifactUri, versionSeriesUri).then(setVersionArtifact).abort
  }, [navigate, versionSeriesUri, setVersionArtifact, versionArtifactUri])

  return (
    <>
      <Typography variant={'h3'}>Version Artifact</Typography>
      <Paper sx={{ p: 2, mt: 2 }}>
        <VersionArtifactResponseDetail versionArtifact={versionArtifact} />
      </Paper>
    </>
  )
}
