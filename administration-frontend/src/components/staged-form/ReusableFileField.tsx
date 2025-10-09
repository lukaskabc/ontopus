import { type FieldProps, type FormContextType, type RJSFSchema, type StrictRJSFSchema } from '@rjsf/utils'
import { ReusableFile } from '@/model/ReusableFile.ts'
import { useCallback } from 'preact/hooks'
import ReusableFileWidget from '@/components/staged-form/ReusableFileWidget.tsx'

/**
 *  File field holding an array of {@link ReusableFile ReusableFiles}
 */
function ReusableFileField<
  T extends ReusableFile[],
  S extends StrictRJSFSchema = RJSFSchema,
  F extends FormContextType = any,
>(props: FieldProps<T, S, F>) {
  const { onChange, fieldPathId } = props
  props.multiple = !!(props.multiple || props.options?.multiple)

  const onInputChange = useCallback((files: ReusableFile[]) => onChange(files as T, fieldPathId.path), [onChange])

  return (
    <ReusableFileWidget
      options={{ ...props.uiSchema }}
      schema={props.schema}
      uiSchema={props.uiSchema}
      id={fieldPathId.$id}
      name={props.name}
      label={props.label}
      hideLabel={!props.displayLabel}
      hideError={props.hideError}
      value={props.formData}
      onChange={onInputChange}
      onBlur={props.onBlur}
      onFocus={props.onFocus}
      required={props.required}
      disabled={props.disabled}
      readonly={props.readonly}
      formContext={props.formContext}
      autofocus={props.autofocus}
      registry={props.registry}
      placeholder={props.placeholder}
      rawErrors={props.rawErrors}
    />
  )
}

export default ReusableFileField
