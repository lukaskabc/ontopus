import { type DataModel, type DataModelId, type DataSource } from '@toolpad/core'
import { useCallback } from 'preact/hooks'
import type { ResourceListEntry } from '@/model/ResourceListEntry.ts'
import type { GridFilterModel, GridSortModel } from '@mui/x-data-grid'
import DataGridList from '@/components/DataGridList.tsx'
import { useLocation } from '@/utils/hooks.ts'

const INITIAL_PAGE_SIZE = 10
const PAGE_SIZE_OPTIONS = new Set([10, 25, 50, 100, INITIAL_PAGE_SIZE])
const PAGE_SIZE_OPTIONS_ARRAY = Array.from(PAGE_SIZE_OPTIONS).sort((a, b) => a - b)

export interface InitialGridState {
  sort?: GridSortModel
  filter?: GridFilterModel
}

export interface ResourceEntryListProps<D extends ResourceListEntry & DataModel> {
  dataSource: DataSource<D> & Required<Pick<DataSource<D>, 'getMany'>>
  gridInitialState?: InitialGridState
  onCreateClick?: () => void
}

export default function ResourceEntryList<D extends ResourceListEntry & DataModel>(props: ResourceEntryListProps<D>) {
  const { dataSource, onCreateClick } = props
  const gridInitialState = props.gridInitialState ?? {
    sort: [{ field: 'modifiedDate', sort: 'desc' }],
  }

  const { navigate } = useLocation()
  const onRowClick = useCallback((id: DataModelId) => navigate(`/${encodeURIComponent(id)}`), [navigate])
  return (
    <DataGridList<D>
      dataSource={dataSource}
      initialPageSize={10}
      onRowClick={onRowClick}
      onCreateClick={onCreateClick}
      initialSortModel={gridInitialState?.sort}
      initialFilterModel={gridInitialState?.filter}
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
            actions: false, // disable default actions column
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
