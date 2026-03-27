import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import type { TypographyVariant } from '@mui/material'
import InlineCode from '@/components/InlineCode.tsx'
import type { SxProps } from '@mui/system'
import type { Theme } from '@mui/material/styles'

export interface ReadOnlyFieldProps {
  label: string
  value?: string
  isCode?: boolean
  valueVariant?: TypographyVariant
  sx?: SxProps<Theme>
}

const ReadOnlyField = ({ label, value, isCode = false, valueVariant = 'body1', sx }: ReadOnlyFieldProps) => (
  <Box sx={{ mb: 1, ...sx }}>
    <Typography variant="caption" color="text.secondary" display="block">
      {label}
    </Typography>
    {(isCode && <InlineCode value={value} />) || (
      <Typography variant={valueVariant} color="text.primary" sx={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}>
        {value?.trim() ? value : '—'}
      </Typography>
    )}
  </Box>
)

export default ReadOnlyField
