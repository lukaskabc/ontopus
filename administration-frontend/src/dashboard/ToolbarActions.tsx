import Stack from '@mui/material/Stack'
import { IconButton, Tooltip } from '@mui/material'
import { Settings as SettingsIcon } from '@mui/icons-material'

export default function ToolbarActions() {
  return (
    <Stack direction="row" alignItems="center" spacing={2}>
      <Tooltip title="Settings">
        <IconButton aria-label="settings" color="inherit">
          <SettingsIcon color={'primary'} />
        </IconButton>
      </Tooltip>
    </Stack>
  )
}
