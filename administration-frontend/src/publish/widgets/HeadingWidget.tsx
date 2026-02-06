import type { WidgetProps } from '@rjsf/utils'
import { Typography } from '@mui/material'

const HeadingWidget = (props: WidgetProps) => {
  const { component, variant } = props.options
  return (
    <Typography variant={variant} component={component}>
      {props.label}
    </Typography>
  )
}

export default HeadingWidget
