import {
  type FormContextType,
  getTemplate,
  type RJSFSchema,
  type StrictRJSFSchema,
  type WidgetProps,
} from '@rjsf/utils'
import type { ChangeEvent } from 'preact/compat'

/**
 *  File widget allowing to reuse a file that is already cached on the server
 */
function ReusableFileWidget<T = any, S extends StrictRJSFSchema = RJSFSchema, F extends FormContextType = any>(
  props: WidgetProps<T, S, F>
) {
  const { disabled, readonly, required, value, options, registry } = props
  const BaseInputTemplate = getTemplate<'BaseInputTemplate', T, S, F>('BaseInputTemplate', registry, options)
  props.multiple = !!(props.multiple || props.options?.multiple)

  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (!event?.target?.files) {
      // skip if there are is no files field
      return
    }
    // process files, perhaps serialize them to some JSON?
    // what about making an array of objects, each will have a tag whether it is a new file to upload
    // or an existing file from server
    // and then just serialize it to JSON
    // but then I would have a JSON inside JSON
    // so perhaps just leave it and we will use this widget with object field
  }

  return (
    <div>
      <BaseInputTemplate
        {...props}
        disabled={disabled || readonly}
        value={value || ''}
        onChange={onChange}
        type="file"
        required={value ? false : required} // this turns off HTML required validation when a value exists
        accept={options.accept ? String(options.accept) : undefined}
      />
    </div>
  )
}

export default ReusableFileWidget
