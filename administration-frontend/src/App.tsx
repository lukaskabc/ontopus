import '@/assets/theme.scss'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Redirect, Route, Switch, useLocation } from 'wouter-preact'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'
import { useEffect } from 'preact/hooks'
import { authPing } from '@/login/actions.ts'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

// const navigationProvider = ({
//   t,
// }: UseTranslationResponse<any, any>): Navigation => [
//   {
//     segment: 'plugins',
//     title: t('dashboard.menu.plugins'),
//     icon: <ExtensionIcon />,
//     children: [
//       {
//         segment: 'git',
//         title: 'git',
//       },
//     ],
//   },
//   {
//     segment: 'ontologies',
//     title: t('dashboard.menu.ontologies'),
//     icon: <PublicIcon />,
//   },
// ]

export function App() {
  // const translation = useTranslation()
  // const navigation = useMemo(() => {
  //   console.debug('reloading navigation')
  //   return navigationProvider(translation)
  // }, [translation])
  const [_, navigate] = useLocation()
  useEffect(() => {
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
          <Redirect to={'/login'} />
        </Switch>
      </ErrorBoundary>
    </WouterAppProvider>
  )
}
