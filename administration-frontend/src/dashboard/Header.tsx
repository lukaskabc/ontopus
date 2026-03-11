import Stack from '@mui/material/Stack'
import Link from '@mui/material/Link'
import { Branding } from '@/config/theme.tsx'
import { Button, styled, Typography, useTheme } from '@mui/material'
import { useTranslation } from 'react-i18next'
import { useCallback } from 'preact/hooks'
import { useLocation } from 'wouter-preact'

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
    <Link href={Branding?.homeUrl} style={{ textDecoration: 'none' }}>
      <Stack direction="row" alignItems="center">
        <LogoContainer>{Branding?.logo}</LogoContainer>
        <HeaderText>{Branding?.title}</HeaderText>
      </Stack>
    </Link>
  )
}

export default function Header() {
  const { t } = useTranslation()
  const [_, navigate] = useLocation()
  const navigateToOntologies = useCallback(() => navigate('/'), [navigate])
  return (
    <Stack direction="row" alignItems="center" spacing={2}>
      <AppTitle />
      <Button variant={'outlined'} onClick={navigateToOntologies}>
        <b>{t('navigation.ontologies')}</b>
      </Button>
    </Stack>
  )
}
