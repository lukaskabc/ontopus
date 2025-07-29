import { Button } from '@mui/material'
import { useTranslation } from 'react-i18next'

export default function CustomButton() {
  const { t } = useTranslation('local')
  return (
    <Button
      type="submit"
      variant="outlined"
      color="info"
      size="small"
      disableElevation
      fullWidth
      sx={{ my: 2, lineHeight: 1.75 }}
    >
      {t('login.button.submit')}
    </Button>
  )
}
