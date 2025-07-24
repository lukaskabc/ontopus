import '@/config/i18n.ts'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import { DashboardLayout } from '@toolpad/core'
import { Route, Switch } from 'wouter-preact'
import OntologiesList from '@/deployments/OntologiesList.tsx'
import TransparentPaper from '@/components/TransparentPaper.tsx'

function SecondDemoPageContent() {
  return (
    <Box
      sx={{
        py: 4,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        textAlign: 'center',
      }}
    >
      <Typography>SECOND Dashboard content</Typography>
    </Box>
  )
}

export default function Dashboard() {
  // TODO: replace header with wouter link
  // https://mui.com/toolpad/core/react-dashboard-layout/#slots

  return (
    <div id={'dashboard-wrapper'}>
      <DashboardLayout disableCollapsibleSidebar>
        <TransparentPaper>
          <Switch>
            <Route path={'/orders'} component={SecondDemoPageContent} />
            {/* Keep the last route as last item */}
            <Route component={OntologiesList} />
          </Switch>
        </TransparentPaper>
      </DashboardLayout>
    </div>
  )
}
