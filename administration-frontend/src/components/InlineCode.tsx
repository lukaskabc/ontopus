import Typography from '@mui/material/Typography'
import type { SxProps } from '@mui/system'
import type { Theme } from '@mui/material/styles'

export default function InlineCode({ value, sx }: { value?: string; sx?: SxProps<Theme> }) {
  return (
    <Typography
      component="code"
      sx={{
        backgroundColor: 'action.hover',
        padding: '2px 4px',
        borderRadius: 1,
        fontFamily: 'Monospace',
        fontSize: '0.875em',
        ...sx,
      }}
    >
      {value}
    </Typography>
  )
}
