import { DashboardLayout, DialogsProvider } from '@toolpad/core'
import TransparentPaper from '@/components/TransparentPaper.tsx'
import { Redirect, Route, Switch } from 'wouter-preact'
import lazy from 'preact-iso/lazy'

const PublishStepper = lazy(() => import('@/publish/PublishStepper.tsx'))
const OntologiesList = lazy(() => import('@/ontologies/OntologiesList.tsx'))

export default function Dashboard() {
  return (
    <DashboardLayout disableCollapsibleSidebar={true} hideNavigation={true}>
      <TransparentPaper>
        <DialogsProvider>
          <Switch>
            <Route path={'/publish'} component={PublishStepper} nest />
            <Route path={'/'} component={OntologiesList} nest />
            <Redirect to={'/'} />
          </Switch>
        </DialogsProvider>
      </TransparentPaper>
    </DashboardLayout>
  )
}
