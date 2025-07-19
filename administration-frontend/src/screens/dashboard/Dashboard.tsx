import '@/config/i18n.ts'
import { useTranslation } from 'react-i18next'
import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '@/config/theme.tsx'
import { SignInPage } from '@toolpad/core/SignInPage'

export default function Dashboard() {
  const { t } = useTranslation()

  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      <SignInPage
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
        slots={{
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
