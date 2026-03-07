import type { ComponentChildren, FunctionComponent } from 'preact'
import { usePromiseTracker } from 'react-promise-tracker'
import {
  Box,
  CircularProgress,
  type CircularProgressProps,
  LinearProgress,
  type LinearProgressProps,
} from '@mui/material'

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
    <Box sx={{ my: 4 }}>
      {useCircleLoading ? <CircularProgress {...circularProps} /> : <LinearProgress {...linearProps} />}
    </Box>
  )
}
