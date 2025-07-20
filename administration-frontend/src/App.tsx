import '@/config/i18n.ts'
import '@/assets/theme.scss'
import { AppProvider, type Navigation } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Redirect, Route, Switch } from 'wouter-preact'
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart'
import DashboardIcon from '@mui/icons-material/Dashboard'

const Login = lazy(() => import('@/screens/login/LoginForm'))
const Dashboard = lazy(() => import('@/screens/dashboard/Dashboard'))

const NAVIGATION: Navigation = [
  {
    segment: '/',
    title: 'Dashboard',
    icon: <DashboardIcon />,
  },
  {
    segment: '/orders',
    title: 'Orders',
    icon: <ShoppingCartIcon />,
  },
]

export function App() {
  return (
    <AppProvider theme={mdTheme} branding={Branding} navigation={NAVIGATION}>
      <ErrorBoundary>
        <Switch>
          <Route path={'/login'} component={Login} />
          <Route path={'/dashboard'} component={Dashboard} nest />
          {/* Default route: redirect to dashboard, keep as last item*/}
          <Redirect to={'/dashboard'} />
        </Switch>
      </ErrorBoundary>
    </AppProvider>
  )
}
