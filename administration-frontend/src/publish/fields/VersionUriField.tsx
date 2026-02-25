import { type FieldProps } from '@rjsf/utils'
import { useCallback, useMemo, useState } from 'preact/hooks'
import { Box, Chip, IconButton, Paper, TextField, Typography } from '@mui/material'
import ArrowBackIcon from '@mui/icons-material/ArrowBack'
import ArrowForwardIcon from '@mui/icons-material/ArrowForward'
import VersionUriTemplate from '@/model/VersionUriTemplate.ts'

const VERSION_SEGMENT = '{version}'

interface SegmentedUri {
  /**
   * scheme + host
   * e.g. 'http://example.com'
   */
  host: string
  /**
   * path segments, e.g. ['api', '{version}', 'users']
   * The last segment may contain fragment and query parameters.
   */
  segments: string[]
}

/**
 * Splits a URI into scheme+host and path segments.
 */
function parseUri(uri: string): SegmentedUri {
  // match two groups: 1) scheme + host, 2) path
  // TODO write tests
  const schemeMatch = uri.match(/^(https?:\/\/[^\/]*)\/(.*)$/)
  if (schemeMatch) {
    const host = schemeMatch[1]
    const path = schemeMatch[2]
    const segments = path.split('/').filter((s) => s.length > 0)
    return { host, segments }
  }
  throw new Error(`Failed to parse URI: ${uri}. Expected format: http(s)://host/path/{version}/...`)
}

/**
 * Reconstructs the URI
 */
function buildUri(segmented: SegmentedUri): string {
  const { host, segments } = segmented
  const path = segments.join('/')
  return `${host}/${path}`
}

interface UriSegmentProps {
  segment: string
  version: string
  id: number
  segmentsCount: number
}
function UriSegment({ segment, version, id, segmentsCount }: UriSegmentProps) {
  const isVersion = segment === VERSION_SEGMENT
  const label = isVersion ? version : segment

  return (
    <Box sx={{ display: 'inline-flex', alignItems: 'center' }}>
      <Chip
        label={label}
        size="small"
        color={isVersion ? 'primary' : 'default'}
        variant={isVersion ? 'filled' : 'outlined'}
      />

      {id < segmentsCount - 1 && (
        <Typography variant="body2" sx={{ color: 'text.secondary', pl: 0.5 }}>
          /
        </Typography>
      )}
    </Box>
  )
}

function VersionUriField(props: FieldProps) {
  const { onChange, fieldPathId } = props

  const [value, setValue] = useState<VersionUriTemplate>(() => VersionUriTemplate.fromJson(props.formData))

  const { host, segments } = useMemo(() => parseUri(value.uri), [value])

  const updateValue = useCallback(
    (next: VersionUriTemplate) => {
      setValue(next)
      onChange(next, fieldPathId?.path)
    },
    [onChange, fieldPathId]
  )

  /** Move the version segment left or right. */
  const moveVersion = useCallback(
    (direction: -1 | 1) => {
      const id = segments.indexOf(VERSION_SEGMENT)
      if (id === -1) return
      const target = id + direction
      if (target < 0 || target >= segments.length) return
      const newValue = [...segments]
      const tmp = newValue[target]
      newValue[target] = newValue[id]
      newValue[id] = tmp

      const version = value.version
      updateValue({ version, uri: buildUri({ host, segments: newValue }) })
    },
    [host, segments, value, updateValue]
  )

  /** Compute the resolved URI for preview (replace version segment with version). */
  const resolvedUri = useMemo(() => {
    if (!value.version) return value.uri
    return value.uri.replace(VERSION_SEGMENT, value.version)
  }, [value.uri, value.version])

  const versionIndex = segments.indexOf(VERSION_SEGMENT)

  return (
    <>
      <Paper variant="outlined" sx={{ p: 1 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', flexWrap: 'wrap', gap: 0.5 }}>
          <UriSegment segment={host} version={value.version} id={0} segmentsCount={2} />
          {segments.map((segment, id) => (
            <UriSegment segment={segment} version={value.version} id={id} segmentsCount={segments.length} />
          ))}
        </Box>
      </Paper>

      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 1, marginY: 2 }}>
        <IconButton size="small" disabled={versionIndex === 0} onClick={() => moveVersion(-1)}>
          <ArrowBackIcon fontSize="small" />
        </IconButton>

        <IconButton size="small" disabled={versionIndex === segments.length - 1} onClick={() => moveVersion(1)}>
          <ArrowForwardIcon fontSize="small" />
        </IconButton>
      </Box>

      <TextField
        variant={'outlined'}
        type={'text'}
        value={resolvedUri}
        fullWidth
        disabled
        slotProps={{ htmlInput: { readOnly: true, style: { textAlign: 'center' } } }}
      />
    </>
  )
}

export default VersionUriField
