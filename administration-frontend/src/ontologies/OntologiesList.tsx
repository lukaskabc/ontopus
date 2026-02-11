import { CrudProvider, DataSourceCache, Edit, List, Show } from '@toolpad/core'

import { Route, Switch, useLocation, useRoute } from 'wouter-preact'
import type { VersionSeriesListEntry } from '@/model/VersionSeriesListEntry.ts'
import { VersionSeriesDataSource } from '@/ontologies/OntologyDataSource.ts'
import { useMemo } from 'preact/hooks'
import { useTranslation } from 'react-i18next'

function CrudList() {
  const [_, navigate] = useLocation()
  return (
    <List<VersionSeriesListEntry>
      initialPageSize={10}
      onRowClick={(id) => navigate(`/${id}`)}
      onCreateClick={() => navigate(`/publish`)}
      onEditClick={(id) => navigate(`/${id}/edit`)}
    />
  )
}

// function CrudCreate() {
//   return (
//     <Create<OntologyEntity>
//       initialValues={{ title: 'New note' }}
//       onSubmitSuccess={() => console.debug('Create submit')}
//       resetOnSubmit={false}
//       pageTitle="New Note"
//     />
//   )
// }

function CrudShow() {
  const [_, params] = useRoute('/:noteId')
  const noteId = params?.noteId || -1
  console.debug(params)
  return (
    <Show<VersionSeriesListEntry>
      id={noteId}
      onEditClick={() => console.debug('Edit click')}
      onDelete={() => console.debug('Delete click')}
      pageTitle={`Note ${noteId}`}
    />
  )
}

function CrudEdit() {
  const [_, params] = useRoute('/:noteId/edit')
  const noteId = params?.noteId || -1
  console.debug(params)
  return (
    <Edit<VersionSeriesListEntry>
      id={noteId}
      onSubmitSuccess={() => console.debug('Edit click')}
      pageTitle={`Note ${noteId} - Edit`}
    />
  )
}

export default function OntologiesList() {
  const { t } = useTranslation()
  const dataCacheRef = useMemo(() => new DataSourceCache(), [])
  const dataSource = useMemo(() => new VersionSeriesDataSource(t), [t])
  return (
    // <Crud<OntologyEntity>
    //   dataSource={OntologyDataSource}
    //   dataSourceCache={notesCache}
    //   rootPath="/ontologies"
    //   initialPageSize={10}
    //   defaultValues={{ title: 'New note' }}
    //   pageTitles={{
    //     create: 'New Note',
    //     edit: `Note ${editNoteId} - Edit`,
    //     show: `Note ${showNoteId}`,
    //   }}
    // />
    <CrudProvider<VersionSeriesListEntry> dataSource={dataSource} dataSourceCache={dataCacheRef}>
      <Switch>
        <Route path={'/:noteId/edit'} component={CrudEdit} />
        <Route path={'/:noteId'} component={CrudShow} />
        <Route path={'/'} component={CrudList} />
      </Switch>
    </CrudProvider>
  )
}
