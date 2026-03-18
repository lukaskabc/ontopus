import Stack from '@mui/material/Stack'
import { Branding } from '@/config/theme.tsx'
import { useTranslation } from 'react-i18next'
import { useCallback } from 'preact/hooks'
import { useLocation } from 'wouter-preact'
import { withoutTrailingSlash } from '@/Constants.ts'

import Button from '@mui/material/Button'

import Typography from '@mui/material/Typography'
import { styled, useTheme } from '@mui/material/zero-styled'

const LogoContainer = styled('div')({
  position: 'relative',
  height: 40,
  display: 'flex',
  alignItems: 'center',
  '& img': {
    maxHeight: 40,
  },
})

function HeaderText({ children, bold = true }: { children?: React.ReactNode; bold?: boolean }) {
  const theme = useTheme()
  return (
    <Typography
      variant="h6"
      sx={{
        color: (theme.vars ?? theme).palette.primary.main,
        fontWeight: bold ? '700' : '400',
        ml: 1,
        whiteSpace: 'nowrap',
        lineHeight: 1,
      }}
    >
      {children}
    </Typography>
  )
}

// https://github.com/mui/toolpad/blob/v0.16.0/packages/toolpad-core/src/DashboardLayout/AppTitle.tsx
function AppTitle() {
  return (
    // <Link href={Branding?.homeUrl} style={{ textDecoration: 'none' }}>
    // </Link>
    <Stack direction="row" alignItems="center">
      <LogoContainer>{Branding?.logo}</LogoContainer>
      <HeaderText>{Branding?.title}</HeaderText>
    </Stack>
  )
}

export default function Header() {
  const { t } = useTranslation()
  const [dirtyLocation, navigate] = useLocation()
  const location = withoutTrailingSlash(dirtyLocation)
  const navigateToOntologies = useCallback(() => navigate('/'), [navigate])

  const isOntologiesLocation = location === '/ontologies'

  return (
    <Stack direction="row" alignItems="center" spacing={2}>
      <AppTitle />
      <Button
        variant={isOntologiesLocation ? 'outlined' : 'contained'}
        onClick={navigateToOntologies}
        disabled={isOntologiesLocation}
      >
        <b>{t('navigation.ontologies')}</b>
      </Button>
    </Stack>
  )
}
