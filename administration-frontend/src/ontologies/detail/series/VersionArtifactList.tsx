import { useTranslation } from 'react-i18next'
import { useMemo } from 'preact/hooks'
import { VersionArtifactListEntryDataSource } from '@/ontologies/detail/series/VersionArtifactListEntryDataSource.ts'
import ResourceEntryList from '@/ontologies/ResourceEntryList.tsx'
import { VersionArtifactDetail } from '@/ontologies/detail/artifact/VersionArtifactDetail.tsx'

export interface VersionArtifactListProps {
  versionSeriesUri: string | null
}

export default function VersionArtifactList({ versionSeriesUri }: VersionArtifactListProps) {
  const { i18n } = useTranslation()
  const dataSource = useMemo(
    () => new VersionArtifactListEntryDataSource(versionSeriesUri, i18n),
    [versionSeriesUri, i18n]
  )
  return <ResourceEntryList dataSource={dataSource} detailComponent={VersionArtifactDetail} />
}
