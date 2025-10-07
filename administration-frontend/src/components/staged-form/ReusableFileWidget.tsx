import {
  type FormContextType,
  getTemplate,
  type RJSFSchema,
  type StrictRJSFSchema,
  type WidgetProps,
} from '@rjsf/utils'

/**
 *  File widget allowing to reuse a file that is already cached on the server
 */
function ReusableFileWidget<T = any, S extends StrictRJSFSchema = RJSFSchema, F extends FormContextType = any>(
  props: WidgetProps<T, S, F>
) {
  const { disabled, readonly, required, value, options, registry } = props
  const BaseInputTemplate = getTemplate<'BaseInputTemplate', T, S, F>('BaseInputTemplate', registry, options)
  const multiple = !!(props.multiple || props?.schema?.multiple || props.options?.multiple)
  return (
    <div>
      <BaseInputTemplate
        {...props}
        multiple={multiple}
        disabled={disabled || readonly}
        type="file"
        required={value ? false : required} // this turns off HTML required validation when a value exists
        accept={options.accept ? String(options.accept) : undefined}
      />
    </div>
  )
}

export default ReusableFileWidget
