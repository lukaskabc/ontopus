import type { DataField, DataSource } from '@toolpad/core'
import { MuiModelListEntryWithOptions, OntopusOptionEntry } from '@/model/MuiModelListEntry.ts'
import { type i18n } from 'i18next'
import { Pageable, Sort } from '@hallysonh/pageable'
import { findAllVersionSeries, findSeriesOptions } from '@/ontologies/actions.ts'
import type { PagedResult } from '@/utils/RequestTypes.ts'

import { defineResourceListEntryFields, type GetManyProps, mapSort } from '@/utils/DataSourceUtils.ts'
import type { GridRenderCellParams } from '@mui/x-data-grid'
import CloudUploadIcon from '@mui/icons-material/CloudUpload'
import { useTranslation } from 'react-i18next'
import { useLocation } from '@/utils/hooks.ts'
import OntopusOptionsMenu from '@/components/OntopusOptionsMenu.tsx'
import IconButton from '@mui/material/IconButton'
import Tooltip from '@mui/material/Tooltip'

function ActionsCell(params: GridRenderCellParams<MuiModelListEntryWithOptions>) {
  const { t } = useTranslation()
  const { navigate } = useLocation()
  // a new cell is rendered for each row
  const onClick = () => {
    navigate('/publish/' + encodeURIComponent(params.id))
  }

  return (
    <>
      <Tooltip title={t('action.publish.version.artifact')} placement={'top'}>
        <IconButton aria-label={'New version'} onClick={onClick} size={'small'}>
          <CloudUploadIcon />
        </IconButton>
      </Tooltip>
      <OntopusOptionsMenu options={params.row.ontopusOptions} pathPrefix={''} series={params.row.identifier} />
    </>
  )
}

async function resolveOptions(identifiers: string[]) {
  if (!identifiers || identifiers.length < 1) {
    return new Map<string, OntopusOptionEntry[]>()
  }
  return findSeriesOptions(identifiers)
}

export class VersionSeriesListEntryDataSource implements DataSource<MuiModelListEntryWithOptions> {
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

  getMany = async ({
    paginationModel,
    sortModel,
    filterModel,
  }: GetManyProps): Promise<PagedResult<MuiModelListEntryWithOptions>> => {
    const sort = new Sort(mapSort(sortModel))
    const pageable = new Pageable(paginationModel.page, paginationModel.pageSize, false, sort)

    const page = await findAllVersionSeries(pageable, filterModel.quickFilterValues)
    const identifiers = page.content.map((entry) => entry.identifier)
    const options = await resolveOptions(identifiers)

    const itemCount = page.totalElements
    const items = page.content.map((entry) => {
      const optionsList = options.get(entry.identifier) ?? []
      return new MuiModelListEntryWithOptions(entry, optionsList)
    })

    return { items, itemCount }
  }
}
