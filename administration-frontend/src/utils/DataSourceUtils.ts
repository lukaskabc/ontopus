import type { GridFilterModel, GridPaginationModel, GridSortDirection, GridSortModel } from '@mui/x-data-grid'
import { Direction, Order } from '@hallysonh/pageable'
import type { DataField } from '@toolpad/core'
import { getAnyLang, type MultilingualString } from '@/model/MultilingualString.ts'
import { type i18n } from 'i18next'

export interface GetManyProps {
  paginationModel: GridPaginationModel
  sortModel: GridSortModel
  filterModel: GridFilterModel
}

export function mapDirection(direction: GridSortDirection): Direction | undefined {
  switch (direction) {
    case 'asc':
      return Direction.asc
    case 'desc':
      return Direction.desc
    default:
      return undefined
  }
}

export function mapSort(sortModel: GridSortModel) {
  return sortModel.map((item) => {
    const direction = mapDirection(item.sort)
    return new Order(item.field, direction)
  })
}

export function defineResourceListEntryFields(i18n: i18n): DataField[] {
  return [
    {
      field: 'title',
      type: 'string',
      headerName: i18n.t('entity.version-series.table.name'),
      flex: 2,
      valueFormatter: (value: MultilingualString) => {
        if (Object.hasOwn(value, i18n.language)) {
          return value[i18n.language]
        }

        const language = getAnyLang(value)
        if (language) {
          return value[language]
        }

        return '<no title>'
      },
    },
    {
      field: 'version',
      type: 'string',
      headerName: i18n.t('entity.version-series.table.version'),
      flex: 1,
    },
    {
      field: 'modifiedDate',
      type: 'date',
      headerName: i18n.t('entity.version-series.table.modified'),
      flex: 1,
      valueFormatter: (val: Date) => i18n.t('data-format.date', { val }),
    },
  ]
}
