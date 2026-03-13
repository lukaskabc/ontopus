import '@/assets/theme.scss'
import '@/config/i18n.ts'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Route, Switch } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import { useLayoutEffect } from 'preact/hooks'
import { authPing } from '@/login/actions.ts'
import { useLocation } from '@/utils/hooks.ts'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

function appendSlash(path: string) {
  if (!path.endsWith('/')) {
    return path + '/'
  }
  return path
}

const loginPath = appendSlash(import.meta.env.BASE_URL) + 'login'

export function App() {
  const { navigate } = useLocation()
  useLayoutEffect(() => {
    authPing().then((loggedIn) => {
      if (!loggedIn) {
        navigate(loginPath)
      }
    })
  }, [navigate])

  return (
    <WouterAppProvider theme={mdTheme} branding={Branding}>
      <ErrorBoundary>
        <Switch>
          <Route path={'/login'} component={Login} />
          <Route path={'/'} component={Dashboard} nest />
        </Switch>
      </ErrorBoundary>
    </WouterAppProvider>
  )
}
