import '@/assets/theme.scss'
import '@/config/i18n.ts'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Route, Router, Switch } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import Constants from '@/Constants.ts'
import { useEffect } from 'preact/hooks'
import { trackPromise, useLocation } from '@/utils/hooks.ts'
import { authPing } from '@/login/actions.ts'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useTranslation } from 'react-i18next'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

const APP_AUTH_PING_PROMISE_AREA = 'APP_AUTH_PING_PROMISE_AREA'

export function App() {
  const { navigate } = useLocation()
  const { i18n } = useTranslation()

  useEffect(() => {
    return trackPromise(
      authPing().catch(() => {
        navigate(Constants.BASE_URL + '/login')
      }),
      APP_AUTH_PING_PROMISE_AREA
    ).abort
  }, [i18n, navigate])

  return (
    <Router base={Constants.BASE_URL}>
      <WouterAppProvider theme={mdTheme} branding={Branding}>
        <ErrorBoundary>
          <PromiseArea area={APP_AUTH_PING_PROMISE_AREA} useCircleLoading={true}>
            <Switch>
              <Route path={'/login'} component={Login} />
              <Route path={'/'} component={Dashboard} nest />
            </Switch>
          </PromiseArea>
        </ErrorBoundary>
      </WouterAppProvider>
    </Router>
  )
}
