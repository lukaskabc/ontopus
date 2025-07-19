import '../../i18n.ts'
import { AppProvider } from '@toolpad/core/AppProvider'
import mdTheme, { Branding } from '../../theme.tsx'

export function Dashboard() {
  return (
    <AppProvider theme={mdTheme} branding={Branding}>
      Administration dashboard
    </AppProvider>
  )
}
