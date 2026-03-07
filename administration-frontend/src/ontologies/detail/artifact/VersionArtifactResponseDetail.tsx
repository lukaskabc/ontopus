import type { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'
import DatasetResponseDetail from '@/ontologies/detail/DatasetResponseDetail.tsx'

export interface VersionArtifactDetailProps {
  versionArtifact: VersionArtifactResponse | null
}

export default function VersionArtifactResponseDetail({ versionArtifact }: VersionArtifactDetailProps) {
  return <DatasetResponseDetail dataset={versionArtifact} />
}
