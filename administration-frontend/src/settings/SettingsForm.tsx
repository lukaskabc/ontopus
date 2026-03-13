import { useLocation, useRoute } from 'wouter-preact'
import JsonFormElement from '@/components/JsonFormElement.tsx'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useCallback, useEffect, useState } from 'preact/hooks'
import type { JsonForm } from '@/model/JsonForm.ts'
import type { FileWithFieldName } from '@/publish/actions.ts'
import { loadSettingsForm, submitSettingsForm } from '@/settings/actions.ts'
import { trackPromise } from 'react-promise-tracker'

const SETTINGS_FORM_PROMISE_AREA = 'SETTINGS_FORM_PROMISE_AREA'

export default function SettingsForm() {
  const [_, navigate] = useLocation()
  const [__, params] = useRoute('/:identifier')
  const identifier = params?.identifier
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadForm, setLoadForm] = useState(true)

  useEffect(() => {
    if (!identifier) {
      navigate('~/')
      return
    }
    if (loadForm) {
      loadSettingsForm(identifier)
        .then(setJsonForm)
        .finally(() => setLoadForm(false))
    }
    // TODO error handling?
  }, [identifier, loadForm])

  const onSubmit = useCallback(
    (formData: any, files: FileWithFieldName[]) => {
      if (!identifier) {
        return Promise.reject()
      }
      return trackPromise(submitSettingsForm(identifier, formData, files), SETTINGS_FORM_PROMISE_AREA).finally(() =>
        setLoadForm(true)
      )
    },
    [identifier]
  )

  return (
    <>
      <JsonFormElement jsonForm={jsonForm} onSubmit={onSubmit} />
      <PromiseArea area={SETTINGS_FORM_PROMISE_AREA} />
    </>
  )
}
