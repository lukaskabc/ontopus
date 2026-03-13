import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import { SignInPage } from '@toolpad/core/SignInPage'
import PasswordField from './PasswordField.tsx'
import UsernameField from './UsernameField.tsx'
import { trackPromise } from 'react-promise-tracker'
import PromiseAreas from '@/PromiseAreas.ts'
import { useEffect } from 'preact/hooks'
import { authPing, submitLoginForm } from '@/login/actions.ts'
import { useLocation } from '@/utils/hooks.ts'

const credentialsProvider = { id: 'credentials', name: 'Provider name' }

const locale = {
  signInTitle: Branding.title,
  signInSubtitle: Branding.subtitle,
}

export default function LoginForm() {
  const { navigate } = useLocation()

  useEffect(() => {
    authPing().then((loggedIn) => {
      if (loggedIn) {
        navigate('/ontologies')
      }
    })
  })

  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      <SignInPage
        providers={[credentialsProvider]}
        signIn={(_, formData) =>
          trackPromise(
            submitLoginForm(formData).then(async (result) => {
              navigate('/ontologies')
              return result
            }),
            PromiseAreas.LOGIN_FORM_SUBMIT
          )
        }
        localeText={locale}
        slots={{
          passwordField: PasswordField,
          emailField: UsernameField,
        }}
        slotProps={{
          submitButton: {
            color: 'primary',
          },
        }}
      />
    </AppProvider>
  )
}
