import { DashboardLayout, DialogsProvider } from '@toolpad/core'
import TransparentPaper from '@/components/TransparentPaper.tsx'
import { Route, Switch } from 'wouter-preact'
import lazy from 'preact-iso/lazy'
import { VersionSeriesDetail } from '@/ontologies/detail/series/VersionSeriesDetail.tsx'
import { VersionArtifactDetail } from '@/ontologies/detail/artifact/VersionArtifactDetail.tsx'
import { Container } from '@mui/material'
import { PUBLISH_STEPPER_ROUTE } from '@/Constants.ts'
import ToolbarActions from '@/dashboard/ToolbarActions.tsx'
import Header from '@/dashboard/Header.tsx'

const PublishStepper = lazy(() => import('@/publish/PublishStepper.tsx'))
const VersionSeriesList = lazy(() => import('@/ontologies/VersionSeriesList.tsx'))

export default function Dashboard() {
  return (
    <DashboardLayout
      disableCollapsibleSidebar={true}
      hideNavigation={true}
      slotProps={{
        header: {
          slots: {
            appTitle: Header,
            toolbarActions: ToolbarActions,
          },
          // menu is not used
          menuOpen: false,
          onToggleMenu: () => null,
        },
      }}
    >
      <TransparentPaper>
        <DialogsProvider>
          <Container maxWidth="lg" sx={{ mt: 5 }}>
            <Switch>
              <Route path={PUBLISH_STEPPER_ROUTE} component={PublishStepper} nest />
              <Route path={'/:versionSeriesIdentifier'} nest>
                {({ versionSeriesIdentifier }) => (
                  <Switch>
                    <Route path={'/:artifactIdentifier'}>
                      {({ artifactIdentifier }) => (
                        <VersionArtifactDetail
                          versionSeriesIdentifier={versionSeriesIdentifier}
                          identifier={artifactIdentifier}
                        />
                      )}
                    </Route>
                    <Route path={'/'}>
                      <VersionSeriesDetail identifier={versionSeriesIdentifier} />
                    </Route>
                  </Switch>
                )}
              </Route>
              <Route path={'/'} component={VersionSeriesList}></Route>
            </Switch>
          </Container>
        </DialogsProvider>
      </TransparentPaper>
    </DashboardLayout>
  )
}
