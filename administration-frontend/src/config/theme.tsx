import { createTheme } from '@mui/material/styles'
import { red } from '@mui/material/colors'
import Logo from '@/assets/logo.svg?react'
import i18n from '@/config/i18n.ts'

const { t } = i18n

// Create a theme instance.
const mdTheme = createTheme({
  cssVariables: true,
  palette: {
    primary: {
      main: '#009dc9',
      light: '#58c4dd',
      dark: '#005a84',
      contrastText: '#fff',
    },
    secondary: {
      main: '#03dac4',
      light: '#d4f6f2',
      dark: '#008966',
      contrastText: '#000',
    },
    error: {
      main: red.A400,
    },
  },
})

export const Branding = {
  logo: <Logo class={'logo'} />,
  title: t('title'),
  subtitle: t('subtitle'),
  homeUrl: '/dashboard',
}

export default mdTheme
