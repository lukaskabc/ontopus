import ResourceEntryList from '@/ontologies/ResourceEntryList.tsx'
import { useCallback, useMemo } from 'preact/hooks'
import { VersionSeriesListEntryDataSource } from '@/ontologies/VersionSeriesListEntryDataSource.tsx'
import { useTranslation } from 'react-i18next'
import { useLocation } from '@/utils/hooks.ts'

export default function VersionSeriesList() {
  const { navigate } = useLocation()
  const { i18n } = useTranslation()
  const dataSource = useMemo(() => new VersionSeriesListEntryDataSource(i18n), [i18n])
  const onCreateClick = useCallback(() => navigate(`/publish`), [navigate])
  return <ResourceEntryList dataSource={dataSource} onCreateClick={onCreateClick} />
}
