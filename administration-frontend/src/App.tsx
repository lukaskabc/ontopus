import '@/config/i18n.ts'
import '@/assets/theme.scss'
import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import {
  ErrorBoundary,
  lazy,
  LocationProvider,
  Route,
  Router,
} from 'preact-iso'

const Login = lazy(() => import('@/screens/login/LoginForm'))
const Dashboard = lazy(() => import('@/screens/dashboard/Dashboard'))

export function App() {
  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      <LocationProvider>
        <ErrorBoundary>
          <Router>
            <Route path={'/login'} component={Login} />
            <Route default component={Dashboard} />
          </Router>
        </ErrorBoundary>
      </LocationProvider>
    </AppProvider>
  )
}
