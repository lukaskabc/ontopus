import '@/config/i18n.ts'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import {
  DashboardLayout,
  DashboardSidebarPageItem,
  type NavigationPageItem,
} from '@toolpad/core'
import {
  Link as WouterLink,
  type LinkProps,
  Route,
  Switch,
  useRoute,
} from 'wouter-preact'
import { useCallback } from 'preact/hooks'

function DemoPageContent() {
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
      <Typography>FIRST Dashboard content</Typography>
    </Box>
  )
}

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

function DashboardSidebarLink(props: LinkProps) {
  return (
    <WouterLink {...props} asChild={true}>
      {props.children}
    </WouterLink>
  )
}

export default function Dashboard() {
  const renderPageItem = useCallback(
    (item: NavigationPageItem, { mini }: { mini: boolean }) => {
      const [isSelected] = useRoute(item.segment || '')
      return (
        <DashboardSidebarPageItem
          item={item}
          href={item.segment}
          expanded={!mini}
          LinkComponent={DashboardSidebarLink}
          selected={isSelected}
        />
      )
    },
    []
  )

  return (
    <div id={'dashboard-wrapper'}>
      <DashboardLayout
        disableCollapsibleSidebar
        renderPageItem={renderPageItem}
      >
        <Switch>
          <Route path={'/orders'} component={SecondDemoPageContent} />
          {/* Keep the last route as last item */}
          <Route component={DemoPageContent} />
        </Switch>
      </DashboardLayout>
    </div>
  )
}
