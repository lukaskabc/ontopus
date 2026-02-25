import { type FieldProps } from '@rjsf/utils'
import { useCallback, useMemo, useState } from 'preact/hooks'
import { Box, Chip, IconButton, Paper, Stack, Typography } from '@mui/material'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import ArrowForwardIcon from '@mui/icons-material/ArrowForward'

const VERSION_PLACEHOLDER = '{version}'

interface VersionUriValue {
  uri: string
  version: string
}

/** Split a URI into scheme+authority prefix and path segments. */
function parseUri(uri: string): { prefix: string; segments: string[] } {
  // Match scheme://authority  (e.g. "http://example.com")
  const schemeMatch = uri.match(/^(https?:\/\/[^/]*)\/(.*)$/)
  if (schemeMatch) {
    const prefix = schemeMatch[1]
    const path = schemeMatch[2]
    const segments = path.split('/').filter((s) => s.length > 0)
    return { prefix, segments }
  }
  // Fallback – treat whole string as segments
  const segments = uri.split('/').filter((s) => s.length > 0)
  return { prefix: '', segments }
}

/** Reconstruct the URI from prefix + segments. */
function buildUri(prefix: string, segments: string[]): string {
  const path = segments.join('/')
  return prefix ? `${prefix}/${path}` : path
}

/** Parse the initial form data into the component value. */
function parseFormData(formData?: any): VersionUriValue {
  if (formData && typeof formData === 'object') {
    return {
      uri: typeof formData.uri === 'string' ? formData.uri : '',
      version: typeof formData.version === 'string' ? formData.version : '',
    }
  }
  return { uri: '', version: '' }
}

/**
 * Field component for react-jsonschema-form that manages a versioned URI.
 *
 * Value shape: `{ uri: string, version: string }`
 *
 * The URI is split at `/` and each path segment is displayed as an inline chip.
 * The special `{version}` segment is highlighted and its display value is the
 * URL-encoded version string. The user can reorder segments (including the
 * version placeholder) with arrow buttons and edit the version string.
 */
function VersionUriField(props: FieldProps) {
  const { onChange, fieldPathId } = props

  const [value, setValue] = useState<VersionUriValue>(() => parseFormData(props.formData))

  const { prefix, segments } = useMemo(() => parseUri(value.uri), [value.uri])

  const propagate = useCallback(
    (next: VersionUriValue) => {
      setValue(next)
      onChange(next, fieldPathId?.path)
    },
    [onChange, fieldPathId]
  )

  /** Move the {version} segment left or right. */
  const moveVersion = useCallback(
    (direction: -1 | 1) => {
      const idx = segments.indexOf(VERSION_PLACEHOLDER)
      if (idx === -1) return
      const target = idx + direction
      if (target < 0 || target >= segments.length) return
      const next = [...segments]
      ;[next[idx], next[target]] = [next[target], next[idx]]
      propagate({ ...value, uri: buildUri(prefix, next) })
    },
    [segments, prefix, value, propagate]
  )

  /** Compute the resolved URI for preview (replace {version} with encoded version). */
  const resolvedUri = useMemo(() => {
    if (!value.version) return value.uri
    return value.uri.replace(VERSION_PLACEHOLDER, encodeURIComponent(value.version))
  }, [value.uri, value.version])

  const versionIndex = segments.indexOf(VERSION_PLACEHOLDER)

  return (
    <Stack spacing={2}>
      {/* Segment reorder UI */}
      <Box>
        <Typography variant="subtitle2" gutterBottom>
          URI path segments
        </Typography>
        <Paper variant="outlined" sx={{ p: 1 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 0.5 }}>
            {/* Scheme + authority prefix (non-editable) */}
            {prefix && (
              <Typography variant="body2" sx={{ mr: 0.5, color: 'text.secondary' }}>
                {prefix}/
              </Typography>
            )}

            {segments.map((segment, idx) => {
              const isVersion = segment === VERSION_PLACEHOLDER
              const label = isVersion
                ? value.version
                  ? encodeURIComponent(value.version)
                  : VERSION_PLACEHOLDER
                : segment

              return (
                <Box key={`${idx}-${segment}`} sx={{ display: 'inline-flex', alignItems: 'center' }}>
                  <Chip
                    label={label}
                    size="small"
                    color={isVersion ? 'primary' : 'default'}
                    variant={isVersion ? 'filled' : 'outlined'}
                  />

                  {idx < segments.length - 1 && (
                    <Typography variant="body2" sx={{ mx: 0.25, color: 'text.secondary' }}>
                      /
                    </Typography>
                  )}
                </Box>
              )
            })}
          </Box>
        </Paper>

        {versionIndex !== -1 && (
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, mt: 1 }}>
            <IconButton
              size="small"
              disabled={versionIndex === 0}
              onClick={() => moveVersion(-1)}
              aria-label="move version left"
            >
              <ArrowBackIcon fontSize="small" />
            </IconButton>
            <Typography variant="body2" color="text.secondary">
              Move version
            </Typography>
            <IconButton
              size="small"
              disabled={versionIndex === segments.length - 1}
              onClick={() => moveVersion(1)}
              aria-label="move version right"
            >
              <ArrowForwardIcon fontSize="small" />
            </IconButton>
          </Box>
        )}
      </Box>

      {/* Resolved URI preview */}
      {value.uri && (
        <Box>
          <Typography variant="subtitle2" gutterBottom>
            Resolved URI
          </Typography>
          <Typography
            variant="body2"
            sx={{
              wordBreak: 'break-all',
              color: versionIndex === -1 ? 'warning.main' : 'text.primary',
            }}
          >
            {resolvedUri}
          </Typography>
          {versionIndex === -1 && (
            <Typography variant="caption" color="warning.main">
              URI does not contain a {VERSION_PLACEHOLDER} segment
            </Typography>
          )}
        </Box>
      )}
    </Stack>
  )
}

export default VersionUriField
