import type { FieldProps, UiSchema } from '@rjsf/utils'
import Typography from '@mui/material/Typography'
import type { TypographyVariant } from '@mui/material'
import type { ElementType } from 'preact/compat'

export interface TypographyFieldUiSchema extends UiSchema {
  component?: ElementType
  variant?: TypographyVariant
}

const TypographyField = (props: FieldProps) => {
  const { component = 'p', variant = 'body1' } = props.uiSchema as TypographyFieldUiSchema
  console.debug(props)
  const title = props.uiSchema?.['ui:title'] ?? props.schema.title
  const description = props.uiSchema?.['ui:description'] ?? props.schema.description
  return (
    <>
      {title && (
        <Typography variant={variant} component={component}>
          {title}
        </Typography>
      )}
      {description && (
        <Typography variant={variant} component={component} whiteSpace={'preserve-breaks'}>
          {description}
        </Typography>
      )}
    </>
  )
}

export default TypographyField
