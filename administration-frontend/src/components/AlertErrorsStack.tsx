import { type ComponentChildren, createContext, type FunctionComponent } from 'preact'
import Stack from '@mui/material/Stack'
import Alert from '@mui/material/Alert'
import { useCallback, useContext, useState } from 'preact/hooks'
import { OntopusProblemDetail, UnknownError } from '@/utils/errors.ts'
import { AlertTitle } from '@mui/material'
import Typography from '@mui/material/Typography'
import Link from '@mui/material/Link'

export interface HasChildren {
  children?: ComponentChildren
}

interface HasErrorProp {
  error: Error | OntopusProblemDetail
}

type ErrorHandler = (e: unknown) => void

interface ErrorAlertProps extends HasErrorProp {
  onClose: (error: Error) => void
}

function OptionalErrorTypeLink({ error, children }: HasErrorProp & HasChildren) {
  if (error instanceof OntopusProblemDetail && error.type) {
    return (
      <Link href={error.type} color={'inherit'} underline={'none'} target={'_blank'}>
        {children}
      </Link>
    )
  }
  return children
}

function ErrorAlertTitle({ error }: HasErrorProp) {
  let title = null
  if (error instanceof OntopusProblemDetail && error.title) {
    title = error.title
  } else {
    title = error.name
  }
  if (title) {
    return (
      <AlertTitle>
        <OptionalErrorTypeLink error={error}>{title}</OptionalErrorTypeLink>
      </AlertTitle>
    )
  }
  return null
}

const ErrorAlert: FunctionComponent<ErrorAlertProps> = ({ error, onClose }) => {
  const onCloseCb = useCallback(() => onClose(error), [error, onClose])

  return (
    <Alert severity={'error'} onClose={onCloseCb}>
      <ErrorAlertTitle error={error} />
      <Typography variant={'body1'}>{error.message}</Typography>
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

const AlertErrorsStack: FunctionComponent<HasChildren> = ({ key, children }) => {
  const [errors, setErrors] = useState<Error[]>([])
  const throwError = useCallback((e: unknown) => {
    let error: Error
    if (e instanceof Error) {
      error = e
    } else {
      error = new UnknownError('Unknown Error: ' + e, e)
    }
    console.error({ error })
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
