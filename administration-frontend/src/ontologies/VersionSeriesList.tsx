import ResourceEntryList from '@/ontologies/ResourceEntryList.tsx'

import { useTranslation } from 'react-i18next'
import { useMemo } from 'preact/hooks'
import { VersionSeriesDetail } from '@/ontologies/detail/series/VersionSeriesDetail.tsx'
import { VersionSeriesListEntryDataSource } from '@/ontologies/VersionSeriesListEntryDataSource.ts'

export default function VersionSeriesList() {
  const { i18n } = useTranslation()
  const dataSource = useMemo(() => new VersionSeriesListEntryDataSource(i18n), [i18n])
  return <ResourceEntryList dataSource={dataSource} detailComponent={VersionSeriesDetail} />
}
