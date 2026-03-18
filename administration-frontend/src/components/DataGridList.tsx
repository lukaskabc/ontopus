// Modified version of https://github.com/mui/toolpad/blob/v0.16.0/packages/toolpad-core/src/Crud/List.tsx
/*
  The MIT License (MIT)

  Copyright (c) 2021 Material-UI SAS

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */
import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'
import {
  DataGrid,
  gridClasses,
  type GridColDef,
  type GridEventListener,
  type GridFilterModel,
  type GridPaginationModel,
  type GridSortModel,
} from '@mui/x-data-grid'
import AddIcon from '@mui/icons-material/Add'
import RefreshIcon from '@mui/icons-material/Refresh'
import type { DataModel, ListSlotProps } from '@toolpad/core/Crud'
import { useCallback, useContext, useEffect, useMemo, useState } from 'preact/hooks'
import {
  type DataModelId,
  type DataSource,
  DataSourceCache,
  PageContainer,
  useActivePage,
  useLocaleText,
} from '@toolpad/core'
import Stack from '@mui/material/Stack'
import { MuiRouterContext } from '@/components/WouterAppProvider.tsx'

import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import Tooltip from '@mui/material/Tooltip'

export interface ListProps<D extends DataModel> {
  /**
   * Server-side [data source](https://mui.com/toolpad/core/react-crud/#data-sources).
   */
  dataSource: DataSource<D> & Required<Pick<DataSource<D>, 'getMany'>>
  /**
   * Initial number of rows to show per page.
   * @default 100
   */
  initialPageSize?: number
  /**
   * Initial sort model for the list.
   */
  initialSortModel?: GridSortModel
  initialFilterModel?: GridFilterModel
  /**
   * Callback fired when a row is clicked. Not called if the target clicked is an interactive element added by the built-in columns.
   */
  onRowClick?: (id: DataModelId) => void
  /**
   * Callback fired when the "Create" button is clicked.
   */
  onCreateClick?: () => void

  /**
   * The title of the page.
   */
  pageTitle?: string

  /**
   * The props used for each slot inside.
   * @default {}
   */
  slotProps?: ListSlotProps
}

export default function DataGridList<D extends DataModel>(props: ListProps<D>) {
  const { initialPageSize = 100, onRowClick, onCreateClick, pageTitle, slotProps } = props

  const localeText = useLocaleText()

  const cachedDataSource = props.dataSource as NonNullable<typeof props.dataSource>
  // omitting the use of cache, required functions from toolpad are inaccessible

  const cache = useMemo(() => new DataSourceCache(), [])

  const { fields, ...methods } = cachedDataSource
  const { getMany } = methods

  const routerContext = useContext(MuiRouterContext)

  const activePage = useActivePage()

  const [paginationModel, setPaginationModel] = useState<GridPaginationModel>({
    page: routerContext?.searchParams.get('page') ? Number(routerContext?.searchParams.get('page')) : 0,
    pageSize: routerContext?.searchParams.get('pageSize')
      ? Number(routerContext?.searchParams.get('pageSize'))
      : initialPageSize,
  })
  const [filterModel, setFilterModel] = useState<GridFilterModel>(
    routerContext?.searchParams.get('filter')
      ? JSON.parse(routerContext?.searchParams.get('filter') ?? '')
      : (props.initialFilterModel ?? { items: [] })
  )
  const [sortModel, setSortModel] = useState<GridSortModel>(
    routerContext?.searchParams.get('sort')
      ? JSON.parse(routerContext?.searchParams.get('sort') ?? '')
      : (props.initialSortModel ?? [])
  )

  const cachedData = useMemo(
    () =>
      cache &&
      (cache.get(
        JSON.stringify([
          'getMany',
          {
            paginationModel,
            sortModel,
            filterModel,
          },
        ])
      ) as {
        items: D[]
        itemCount: number
      }),
    [cache, filterModel, paginationModel, sortModel]
  )

  const [rowsState, setRowsState] = useState<{ rows: D[]; rowCount: number }>({
    rows: cachedData?.items ?? [],
    rowCount: cachedData?.itemCount ?? 0,
  })
  const [isLoading, setIsLoading] = useState(!cachedData)
  const [error, setError] = useState<Error | null>(null)

  const handlePaginationModelChange = useCallback(
    (model: GridPaginationModel) => {
      setPaginationModel(model)

      if (routerContext) {
        const { pathname, searchParams, navigate } = routerContext

        // Needed because searchParams from Next.js are read-only
        const writeableSearchParams = new URLSearchParams(searchParams)

        writeableSearchParams.set('page', String(paginationModel.page))
        writeableSearchParams.set('pageSize', String(paginationModel.pageSize))

        const newSearchParamsString = writeableSearchParams.toString()

        navigate(`${pathname}${newSearchParamsString ? '?' : ''}${newSearchParamsString}`)
      }
    },
    [paginationModel.page, paginationModel.pageSize, routerContext]
  )

  const handleFilterModelChange = useCallback(
    (model: GridFilterModel) => {
      setFilterModel(model)

      if (routerContext) {
        const { pathname, searchParams, navigate } = routerContext

        // Needed because searchParams from Next.js are read-only
        const writeableSearchParams = new URLSearchParams(searchParams)

        if (
          filterModel.items.length > 0 ||
          (filterModel.quickFilterValues && filterModel.quickFilterValues.length > 0)
        ) {
          writeableSearchParams.set('filter', JSON.stringify(filterModel))
        } else {
          writeableSearchParams.delete('filter')
        }

        const newSearchParamsString = writeableSearchParams.toString()

        navigate(`${pathname}${newSearchParamsString ? '?' : ''}${newSearchParamsString}`)
      }
    },
    [filterModel, routerContext]
  )

  const handleSortModelChange = useCallback(
    (model: GridSortModel) => {
      setSortModel(model)

      if (routerContext) {
        const { pathname, searchParams, navigate } = routerContext

        // Needed because searchParams from Next.js are read-only
        const writeableSearchParams = new URLSearchParams(searchParams)

        if (model.length > 0) {
          writeableSearchParams.set('sort', JSON.stringify(model))
        } else {
          writeableSearchParams.delete('sort')
        }

        const newSearchParamsString = writeableSearchParams.toString()

        navigate(`${pathname}${newSearchParamsString ? '?' : ''}${newSearchParamsString}`)
      }
    },
    [routerContext]
  )

  const loadData = useCallback(async () => {
    setError(null)

    let listData = cachedData
    if (!listData) {
      setIsLoading(true)

      try {
        listData = await getMany({
          paginationModel,
          sortModel,
          filterModel,
        })
      } catch (listDataError) {
        setError(listDataError as Error)
      }
    }

    if (listData) {
      setRowsState({
        rows: listData.items,
        rowCount: listData.itemCount,
      })
    }
    setIsLoading(false)
  }, [cachedData, filterModel, getMany, paginationModel, sortModel])

  useEffect(() => {
    loadData()
  }, [loadData])

  const handleRefresh = useCallback(() => {
    if (!isLoading) {
      cache?.clear()
      loadData()
    }
  }, [cache, isLoading, loadData])

  const handleRowClick = useCallback<GridEventListener<'rowClick'>>(
    ({ row }) => {
      if (onRowClick) {
        onRowClick(row.id)
      }
    },
    [onRowClick]
  )

  const initialState = useMemo(
    () => ({
      pagination: { paginationModel: { pageSize: initialPageSize } },
      sorting: { sortModel: props.initialSortModel },
      filter: { filterModel: props.initialFilterModel },
    }),
    [initialPageSize, props.initialSortModel, props.initialFilterModel]
  )

  const columns = useMemo<GridColDef[]>(() => {
    return [
      ...fields.map((field) => ({
        ...field,
        editable: false,
      })),
    ]
  }, [fields])

  return (
    <PageContainer
      title={pageTitle}
      breadcrumbs={
        activePage && pageTitle
          ? [
              ...activePage.breadcrumbs,
              {
                title: pageTitle,
              },
            ]
          : undefined
      }
      {...slotProps?.pageContainer}
    >
      <Box sx={{ flex: 1, width: '100%' }}>
        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1 }}>
          <Tooltip title={localeText.reloadButtonLabel} placement="right" enterDelay={1000}>
            <div>
              <IconButton aria-label="refresh" onClick={handleRefresh}>
                <RefreshIcon />
              </IconButton>
            </div>
          </Tooltip>
          {onCreateClick ? (
            <Button variant="contained" onClick={onCreateClick} startIcon={<AddIcon />}>
              {localeText.createNewButtonLabel}
            </Button>
          ) : null}
        </Stack>
        {error ? (
          <Box sx={{ flexGrow: 1 }}>
            <Alert severity="error">{error.message}</Alert>
          </Box>
        ) : (
          <DataGrid
            rows={rowsState.rows}
            rowCount={rowsState.rowCount}
            columns={columns}
            pagination
            sortingMode="server"
            filterMode="server"
            paginationMode="server"
            paginationModel={paginationModel}
            onPaginationModelChange={handlePaginationModelChange}
            sortModel={sortModel}
            onSortModelChange={handleSortModelChange}
            filterModel={filterModel}
            onFilterModelChange={handleFilterModelChange}
            disableRowSelectionOnClick
            onRowClick={handleRowClick}
            loading={isLoading}
            initialState={initialState}
            showToolbar={true}
            // Prevent type conflicts if slotProps don't match DataGrid used for dataGrid slot
            {...(slotProps?.dataGrid as Record<string, unknown>)}
            sx={{
              [`& .${gridClasses.columnHeader}, & .${gridClasses.cell}`]: {
                outline: 'transparent',
              },
              [`& .${gridClasses.columnHeader}:focus-within, & .${gridClasses.cell}:focus-within`]: {
                outline: 'none',
              },
              ...(onRowClick
                ? {
                    [`& .${gridClasses.row}:hover`]: {
                      cursor: 'pointer',
                    },
                  }
                : {}),
              ...slotProps?.dataGrid?.sx,
            }}
          />
        )}
      </Box>
    </PageContainer>
  )
}
