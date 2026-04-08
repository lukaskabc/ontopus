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

const JsonFormElement = lazy(() => import('@/components/JsonFormElement.tsx'))

export interface StagedFormProps {
  resetForm: () => void
}

export const StagedForm: FunctionComponent<StagedFormProps> = ({ resetForm }) => {
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadScheme, setLoadScheme] = useState<boolean>(true)
  const { navigate } = useLocation()
  const [triggerLoadScheme, setTriggerLoadScheme] = useState(false)

  // load JSON form when loadScheme is true
  useEffect(() => {
    if (!loadScheme) {
      return
    }

    const finishLoading = () => setLoadScheme(false)

    return trackPromise(loadJsonForm(), STAGED_FORM_PROMISE_AREA)
      .then(setJsonForm)
      .then(finishLoading)
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
          finishLoading()
          setTriggerLoadScheme(true)
        } else {
          // TODO handle error, propagate to user
          console.error(e)
          finishLoading()
        }
      }).abort
  }, [loadScheme, resetForm, navigate])

  useEffect(() => {
    if (triggerLoadScheme) {
      setLoadScheme(true)
      setTriggerLoadScheme(false)
    }
  }, [triggerLoadScheme])

  const onSubmit = useCallback((formData: GenericObjectType, files: FileWithFieldName[]) => {
    return trackPromise(submitForm(formData, files), STAGED_FORM_PROMISE_AREA)
      .then(() => setLoadScheme(true))
      .catch((e) => {
        if (e instanceof UnexpectedResponseStatusError && e.payload.status === 400) {
          // TODO show error
          setLoadScheme(true)
        } else {
          console.error(e)
        }
      }) // TODO handle and show errors
  }, [])

  return (
    <>
      <JsonFormElement jsonForm={jsonForm} onSubmit={onSubmit} />
      <PromiseArea area={STAGED_FORM_PROMISE_AREA} boxProps={{ sx: { width: '100%', marginY: '1em' } }} />
    </>
  )
}
