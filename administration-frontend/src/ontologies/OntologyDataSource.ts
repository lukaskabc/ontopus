import type { DataField, DataSource } from '@toolpad/core'
import { type VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { type i18n } from 'i18next'
import type { GridFilterModel, GridPaginationModel, GridSortDirection, GridSortModel } from '@mui/x-data-grid'
import { Direction, Order, Pageable, Sort } from '@hallysonh/pageable'
import { findAllVersionSeries } from '@/ontologies/actions.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'
import { getAnyLang, type MultilingualString } from '@/model/MultilingualString.ts'
import { createContext } from 'preact'

export type GetManyProps = {
  paginationModel: GridPaginationModel
  sortModel: GridSortModel
  filterModel: GridFilterModel
}

function mapDirection(direction: GridSortDirection): Direction | undefined {
  switch (direction) {
    case 'asc':
      return Direction.asc
    case 'desc':
      return Direction.desc
    default:
      return undefined
  }
}

function mapSort(sortModel: GridSortModel) {
  return sortModel.map((item) => {
    const direction = mapDirection(item.sort)
    return new Order(item.field, direction)
  })
}

function defineFields(i18n: i18n): DataField[] {
  return [
    {
      field: 'title',
      type: 'string',
      headerName: i18n.t('entity.version-series.table.name'),
      flex: 2,
      valueFormatter: (value: MultilingualString) => {
        if (value.hasOwnProperty(i18n.language)) {
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

export class VersionSeriesListEntryDataSource implements DataSource<VersionSeriesListEntry> {
  readonly fields: DataField[]

  constructor(i18n: i18n) {
    this.fields = defineFields(i18n)
  }

  getMany({ paginationModel, sortModel, filterModel }: GetManyProps): Promise<PagedResult<VersionSeriesListEntry>> {
    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    return findAllVersionSeries(pageable, filterModel.quickFilterValues).then((page) => {
      return { items: page.content, itemCount: page.totalElements }
    })
  }
}

//
//   getOne: async (noteId) => {
//     // Simulate loading delay
//     await new Promise((resolve) => {
//       setTimeout(resolve, 750)
//     })
//
//     const noteToShow = ontologyStore.find((note) => note.id === Number(noteId))
//
//     if (!noteToShow) {
//       throw new Error('Note not found')
//     }
//     return noteToShow
//   },
//
//   createOne: async (data) => {
//     // Simulate loading delay
//     await new Promise((resolve) => {
//       setTimeout(resolve, 750)
//     })
//
//     const newNote = {
//       id: ontologyStore.reduce((max, note) => Math.max(max, note.id), 0) + 1,
//       ...data,
//     } as VersionSeriesListEntry
//
//     // ontologyStore = [...ontologyStore, newNote]
//
//     return newNote
//   },
//
//   updateOne: async (noteId, data) => {
//     // Simulate loading delay
//     await new Promise((resolve) => {
//       setTimeout(resolve, 750)
//     })
//
//     let updatedNote: VersionSeriesListEntry | null = null
//
//     // notesStore = notesStore.map((note) => {
//     //   if (note.id === Number(noteId)) {
//     //     updatedNote = { ...note, ...data }
//     //     return updatedNote
//     //   }
//     //   return note
//     // })
//
//     if (!updatedNote) {
//       throw new Error('Note not found')
//     }
//     return updatedNote
//   },
//
//   deleteOne: async (noteId) => {
//     // Simulate loading delay
//     await new Promise((resolve) => {
//       setTimeout(resolve, 750)
//     })
//
//     // notesStore = notesStore.filter((note) => note.id !== Number(noteId))
//   },
//
//   validate: (formValues) => {
//     let issues: { message: string; path: [keyof VersionSeriesListEntry] }[] = []
//
//     // if (!formValues.title) {
//     //   issues = [...issues, { message: 'Title is required', path: ['title'] }]
//     // }
//     //
//     // if (formValues.title && formValues.title.length < 3) {
//     //   issues = [
//     //     ...issues,
//     //     {
//     //       message: 'Title must be at least 3 characters long',
//     //       path: ['title'],
//     //     },
//     //   ]
//     // }
//     //
//     // if (!formValues.text) {
//     //   issues = [...issues, { message: 'Text is required', path: ['text'] }]
//     // }
//
//     return { issues }
//   },
// }

export const VersionSeriesListEntryDataSourceContext = createContext<VersionSeriesListEntryDataSource | null>(null)
