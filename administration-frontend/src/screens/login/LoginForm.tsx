import '@/config/i18n.ts'
import { useTranslation } from 'react-i18next'
import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import { SignInPage } from '@toolpad/core/SignInPage'
import PasswordField from './PasswordField.tsx'
import UsernameField from './UsernameField.tsx'

export default function LoginForm() {
  const { t } = useTranslation()

  const credentialsProvider = { id: 'credentials', name: 'Provider name' }
  const locale = {
    signInTitle: t('login.title'),
    providerSignInTitle: (_: any) => t('login.button.submit'),
    email: t('login.field.username.title'),
    password: t('login.field.password.title'),
  }

  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      <SignInPage
        providers={[credentialsProvider]}
        signIn={(provider, formData) =>
          new Promise((resolve) => {
            console.debug(provider, formData)
            setTimeout(
              () =>
                resolve({
                  error: 'Login failed, no server connection',
                  // success: '',
                }),
              3000
            )
          })
        }
        localeText={locale}
        slots={{
          passwordField: PasswordField,
          emailField: UsernameField,
          subtitle: () => <></>,
        }}
        slotProps={{
          form: {
            id: 'login-form',
          },
          submitButton: {
            color: 'primary',
          },
        }}
      />
    </AppProvider>
  )
}
