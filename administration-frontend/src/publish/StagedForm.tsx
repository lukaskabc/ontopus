import { PromiseArea } from '@/components/PromiseArea.tsx'
import { type FunctionComponent } from 'preact'

import { useCallback, useEffect, useState } from 'preact/hooks'
import { type FileWithFieldName, loadJsonForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/publish/actions.ts'
import { trackPromise, useLocation } from '@/utils/hooks.ts'
import {
  ImportProcessNotInitializedError,
  PromiseCanceledError,
  UnexpectedResponseStatusError,
} from '@/utils/errors.ts'
import type { JsonForm } from '@/model/JsonForm.ts'
import JsonFormElement from '@/components/JsonFormElement.tsx'
import type { GenericObjectType } from '@rjsf/utils'
import Constants from '@/Constants.ts'

export interface StagedFormProps {
  resetForm: () => void
}

export const StagedForm: FunctionComponent<StagedFormProps> = ({ resetForm }) => {
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadScheme, setLoadScheme] = useState<boolean>(true)
  const { navigate } = useLocation()

  // load JSON form when loadScheme is true
  useEffect(() => {
    if (!loadScheme) {
      return
    }

    return trackPromise(loadJsonForm(), STAGED_FORM_PROMISE_AREA)
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
          return
        } else {
          // TODO handle error, propagate to user
          console.error(e)
        }
      })
      .finally(() => setLoadScheme(false)).abort
  }, [loadScheme, resetForm, navigate])

  const onSubmit = useCallback((formData: GenericObjectType, files: FileWithFieldName[]) => {
    return trackPromise(submitForm(formData, files), STAGED_FORM_PROMISE_AREA)
      .then(() => setLoadScheme(true))
      .catch((e) => {
        console.error(e)
      }) // TODO handle and show errors
  }, [])

  return (
    <>
      <JsonFormElement jsonForm={jsonForm} onSubmit={onSubmit} />
      <PromiseArea area={STAGED_FORM_PROMISE_AREA} boxProps={{ sx: { width: '100%', marginY: '1em' } }} />
    </>
  )
}
