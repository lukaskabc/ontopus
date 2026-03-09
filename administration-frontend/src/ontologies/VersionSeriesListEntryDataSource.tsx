import type { DataField, DataSource } from '@toolpad/core'
import { type MuiModelListEntry } from '@/model/MuiModelListEntry.ts'
import { type i18n } from 'i18next'
import { Pageable, Sort } from '@hallysonh/pageable'
import { findAllVersionSeries } from '@/ontologies/actions.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'

import { defineResourceListEntryFields, type GetManyProps, mapSort } from '@/utils/DataSourceUtils.ts'
import type { GridRenderCellParams } from '@mui/x-data-grid'
import { IconButton, Tooltip } from '@mui/material'
import CloudUploadIcon from '@mui/icons-material/CloudUpload'
import { useTranslation } from 'react-i18next'
import { useLocation } from 'wouter-preact'

function ActionsCell(params: GridRenderCellParams) {
  const { t } = useTranslation()
  const [_, navigate] = useLocation()
  // a new cell is rendered for each row
  const onClick = () => {
    navigate('/publish/' + encodeURIComponent(params.id))
  }
  return (
    <Tooltip title={t('action.publish.version.artifact')} placement={'top'}>
      <IconButton aria-label={'New version'} onClick={onClick} size={'small'}>
        <CloudUploadIcon />
      </IconButton>
    </Tooltip>
  )
}

export class VersionSeriesListEntryDataSource implements DataSource<MuiModelListEntry> {
  readonly fields: DataField[]

  constructor(i18n: i18n) {
    this.fields = [
      ...defineResourceListEntryFields(i18n),
      {
        field: 'ontopus_actions',
        type: 'actions',
        headerName: '',
        flex: 0,
        renderCell: ActionsCell,
      },
    ]
  }

  getMany = ({ paginationModel, sortModel, filterModel }: GetManyProps): Promise<PagedResult<MuiModelListEntry>> => {
    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    return findAllVersionSeries(pageable, filterModel.quickFilterValues).then((page) => {
      return { items: page.content, itemCount: page.totalElements }
    })
  }
}
