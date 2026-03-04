import type { DataField, DataSource } from '@toolpad/core'
import type { VersionArtifactListEntry } from '@/model/VersionArtifactListEntry.ts'
import { type i18n } from 'i18next'
import { defineResourceListEntryFields, type GetManyProps, mapSort } from '@/utils/DataSourceUtils.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'
import type { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { Pageable, Sort } from '@hallysonh/pageable'
import { findVersionArtifactsForVersionSeries } from '@/ontologies/detail/series/actions.ts'

export class VersionArtifactListEntryDataSource implements DataSource<VersionArtifactListEntry> {
  readonly fields: DataField[]
  readonly versionSeriesUri: string | null

  constructor(versionSeriesUri: string | null, i18n: i18n) {
    this.fields = defineResourceListEntryFields(i18n)
    this.versionSeriesUri = versionSeriesUri
    console.debug('ARTIFACT DATASOURCE for', versionSeriesUri)
  }

  getMany = ({
    paginationModel,
    sortModel,
    filterModel,
  }: GetManyProps): Promise<PagedResult<VersionSeriesListEntry>> => {
    console.debug(
      'Requesting artifacts for version series',
      this.versionSeriesUri,
      paginationModel,
      sortModel,
      filterModel
    )
    if (!this.versionSeriesUri) {
      return Promise.resolve({ items: [], itemCount: 0 })
    }

    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    return findVersionArtifactsForVersionSeries(this.versionSeriesUri, pageable, filterModel.quickFilterValues).then(
      (page) => {
        return { items: page.content, itemCount: page.totalElements }
      }
    )
  }
}
