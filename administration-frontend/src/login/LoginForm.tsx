import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import { type AuthProvider, SignInPage } from '@toolpad/core/SignInPage'
import PasswordField from './PasswordField.tsx'
import UsernameField from './UsernameField.tsx'
import PromiseAreas from '@/PromiseAreas.ts'
import { submitLoginForm } from '@/login/actions.ts'
import { useCallback, useMemo } from 'preact/hooks'
import { trackPromise } from 'react-promise-tracker'
import { useTranslation } from 'react-i18next'
import type { AuthResponse } from '@toolpad/core'
import { useLocation } from '@/utils/hooks.ts'

const credentialsProvider = { id: 'credentials', name: 'Username and Password' }

export default function LoginForm() {
  const { t, i18n } = useTranslation()
  const { navigate } = useLocation()

  const locale = useMemo(
    () => ({
      signInTitle: Branding.title,
      signInSubtitle: Branding.subtitle,
      providerSignInTitle: () => t('login.button.submit'),
    }),
    [t]
  )

  const onSignIn = useCallback(
    (_: AuthProvider, formData: FormData) =>
      trackPromise(
        submitLoginForm(formData, t)
          .then(
            (): Promise<AuthResponse> =>
              i18n.reloadResources(undefined, ['server']).then(() => {
                navigate('/ontologies')
                return {}
              })
          )
          .catch((authResponse: unknown): AuthResponse => {
            return authResponse as AuthResponse
          }),
        PromiseAreas.LOGIN_FORM_SUBMIT
      ),
    [t, i18n, navigate]
  )

  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      <SignInPage
        providers={[credentialsProvider]}
        signIn={onSignIn}
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
