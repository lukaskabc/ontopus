import ResourceEntryList from '@/ontologies/ResourceEntryList.tsx'

import { useTranslation } from 'react-i18next'
import { useCallback, useMemo } from 'preact/hooks'
import { VersionSeriesDetail } from '@/ontologies/detail/series/VersionSeriesDetail.tsx'
import { VersionSeriesListEntryDataSource } from '@/ontologies/VersionSeriesListEntryDataSource.ts'
import { useLocation } from 'wouter-preact'

export default function VersionSeriesList() {
  const [_, navigate] = useLocation()
  const { i18n } = useTranslation()
  const dataSource = useMemo(() => new VersionSeriesListEntryDataSource(i18n), [i18n])
  const onCreateClick = useCallback(() => navigate(`/publish`), [navigate])
  return (
    <ResourceEntryList dataSource={dataSource} detailComponent={VersionSeriesDetail} onCreateClick={onCreateClick} />
  )
}
