import { CrudProvider, type DataModel, type DataModelId, type DataSource, DataSourceCache, List } from '@toolpad/core'

import { useLocation } from 'wouter-preact'
import type { MuiModelListEntry } from '@/model/MuiModelListEntry.ts'
import { useCallback, useMemo } from 'preact/hooks'
import type { ResourceListEntry } from '@/model/ResourceListEntry.ts'

const INITIAL_PAGE_SIZE = 10
const PAGE_SIZE_OPTIONS = new Set([10, 25, 50, 100, INITIAL_PAGE_SIZE])
const PAGE_SIZE_OPTIONS_ARRAY = Array.from(PAGE_SIZE_OPTIONS).sort((a, b) => a - b)

function CrudList({ onCreateClick }: { onCreateClick?: () => void }) {
  const [_, navigate] = useLocation()
  const onRowClick = useCallback((id: DataModelId) => navigate(`/${encodeURIComponent(id)}`), [navigate])
  return (
    <List<MuiModelListEntry>
      initialPageSize={10}
      onRowClick={onRowClick}
      onCreateClick={onCreateClick}
      slotProps={{
        pageContainer: {
          disableGutters: true, // removes padding on sides
          sx: {
            // removes margin on top and bottom
            '& > .MuiStack-root': {
              mt: 0,
              mb: 0,
            },
            '& > .MuiStack-root > .MuiBox-root': {
              mt: 0,
            },
          },
        },
        dataGrid: {
          disableColumnFilter: true,
          sortingMode: 'server',
          filterMode: 'server',
          paginationMode: 'server',
          showToolbar: true,
          disableColumnSelector: true,
          disableDensitySelector: true,
          pageSizeOptions: PAGE_SIZE_OPTIONS_ARRAY,
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
  onCreateClick?: () => void
}

export default function ResourceEntryList<D extends ResourceListEntry & DataModel>(props: ResourceEntryListProps<D>) {
  const dataCacheRef = useMemo(() => new DataSourceCache(), [])

  return (
    <CrudProvider<D> dataSource={props.dataSource} dataSourceCache={dataCacheRef}>
      <CrudList onCreateClick={props.onCreateClick} />
    </CrudProvider>
  )
}
