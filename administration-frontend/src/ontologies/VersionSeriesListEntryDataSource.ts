import type { DataField, DataSource } from '@toolpad/core'
import { type MuiModelListEntry } from '@/model/MuiModelListEntry.ts'
import { type i18n } from 'i18next'
import { Pageable, Sort } from '@hallysonh/pageable'
import { findAllVersionSeries } from '@/ontologies/actions.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'

import { defineResourceListEntryFields, type GetManyProps, mapSort } from '@/utils/DataSourceUtils.ts'

export class VersionSeriesListEntryDataSource implements DataSource<MuiModelListEntry> {
  readonly fields: DataField[]

  constructor(i18n: i18n) {
    this.fields = defineResourceListEntryFields(i18n)
  }

  getMany = ({ paginationModel, sortModel, filterModel }: GetManyProps): Promise<PagedResult<MuiModelListEntry>> => {
    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    return findAllVersionSeries(pageable, filterModel.quickFilterValues).then((page) => {
      return { items: page.content, itemCount: page.totalElements }
    })
  }
}
