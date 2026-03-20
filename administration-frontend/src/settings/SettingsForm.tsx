import { useLocation, useRoute } from '@/utils/hooks.ts'
import JsonFormElement from '@/components/JsonFormElement.tsx'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { useCallback, useEffect, useState } from 'preact/hooks'
import type { JsonForm } from '@/model/JsonForm.ts'
import type { FileWithFieldName } from '@/publish/actions.ts'
import { loadSettingsForm, submitSettingsForm } from '@/settings/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import type { GenericObjectType } from '@rjsf/utils'
import Constants from '@/Constants.ts'

const SETTINGS_FORM_PROMISE_AREA = 'SETTINGS_FORM_PROMISE_AREA'

export default function SettingsForm() {
  const { navigate } = useLocation()
  const { params } = useRoute('/:identifier')
  const identifier = params?.identifier
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadForm, setLoadForm] = useState(true)

  useEffect(() => {
    if (!identifier) {
      navigate('~' + Constants.BASE_URL)
      return
    }
    if (loadForm) {
      loadSettingsForm(identifier)
        .then(setJsonForm)
        .finally(() => setLoadForm(false))
    }
    // TODO error handling?
  }, [identifier, loadForm, navigate])

  const onSubmit = useCallback(
    (formData: GenericObjectType, files: FileWithFieldName[]) => {
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
