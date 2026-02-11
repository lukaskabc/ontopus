import '@/assets/theme.scss'
import '@/config/i18n.ts'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Redirect, Route, Switch, useLocation } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import { useLayoutEffect } from 'preact/hooks'
import { authPing } from '@/login/actions.ts'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

export function App() {
  const [_, navigate] = useLocation()
  useLayoutEffect(() => {
    authPing().then((loggedIn) => {
      if (!loggedIn) {
        navigate('/login')
      }
    })
  }, [])

  return (
    <WouterAppProvider
      theme={mdTheme}
      branding={Branding}
      // navigation={navigation}
    >
      <ErrorBoundary>
        <Switch>
          <Route path={'/login'} component={Login} />
          <Route path={'/ontologies'} component={Dashboard} nest />
          {/* Default route: redirect to dashboard, keep as last item*/}
          <Redirect to={'/ontologies'} />
        </Switch>
      </ErrorBoundary>
    </WouterAppProvider>
  )
}
