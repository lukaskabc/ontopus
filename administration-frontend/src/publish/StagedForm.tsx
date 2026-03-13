import { PromiseArea } from '@/components/PromiseArea.tsx'
import { type FunctionComponent } from 'preact'

import { useCallback, useEffect, useState } from 'preact/hooks'
import { type FileWithFieldName, loadJsonForm, STAGED_FORM_PROMISE_AREA, submitForm } from '@/publish/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import { ImportProcessNotInitializedError, PromiseCanceledError } from '@/utils/errors.ts'
import type { JsonForm } from '@/model/JsonForm.ts'
import JsonFormElement from '@/components/JsonFormElement.tsx'
import type { GenericObjectType } from '@rjsf/utils'

export interface StagedFormProps {
  resetForm: () => void
}

export const StagedForm: FunctionComponent<StagedFormProps> = ({ resetForm }) => {
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadScheme, setLoadScheme] = useState<boolean>(true)

  // load JSON form when loadScheme is true
  useEffect(() => {
    if (!loadScheme) {
      return
    }

    const { promise, cleanup } = loadJsonForm()

    trackPromise(promise, STAGED_FORM_PROMISE_AREA)
      .then(setJsonForm)
      .catch((e) => {
        if (e instanceof ImportProcessNotInitializedError) {
          resetForm()
        } else if (e instanceof PromiseCanceledError) {
          // promise was cleaned up
        } else {
          // TODO handle error, propagate to user
          console.error(e)
        }
      })
      .finally(() => setLoadScheme(false))
    return cleanup
    // TODO install eslint for preact hooks validation
  }, [loadScheme, resetForm])

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
      <PromiseArea area={STAGED_FORM_PROMISE_AREA} />
    </>
  )
}
