import type { DataField, DataSource } from '@toolpad/core'
import { type VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { type TFunction } from 'i18next'
import type { GridFilterModel, GridPaginationModel, GridSortDirection, GridSortModel } from '@mui/x-data-grid'
import { Direction, Order, Pageable, Sort } from '@hallysonh/pageable'
import { findVersionSeries } from '@/ontologies/actions.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'

// const ontologyStore: VersionSeriesListEntry[] = [
//   {
//     id: 'Aid',
//     uri: 'Auri',
//     ontologyUri: 'AontologyUri',
//     name: 'A ontology',
//     versionInfo: 'A version info',
//     versionIri: 'A version IRI',
//   },
//   {
//     id: 'Bid',
//     uri: 'Buri',
//     ontologyUri: 'BontologyUri',
//     name: 'B ontology',
//     versionInfo: 'B version info',
//     versionIri: 'B version IRI',
//   },
//   {
//     id: 'Cid',
//     uri: 'Curi',
//     ontologyUri: 'ContologyUri',
//     name: 'C ontology',
//     versionInfo: 'C version info',
//     versionIri: 'C version IRI',
//   },
// ]

function defineFields(t: TFunction): DataField[] {
  return [
    { field: 'title', headerName: t('entity.version-series.name'), flex: 2 },
    {
      field: 'version',
      headerName: t('entity.version-series.version'),
      flex: 1,
    },
    { field: 'modifiedDate', headerName: t('entity.version-series.modified') },
  ]
}

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

export class VersionSeriesDataSource implements DataSource<VersionSeriesListEntry> {
  readonly fields: DataField[]

  constructor(t: TFunction) {
    this.fields = defineFields(t)
  }

  getMany({ paginationModel, sortModel }: GetManyProps): Promise<PagedResult<VersionSeriesListEntry>> {
    // TODO filtering
    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    return findVersionSeries(pageable).then((page) => {
      return { items: page.content, itemCount: page.totalElements }
    })
  }
}
//
// export const OntologyDataSource: DataSource<VersionSeriesListEntry> = {
//   getMany: async ({ paginationModel, filterModel, sortModel }) => {
//     // Simulate loading delay
//     await new Promise((resolve) => {
//       setTimeout(resolve, 750)
//     })
//
//     let processedNotes = [...ontologyStore]
//
//     // Apply filters (demo only)
//     if (filterModel?.items?.length) {
//       filterModel.items.forEach(({ field, value, operator }) => {
//         if (!field || value == null) {
//           return
//         }
//
//         processedNotes = processedNotes.filter((note) => {
//           const noteValue = note[field]
//
//           switch (operator) {
//             case 'contains':
//               return String(noteValue).toLowerCase().includes(String(value).toLowerCase())
//             case 'equals':
//               return noteValue === value
//             case 'startsWith':
//               return String(noteValue).toLowerCase().startsWith(String(value).toLowerCase())
//             case 'endsWith':
//               return String(noteValue).toLowerCase().endsWith(String(value).toLowerCase())
//             case '>':
//               return (noteValue as number) > value
//             case '<':
//               return (noteValue as number) < value
//             default:
//               return true
//           }
//         })
//       })
//     }
//
//     // Apply sorting
//     if (sortModel?.length) {
//       processedNotes.sort((a, b) => {
//         for (const { field, sort } of sortModel) {
//           if ((a[field] as number) < (b[field] as number)) {
//             return sort === 'asc' ? -1 : 1
//           }
//           if ((a[field] as number) > (b[field] as number)) {
//             return sort === 'asc' ? 1 : -1
//           }
//         }
//         return 0
//       })
//     }
//
//     // Apply pagination
//     const start = paginationModel.page * paginationModel.pageSize
//     const end = start + paginationModel.pageSize
//     const paginatedNotes = processedNotes.slice(start, end)
//
//     return {
//       items: paginatedNotes,
//       itemCount: processedNotes.length,
//     }
//   },
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
