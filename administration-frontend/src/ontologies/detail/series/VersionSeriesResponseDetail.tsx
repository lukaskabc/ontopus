import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import DatasetResponseDetail from '@/ontologies/detail/DatasetResponseDetail.tsx'
import { useTranslation } from 'react-i18next'
import ReadOnlyField from '@/components/ReadOnlyField.tsx'

export interface VersionSeriesDetailProps {
  versionSeries: VersionSeriesResponse | null
}

export default function VersionSeriesResponseDetail({ versionSeries }: VersionSeriesDetailProps) {
  const { t } = useTranslation()
  return (
    <>
      <ReadOnlyField
        label={t('entity.version-series.detail.ontologyIdentifier')}
        value={versionSeries?.ontologyURI}
        sx={{ mb: 3 }}
        isCode
      />
      <DatasetResponseDetail dataset={versionSeries} />
    </>
  )
}
