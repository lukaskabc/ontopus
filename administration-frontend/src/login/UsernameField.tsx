import { useTranslation } from 'react-i18next'

import FormControl from '@mui/material/FormControl'
import InputLabel from '@mui/material/InputLabel'
import OutlinedInput from '@mui/material/OutlinedInput'

export default function () {
  const { t } = useTranslation('local')
  const label = t('login.field.username.title')
  return (
    <FormControl sx={{ my: 2 }} fullWidth variant="outlined">
      <InputLabel size="small" htmlFor="outlined-adornment-username">
        {label}
      </InputLabel>
      <OutlinedInput
        id="outlined-adornment-username"
        type="text"
        name="username"
        size="small"
        required={true}
        label={label}
      />
    </FormControl>
  )
}
