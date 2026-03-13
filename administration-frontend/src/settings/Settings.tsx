import { Paper } from '@mui/material'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import SettingsForm from '@/settings/SettingsForm.tsx'

const SETTINGS_FORM_PROMISE_AREA = 'SETTINGS_FORM_PROMISE_AREA'

export default function Settings() {
  return (
    <Paper sx={{ p: 2 }}>
      <PromiseArea area={SETTINGS_FORM_PROMISE_AREA}>
        <SettingsForm />
      </PromiseArea>
    </Paper>
  )
}
