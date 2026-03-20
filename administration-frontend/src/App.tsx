import '@/assets/theme.scss'
import '@/config/i18n.ts'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Route, Router, Switch } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import Constants from '@/Constants.ts'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

export function App() {
  return (
    <Router base={Constants.BASE_URL}>
      <WouterAppProvider theme={mdTheme} branding={Branding}>
        <ErrorBoundary>
          <Switch>
            <Route path={'/login'} component={Login} />
            <Route path={'/'} component={Dashboard} nest />
          </Switch>
        </ErrorBoundary>
      </WouterAppProvider>
    </Router>
  )
}
