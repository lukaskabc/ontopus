import { CrudProvider, DataSourceCache, List } from '@toolpad/core'

import { Route, Switch, useLocation } from 'wouter-preact'
import type { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import {
  VersionSeriesListEntryDataSource,
  VersionSeriesListEntryDataSourceContext,
} from '@/ontologies/OntologyDataSource.ts'
import { useMemo } from 'preact/hooks'
import { useTranslation } from 'react-i18next'
import { VersionSeriesDetail } from '@/ontologies/detail/VersionSeriesDetail.tsx'

function CrudList() {
  const [_, navigate] = useLocation()
  return (
    <List<VersionSeriesListEntry>
      initialPageSize={10}
      onRowClick={(id) => navigate(`/${encodeURIComponent(id)}`)}
      onCreateClick={() => navigate(`/publish`)}
      slotProps={{
        dataGrid: {
          disableColumnFilter: true,
          sortingMode: 'server',
          filterMode: 'server',
          paginationMode: 'server',
          showToolbar: true,
          disableColumnSelector: true,
          disableDensitySelector: true,
          columnVisibilityModel: {
            actions: false,
          },
          slotProps: {
            toolbar: {
              printOptions: { disableToolbarButton: true },
              csvOptions: { disableToolbarButton: true },
            },
          },
        },
      }}
    />
  )
}

export default function OntologiesList() {
  const { i18n } = useTranslation()
  const dataCacheRef = useMemo(() => new DataSourceCache(), [])
  const dataSource = useMemo(() => new VersionSeriesListEntryDataSource(i18n), [i18n])

  return (
    <VersionSeriesListEntryDataSourceContext.Provider value={dataSource}>
      <CrudProvider<VersionSeriesListEntry> dataSource={dataSource} dataSourceCache={dataCacheRef}>
        <Switch>
          <Route path={'/'} component={CrudList} />
          <Route path={'/*'} component={VersionSeriesDetail} />
        </Switch>
      </CrudProvider>
    </VersionSeriesListEntryDataSourceContext.Provider>
  )
}
