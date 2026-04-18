import { type ComponentChildren, createContext, type FunctionComponent } from 'preact'
import Stack from '@mui/material/Stack'
import Alert from '@mui/material/Alert'
import { useCallback, useContext, useState } from 'preact/hooks'
import { OntopusProblemDetail, UnknownError } from '@/utils/errors.ts'
import { AlertTitle } from '@mui/material'

export interface AlertErrorBoundaryProps {
  children?: ComponentChildren
}

interface HasErrorProp {
  error: Error | OntopusProblemDetail
}

type ErrorHandler = (e: unknown) => void

interface ErrorAlertProps extends HasErrorProp {
  onClose: (error: Error) => void
}

function ErrorAlertTitle({ error }: HasErrorProp) {
  if (error.name) {
    return <AlertTitle>{error.name}</AlertTitle>
  }
  return null
}

const ErrorAlert: FunctionComponent<ErrorAlertProps> = ({ error, onClose }) => {
  const onCloseCb = useCallback(() => onClose(error), [error, onClose])

  return (
    <Alert severity={'error'} onClose={onCloseCb}>
      <ErrorAlertTitle error={error} />
      {error.message}
    </Alert>
  )
}

const ErrorHandlerContext = createContext<ErrorHandler | null>(null)

export const useThrowError = () => {
  const context = useContext(ErrorHandlerContext)
  if (context) {
    return context
  }

  throw new Error('useThrowError must be used within AlertErrorsStack')
}

const AlertErrorsStack: FunctionComponent<AlertErrorBoundaryProps> = ({ key, children }) => {
  const [errors, setErrors] = useState<Error[]>([])
  const throwError = useCallback((e: unknown) => {
    let error: Error
    if (e instanceof Error) {
      error = e
    } else {
      error = new UnknownError('Unknown Error: ' + e, e)
    }
    setErrors((arr) => [error, ...arr])
  }, [])
  const removeError = useCallback((e: Error) => {
    setErrors((arr) => arr.filter((err) => err !== e))
  }, [])

  return (
    <ErrorHandlerContext.Provider value={throwError}>
      {errors.length > 0 && (
        <Stack direction={'column'} sx={{ my: 1 }}>
          {errors.map((error) => (
            <ErrorAlert key={key + 'AlertErrorBoundary-Error-' + error} error={error} onClose={removeError} />
          ))}
        </Stack>
      )}
      {children}
    </ErrorHandlerContext.Provider>
  )
}

export default AlertErrorsStack
