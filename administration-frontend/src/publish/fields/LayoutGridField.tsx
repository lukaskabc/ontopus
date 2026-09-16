import { getDefaultRegistry } from '@rjsf/core'
import type { FieldProps } from '@rjsf/utils'

const RjsfLayoutGridField = getDefaultRegistry().fields.LayoutGridField

/**
 * Prevent an array item's generated title (for example, "Item-1") from being
 * forwarded by RJSF's LayoutGridField to every string field in the layout.
 * Each child can then use its own ui:title or JSON Schema title.
 */
export default function LayoutGridField(props: FieldProps) {
  return <RjsfLayoutGridField {...props} title={undefined} />
}
