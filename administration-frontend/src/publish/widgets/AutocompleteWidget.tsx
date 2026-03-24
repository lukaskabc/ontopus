import type { WidgetProps } from '@rjsf/utils'
import Autocomplete from '@mui/material/Autocomplete'
import TextField from '@mui/material/TextField'
import type { JSX } from 'preact'

interface AutocompleteUiOptions {
  freeSolo?: boolean
  disableClearable?: boolean
  autoHighlight?: boolean
  autoSelect?: boolean
  clearOnBlur?: boolean
  handleHomeEndKeys?: boolean
  openOnFocus?: boolean
  selectOnFocus?: boolean
}

// Bridge Preact+MUI typing differences for Autocomplete render params.
type CompatibleTextFieldProps = Record<string, unknown>
const CompatibleTextField = TextField as unknown as (props: CompatibleTextFieldProps) => JSX.Element

/**
 * Renders a {@link @mui/material/Autocomplete} component.
 * It provides a dropdown with suggestions based on the `examples` array provided in the JSON schema.
 *
 * Supported UI schema options (`ui:options`):
 * - `freeSolo` (boolean): If true, allows the user to type and submit arbitrary string values
 *   not present in the `examples` list.
 * - `disableClearable` (boolean): If true, the clear icon is not rendered.
 * - `autoHighlight` (boolean): If true, highlights the first matching option.
 * - `autoSelect` (boolean): If true, the highlighted option is selected on blur.
 * - `clearOnBlur` (boolean): If true, clears typed text when no option is selected.
 * - `handleHomeEndKeys` (boolean): If true, Home/End keys navigate within options.
 * - `openOnFocus` (boolean): If true, opens the option popup on focus.
 * - `selectOnFocus` (boolean): If true, selects the current input text on focus.
 */
export default function AutocompleteWidget(props: WidgetProps) {
  const {
    id,
    schema,
    value,
    required,
    disabled,
    readonly,
    autofocus,
    onChange,
    onBlur,
    onFocus,
    options: uiOptions,
    label,
  } = props

  const schemaOptions = Array.isArray(schema.examples)
    ? schema.examples.filter((example) => typeof example === 'string')
    : []

  const {
    freeSolo = false,
    disableClearable = false,
    autoHighlight,
    autoSelect,
    clearOnBlur,
    handleHomeEndKeys,
    openOnFocus,
    selectOnFocus,
  } = (uiOptions ?? {}) as AutocompleteUiOptions

  const normalizedValue = typeof value === 'string' ? value : value == null ? null : String(value)

  return (
    <Autocomplete
      id={id}
      options={schemaOptions}
      value={normalizedValue}
      freeSolo={freeSolo}
      disableClearable={disableClearable}
      autoHighlight={autoHighlight}
      autoSelect={autoSelect}
      clearOnBlur={clearOnBlur}
      handleHomeEndKeys={handleHomeEndKeys}
      openOnFocus={openOnFocus}
      selectOnFocus={selectOnFocus}
      disabled={disabled || readonly}
      forcePopupIcon={true}
      onChange={(_event, newValue) => {
        onChange(newValue)
      }}
      onInputChange={(_event, newInputValue, reason) => {
        if (freeSolo && reason === 'input') {
          onChange(newInputValue)
        }
      }}
      onBlur={() => onBlur(id, normalizedValue ?? undefined)}
      onFocus={() => onFocus(id, normalizedValue ?? undefined)}
      renderInput={(params) => (
        <CompatibleTextField
          {...params}
          label={label || schema.title}
          required={required}
          autoFocus={autofocus}
          variant="outlined"
          fullWidth
        />
      )}
    />
  )
}
