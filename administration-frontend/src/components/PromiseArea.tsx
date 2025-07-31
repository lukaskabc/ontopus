import type { ComponentChildren, FunctionComponent } from 'preact'
import { usePromiseTracker } from 'react-promise-tracker'
import type { CircularProgressProps } from '@mui/material'
import { CircularProgress, LinearProgress, type LinearProgressProps } from '@mui/material'

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
  if (useCircleLoading) {
    return <CircularProgress {...circularProps} />
  } else {
    return <LinearProgress {...linearProps} />
  }
}
