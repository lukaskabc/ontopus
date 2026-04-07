import type { FieldProps } from '@rjsf/utils'
import Autocomplete from '@mui/material/Autocomplete'
import Box from '@mui/material/Box'
import FormHelperText from '@mui/material/FormHelperText'
import Stack from '@mui/material/Stack'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import { RichDescription } from '@rjsf/core'
import { useEffect, useMemo, useState } from 'preact/hooks'
import type { JSX } from 'preact'

type MultilingualValue = Record<string, string>

interface MultilingualUiOptions {
  multiline?: boolean
  rows?: number
  minRows?: number
  maxRows?: number
}

// Bridge Preact+MUI typing differences for Autocomplete render params.
type CompatibleTextFieldProps = Record<string, unknown>
const CompatibleTextField = TextField as unknown as (props: CompatibleTextFieldProps) => JSX.Element

function asMultilingualValue(formData: unknown): MultilingualValue {
  if (!formData || typeof formData !== 'object' || Array.isArray(formData)) {
    return {}
  }

  const value: MultilingualValue = {}
  Object.entries(formData).forEach(([lang, text]) => {
    if (typeof text === 'string') {
      value[lang] = text
    }
  })
  return value
}

function toValue(map: MultilingualValue): MultilingualValue {
  const value: MultilingualValue = {}
  Object.entries(map).forEach(([lang, text]) => {
    const tag = lang.trim()
    if (!tag) {
      return
    }
    value[tag] = text
  })
  return value
}

function getSchemaLanguageOptions(schema: unknown): string[] {
  if (!schema || typeof schema !== 'object') {
    return []
  }

  const propertyNames = (schema as { propertyNames?: unknown }).propertyNames
  if (!propertyNames || typeof propertyNames !== 'object') {
    return []
  }

  const enumValues = (propertyNames as { enum?: unknown }).enum
  if (!Array.isArray(enumValues)) {
    return []
  }

  return enumValues.filter((value): value is string => typeof value === 'string')
}

function MultilingualStringField(props: FieldProps) {
  const { formData, onChange, disabled, readonly, fieldPathId, schema, required, rawErrors } = props
  const value = useMemo(() => asMultilingualValue(formData), [formData])
  const isDisabled = !!(disabled || readonly)
  const [translations, setTranslations] = useState<MultilingualValue>(value)
  const [selectedLanguage, setSelectedLanguage] = useState<string>(Object.keys(value)[0] ?? '')

  const title = props.uiSchema?.['ui:title'] ?? props.schema.title
  const description = props.uiSchema?.['ui:description'] ?? props.schema.description
  const help = props.uiSchema?.['ui:help'] ?? props.schema.help
  const uiOptions = (props.uiSchema?.['ui:options'] ?? {}) as MultilingualUiOptions
  const isMultiline = !!uiOptions.multiline

  const schemaLanguageOptions = useMemo(() => getSchemaLanguageOptions(schema), [schema])

  const languageOptions = useMemo(() => {
    return Array.from(new Set([...schemaLanguageOptions, ...Object.keys(translations)])).filter(
      (language) => language.trim() !== ''
    )
  }, [translations, schemaLanguageOptions])

  useEffect(() => {
    setTranslations(value)
    const keys = Object.keys(value)
    if (keys.length === 0) {
      setSelectedLanguage('')
      return
    }

    // Preserve current selection when still present; otherwise fallback to first key.
    setSelectedLanguage((current) =>
      current && Object.prototype.hasOwnProperty.call(value, current) ? current : (keys[0] ?? '')
    )
  }, [value])

  const emitChange = (nextMap: MultilingualValue) => {
    const sanitized = toValue(nextMap)
    setTranslations(sanitized)
    onChange(sanitized, fieldPathId.path)
  }

  const onLanguageChange = (_: unknown, newValue: string | null) => {
    const nextLanguage = (newValue ?? '').trim()

    const previousLanguage = selectedLanguage.trim()
    if (previousLanguage && previousLanguage !== nextLanguage && (translations[previousLanguage] ?? '') === '') {
      const withoutPrevious = Object.fromEntries(
        Object.entries(translations).filter(([key]) => key !== previousLanguage)
      ) as MultilingualValue
      emitChange(withoutPrevious)
    }

    setSelectedLanguage(nextLanguage)
  }

  const onValueChange = (newText: string) => {
    const language = selectedLanguage.trim()
    if (!language) {
      return
    }

    emitChange({ ...translations, [language]: newText })
  }

  return (
    <Stack spacing={1.5}>
      {description && (
        <Typography variant="body2" color={'text.secondary'} component={'div'}>
          <RichDescription description={description} uiSchema={props.uiSchema} registry={props.registry} />
        </Typography>
      )}

      <Stack spacing={1.5} direction={'row'}>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          <Autocomplete
            size="small"
            freeSolo
            options={languageOptions}
            value={selectedLanguage || null}
            onChange={onLanguageChange}
            onInputChange={(_, newInput, reason) => {
              if (reason === 'input' || reason === 'clear') {
                setSelectedLanguage(newInput.trim())
              }
            }}
            disabled={isDisabled}
            sx={{ minWidth: 200 }}
            renderInput={(params) => <CompatibleTextField {...params} label={'Language'} />}
          />
        </Box>

        <TextField
          size="small"
          label={title ?? 'Value'}
          value={selectedLanguage.trim() ? (translations[selectedLanguage.trim()] ?? '') : ''}
          onChange={(e) => onValueChange(e.currentTarget.value)}
          disabled={isDisabled || selectedLanguage.trim() === ''}
          multiline={isMultiline}
          rows={isMultiline ? uiOptions.rows : undefined}
          minRows={isMultiline ? uiOptions.minRows : undefined}
          maxRows={isMultiline ? uiOptions.maxRows : undefined}
          fullWidth
        />
      </Stack>

      {help && <FormHelperText>{String(help)}</FormHelperText>}
      {rawErrors?.length ? <FormHelperText error>{rawErrors.join(', ')}</FormHelperText> : null}
    </Stack>
  )
}

export default MultilingualStringField
