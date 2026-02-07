import type { FunctionComponent } from 'preact'
import { type DropzoneOptions, useDropzone } from 'react-dropzone'
import { useMemo } from 'preact/hooks'
import { type Theme, useTheme } from '@mui/material'
import CloudUploadIcon from '@mui/icons-material/CloudUpload'

function makeStyles(theme: Theme) {
  const { palette } = theme
  const baseStyle = {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: '20px',
    borderWidth: 2,
    borderRadius: 2,
    borderColor: palette.primary.main,
    borderStyle: 'dashed',
    backgroundColor: palette.background.default,
    color: palette.text.primary,
    outline: 'none',
    transition: 'border .24s ease-in-out',
  }

  const acceptStyle = {
    borderColor: palette.success.main,
    backgroundColor: theme.lighten(palette.success.light, 0.8),
  }

  const rejectStyle = {
    borderColor: palette.error.main,
    backgroundColor: theme.lighten(palette.error.light, 0.8),
  }

  return { baseStyle, acceptStyle, rejectStyle }
}

export interface StyledFileDropzoneProps extends DropzoneOptions {
  text: string
}

/**
 * React dropzone with default styling.
 * <p>
 *   The inner file input will not keep dropped files, they need to be processed by callbacks.
 * </p>
 *
 * @param props text to display inside the dropzone and {@link DropzoneOptions} to pass to dropzone.
 * @see https://react-dropzone.js.org/
 */
export const StyledFileDropzone: FunctionComponent<StyledFileDropzoneProps> = (props) => {
  const { getRootProps, getInputProps, isDragAccept, isDragReject } = useDropzone(props)
  const theme = useTheme()
  const { baseStyle, acceptStyle, rejectStyle } = makeStyles(theme)
  const style = useMemo(
    () => ({
      ...baseStyle,
      ...(isDragAccept ? acceptStyle : {}),
      ...(isDragReject ? rejectStyle : {}),
    }),
    [isDragAccept, isDragReject]
  )

  return (
    <div className="container">
      <div {...getRootProps({ style })}>
        <input {...getInputProps()} />
        <CloudUploadIcon fontSize={'large'} color={'primary'} />
        <p>{props.text}</p>
      </div>
    </div>
  )
}
