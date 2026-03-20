import type { ComponentChildren, FunctionComponent } from 'preact'
import { usePromiseTracker } from 'react-promise-tracker'
import { type CircularProgressProps, type LinearProgressProps } from '@mui/material'
import Box from '@mui/material/Box'
import CircularProgress from '@mui/material/CircularProgress'
import LinearProgress from '@mui/material/LinearProgress'

export interface PromiseAreaProps {
  area: string
  children?: ComponentChildren
  useCircleLoading?: boolean
  circularProps?: CircularProgressProps
  linearProps?: LinearProgressProps
}

export const PromiseArea: FunctionComponent<PromiseAreaProps> = ({
  area,
  children,
  useCircleLoading,
  circularProps,
  linearProps,
}) => {
  const { promiseInProgress } = usePromiseTracker({ area })
  if (!promiseInProgress) {
    return children
  }
  return (
    <Box sx={{ my: 4, mx: 'auto', width: 'fit-content' }}>
      {useCircleLoading ? <CircularProgress {...circularProps} /> : <LinearProgress {...linearProps} />}
    </Box>
  )
}
