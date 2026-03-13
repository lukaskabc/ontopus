import Stack from '@mui/material/Stack'
import SettingsMenuButton from '@/dashboard/SettingsMenuButton.tsx'

export default function ToolbarActions() {
  return (
    <Stack direction="row" alignItems="center" spacing={2}>
      <SettingsMenuButton />
    </Stack>
  )
}
