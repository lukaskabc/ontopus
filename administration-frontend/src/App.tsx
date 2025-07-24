import '@/config/i18n.ts'
import '@/assets/theme.scss'
import { type Navigation } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import lazy, { ErrorBoundary } from 'preact-iso/lazy'
import { Redirect, Route, Switch } from 'wouter-preact'
import PublicIcon from '@mui/icons-material/Public'
import ExtensionIcon from '@mui/icons-material/Extension'
import { useTranslation, type UseTranslationResponse } from 'react-i18next'
import { useMemo } from 'preact/hooks'
import WouterAppProvider from '@/components/WouterAppProvider.tsx'

const Login = lazy(() => import('@/login/LoginForm'))
const Dashboard = lazy(() => import('@/dashboard/Dashboard'))

const navigationProvider = ({
  t,
}: UseTranslationResponse<any, any>): Navigation => [
  {
    segment: 'plugins',
    title: t('dashboard.menu.plugins'),
    icon: <ExtensionIcon />,
    children: [
      {
        segment: 'github',
        title: 'github',
      },
    ],
  },
  {
    segment: 'ontologies',
    title: t('dashboard.menu.ontologies'),
    icon: <PublicIcon />,
  },
]

export function App() {
  const translation = useTranslation()
  const navigation = useMemo(() => {
    console.debug('reloading navigation')
    return navigationProvider(translation)
  }, [translation])

  return (
    <WouterAppProvider
      theme={mdTheme}
      branding={Branding}
      navigation={navigation}
    >
      <ErrorBoundary>
        <Switch>
          <Route path={'/login'} component={Login} />
          <Route path={'/'} component={Dashboard} nest />
          {/* Default route: redirect to dashboard, keep as last item*/}
          <Redirect to={'/'} />
        </Switch>
      </ErrorBoundary>
    </WouterAppProvider>
  )
}
