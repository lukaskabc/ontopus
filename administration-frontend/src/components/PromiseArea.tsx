import type { ComponentChildren, FunctionComponent } from 'preact'
import { usePromiseTracker } from 'react-promise-tracker'
import { type BoxProps, type CircularProgressProps, type LinearProgressProps } from '@mui/material'
import Box from '@mui/material/Box'
import CircularProgress from '@mui/material/CircularProgress'
import LinearProgress from '@mui/material/LinearProgress'

export interface PromiseAreaProps {
  area: string
  children?: ComponentChildren
  useCircleLoading?: boolean
  circularProps?: CircularProgressProps
  linearProps?: LinearProgressProps
  boxProps?: BoxProps
}

export const PromiseArea: FunctionComponent<PromiseAreaProps> = ({
  area,
  children,
  useCircleLoading,
  circularProps,
  linearProps,
  boxProps,
}) => {
  const { promiseInProgress } = usePromiseTracker({ area })
  if (!promiseInProgress) {
    return children
  }
  return (
    <Box sx={{ my: 4, mx: 'auto', width: 'fit-content' }} {...boxProps}>
      {useCircleLoading ? <CircularProgress {...circularProps} /> : <LinearProgress {...linearProps} />}
    </Box>
  )
}
