import type { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'
import DatasetResponseDetail from '@/ontologies/detail/DatasetResponseDetail.tsx'
import ReadOnlyField from '@/components/ReadOnlyField.tsx'
import { useTranslation } from 'react-i18next'

export interface VersionArtifactDetailProps {
  versionArtifact: VersionArtifactResponse | null
}

export default function VersionArtifactResponseDetail({ versionArtifact }: VersionArtifactDetailProps) {
  const { t } = useTranslation()
  return (
    <>
      <ReadOnlyField
        label={t('entity.version-artifact.detail.versionUri')}
        value={versionArtifact?.versionUri}
        sx={{ mb: 3 }}
        isCode
      />
      <DatasetResponseDetail dataset={versionArtifact} />
    </>
  )
}
