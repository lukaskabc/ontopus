import '@/assets/theme.scss'
import '@/config/i18n.ts'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy from 'preact-iso/lazy'
import { Route, Router, Switch } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import Constants from '@/Constants.ts'
import { useEffect } from 'preact/hooks'
import { trackPromise, useLocation } from '@/utils/hooks.ts'
import { authPing } from '@/login/actions.ts'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useTranslation } from 'react-i18next'
import AlertErrorsStack from '@/components/AlertErrorsStack.tsx'
import { Suspense } from 'preact/compat'
import CircularProgress from '@mui/material/CircularProgress'
import Box from '@mui/material/Box'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

const APP_AUTH_PING_PROMISE_AREA = 'APP_AUTH_PING_PROMISE_AREA'

function Loading() {
  return (
    <Box sx={{ my: 4, mx: 'auto', width: 'fit-content' }}>
      <CircularProgress />
    </Box>
  )
}

export function App() {
  const { navigate } = useLocation()
  const { i18n } = useTranslation()

  useEffect(() => {
    return trackPromise(
      authPing().then((authenticated) => {
        if (!authenticated) {
          navigate(Constants.BASE_URL + '/login')
        }
      }),
      APP_AUTH_PING_PROMISE_AREA
    ).abort
  }, [i18n, navigate])

  return (
    <Router base={Constants.BASE_URL}>
      <WouterAppProvider theme={mdTheme} branding={Branding}>
        <AlertErrorsStack>
          <PromiseArea area={APP_AUTH_PING_PROMISE_AREA} useCircleLoading={true}>
            <Suspense fallback={Loading}>
              <Switch>
                <Route path={'/login'} component={Login} />
                <Route path={'/'} component={Dashboard} nest />
              </Switch>
            </Suspense>
          </PromiseArea>
        </AlertErrorsStack>
      </WouterAppProvider>
    </Router>
  )
}
