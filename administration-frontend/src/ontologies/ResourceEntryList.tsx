import { CrudProvider, type DataModel, type DataModelId, type DataSource, DataSourceCache, List } from '@toolpad/core'

import { Route, Switch, useLocation, useRoute } from 'wouter-preact'
import type { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { useCallback, useMemo } from 'preact/hooks'
import type { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import type { FunctionComponent } from 'preact'

function CrudList({ onCreateClick }: { onCreateClick?: () => void }) {
  const [_, navigate] = useLocation()
  const onRowClick = useCallback((id: DataModelId) => navigate(`/${encodeURIComponent(id)}`), [navigate])
  return (
    <List<VersionSeriesListEntry>
      initialPageSize={10}
      onRowClick={onRowClick}
      onCreateClick={onCreateClick}
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

export interface ResourceDetailProps<D extends ResourceListEntry & DataModel> {
  identifier?: string
  dataSource: DataSource<D>
}

export interface ResourceEntryListProps<D extends ResourceListEntry & DataModel> {
  dataSource: DataSource<D>
  detailComponent: FunctionComponent<ResourceDetailProps<D>>
  onCreateClick?: () => void
}

export default function ResourceEntryList<D extends ResourceListEntry & DataModel>(props: ResourceEntryListProps<D>) {
  const [_, params] = useRoute('/:identifier')

  const dataCacheRef = useMemo(() => new DataSourceCache(), [])

  const ListComponent = useMemo(() => () => <CrudList onCreateClick={props.onCreateClick} />, [props.onCreateClick])

  const DetailComponent = useCallback(
    () => <props.detailComponent dataSource={props.dataSource} identifier={params?.identifier} />,
    [props.detailComponent, props.dataSource, params?.identifier]
  )

  return (
    <CrudProvider<D> dataSource={props.dataSource} dataSourceCache={dataCacheRef}>
      <Switch>
        <Route path={'/'} component={ListComponent} />
        <Route path={'/:identifier'} component={DetailComponent} nest />
      </Switch>
    </CrudProvider>
  )
}
