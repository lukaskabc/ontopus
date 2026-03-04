import { DashboardLayout, DialogsProvider } from '@toolpad/core'
import TransparentPaper from '@/components/TransparentPaper.tsx'
import { Route, Switch } from 'wouter-preact'
import lazy from 'preact-iso/lazy'

const PublishStepper = lazy(() => import('@/publish/PublishStepper.tsx'))
const VersionSeriesList = lazy(() => import('@/ontologies/VersionSeriesList.tsx'))

export default function Dashboard() {
  return (
    <DashboardLayout disableCollapsibleSidebar={true} hideNavigation={true}>
      <TransparentPaper>
        <DialogsProvider>
          <Switch>
            <Route path={'/publish'} component={PublishStepper} nest />
            <Route path={'/'} component={VersionSeriesList} nest />
          </Switch>
        </DialogsProvider>
      </TransparentPaper>
    </DashboardLayout>
  )
}
