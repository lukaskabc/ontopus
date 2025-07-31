import { Create, CrudProvider, DataSourceCache, Edit, List, Show, useDialogs } from '@toolpad/core'

import { Route, Switch, useLocation, useRoute } from 'wouter-preact'
import type { OntologyEntity } from '@/model/OntologyEntity.ts'
import { OntologyDataSource } from '@/deployments/OntologyDataSource.ts'
import { DataGrid } from '@mui/x-data-grid'
import OntologyPublishSourceSelectDialog from '@/deployments/OntologyPublishSourceSelectDialog.tsx'
import PublishStepper from '@/publish/PublishStepper.tsx'

const notesCache = new DataSourceCache()

function CrudList() {
  const [_, navigate] = useLocation()
  const dialogs = useDialogs()
  return (
    <List<OntologyEntity>
      initialPageSize={10}
      onRowClick={(id) => navigate(`/${id}`)}
      onCreateClick={async () => await dialogs.open(OntologyPublishSourceSelectDialog)}
      onEditClick={(id) => navigate(`/${id}/edit`)}
      slots={{
        dataGrid: (p) => <DataGrid {...p} />,
      }}
    />
  )
}

function CrudCreate() {
  return (
    <Create<OntologyEntity>
      initialValues={{ title: 'New note' }}
      onSubmitSuccess={() => console.debug('Create submit')}
      resetOnSubmit={false}
      pageTitle="New Note"
    />
  )
}

function CrudShow() {
  const [_, params] = useRoute('/:noteId')
  const noteId = params?.noteId || -1
  console.debug(params)
  return (
    <Show<OntologyEntity>
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
    <Edit<OntologyEntity>
      id={noteId}
      onSubmitSuccess={() => console.debug('Edit click')}
      pageTitle={`Note ${noteId} - Edit`}
    />
  )
}

export default function OntologiesList() {
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
    <CrudProvider<OntologyEntity> dataSource={OntologyDataSource} dataSourceCache={notesCache}>
      <Switch>
        {/*<Route path={'/publish'} component={CrudCreate} />*/}
        <Route path={'/publish'} component={PublishStepper} />
        <Route path={'/:noteId/edit'} component={CrudEdit} />
        <Route path={'/:noteId'} component={CrudShow} />
        <Route path={'/'} component={CrudList} />
      </Switch>
    </CrudProvider>
  )
}
