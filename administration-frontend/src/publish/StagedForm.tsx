import { PromiseArea } from '@/components/PromiseArea.tsx'
import { type FunctionComponent } from 'preact'

import { useCallback, useEffect, useState } from 'preact/hooks'
import lazy from 'preact-iso/lazy'
import { type FileWithFieldName, loadJsonForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/publish/actions.ts'
import { trackPromise, useLocation } from '@/utils/hooks.ts'
import {
  ImportProcessNotInitializedError,
  PromiseCanceledError,
  UnexpectedResponseStatusError,
} from '@/utils/errors.ts'
import type { JsonForm } from '@/model/JsonForm.ts'
import type { GenericObjectType } from '@rjsf/utils'
import Constants from '@/Constants.ts'
import { usePromiseTracker } from 'react-promise-tracker'
import { useThrowError } from '@/components/AlertErrorsStack.tsx'

const JsonFormElement = lazy(() => import('@/components/JsonFormElement.tsx'))

export interface StagedFormProps {
  resetForm: () => void
}

export const StagedForm: FunctionComponent<StagedFormProps> = ({ resetForm, children }) => {
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const { navigate } = useLocation()
  const [triggerLoadScheme, setTriggerLoadScheme] = useState(true)
  const { promiseInProgress } = usePromiseTracker({ area: STAGED_FORM_PROMISE_AREA })
  const throwError = useThrowError()

  const loadScheme = useCallback(
    () =>
      trackPromise(loadJsonForm(), STAGED_FORM_PROMISE_AREA)
        .then(setJsonForm)
        .catch((e) => {
          if (e instanceof ImportProcessNotInitializedError) {
            resetForm()
          } else if (e instanceof PromiseCanceledError) {
            // promise was cleaned up
          } else if (e instanceof UnexpectedResponseStatusError && e.payload.status === 201) {
            // import process finished
            const location = e.payload.headers.get('Location') ?? ''
            navigate('~' + Constants.BASE_URL + '/ontologies/' + encodeURIComponent(decodeURI(location)))
          } else if (e instanceof UnexpectedResponseStatusError && e.payload.status === 400) {
            setTriggerLoadScheme(true)
          } else {
            throwError(e)
          }
        }),
    [navigate, resetForm, throwError]
  )

  useEffect(() => {
    if (triggerLoadScheme) {
      return trackPromise(loadScheme(), STAGED_FORM_PROMISE_AREA)
        .then(() => setTriggerLoadScheme(false))
        .catch(throwError).abort
    }
  }, [triggerLoadScheme, loadScheme, throwError])

  const onSubmit = useCallback(
    (formData: GenericObjectType, files: FileWithFieldName[]) => {
      return trackPromise(submitForm(formData, files), STAGED_FORM_PROMISE_AREA)
        .then(() => setTriggerLoadScheme(true))
        .catch((e) => {
          if (e instanceof UnexpectedResponseStatusError && e.payload.status === 400) {
            setTriggerLoadScheme(true)
          } else {
            throwError(e)
          }
        })
    },
    [throwError]
  )

  return (
    <>
      <JsonFormElement jsonForm={jsonForm} onSubmit={onSubmit} disabled={promiseInProgress} />
      {children}
      <PromiseArea area={STAGED_FORM_PROMISE_AREA} boxProps={{ sx: { width: '100%', marginY: '1em' } }} />
    </>
  )
}
