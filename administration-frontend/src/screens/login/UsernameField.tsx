import { FormControl, InputLabel, OutlinedInput } from '@mui/material'
import { useTranslation } from 'react-i18next'

export default function () {
  const { t } = useTranslation()
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
