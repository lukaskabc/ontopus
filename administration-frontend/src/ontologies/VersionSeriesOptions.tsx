import { useLocation, useRoute } from '@/utils/hooks.ts'
import { useCallback, useEffect, useState } from 'preact/hooks'
import type { JsonForm } from '@/model/JsonForm.ts'
import type { GenericObjectType } from '@rjsf/utils'
import type { FileWithFieldName } from '@/publish/actions.ts'
import { trackPromise } from 'react-promise-tracker'
import JsonFormElement from '@/components/JsonFormElement.tsx'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { loadSeriesOptionForm, parseUri, submitSeriesOptionForm } from '@/ontologies/actions.ts'
import Paper from '@mui/material/Paper'

const VERSION_SERIES_OPTIONS_FORM_PROMISE_AREA = 'VERSION_SERIES_OPTIONS_FORM_PROMISE_AREA'

export default function VersionSeriesOptions() {
  const { navigate } = useLocation()
  const { params } = useRoute('/:identifier/:series')
  const identifier = params?.identifier
  const series = parseUri(params?.series)
  const [jsonForm, setJsonForm] = useState<JsonForm | null>(null)
  const [loadForm, setLoadForm] = useState(true)

  useEffect(() => {
    if (!identifier || !series) {
      navigate('~/')
      return
    }
    if (loadForm) {
      loadSeriesOptionForm(series, identifier)
        .then(setJsonForm)
        .finally(() => setLoadForm(false))
    }
    // TODO error handling?
  }, [identifier, loadForm, navigate, series])

  const onSubmit = useCallback(
    (formData: GenericObjectType, files: FileWithFieldName[]) => {
      if (!identifier || !series) {
        return Promise.reject()
      }
      return trackPromise(
        submitSeriesOptionForm(series, identifier, formData, files),
        VERSION_SERIES_OPTIONS_FORM_PROMISE_AREA
      ).finally(() => setLoadForm(true))
    },
    [identifier, series]
  )

  return (
    <Paper sx={{ p: 2 }}>
      <PromiseArea area={VERSION_SERIES_OPTIONS_FORM_PROMISE_AREA}>
        <JsonFormElement jsonForm={jsonForm} onSubmit={onSubmit} />
        <PromiseArea area={VERSION_SERIES_OPTIONS_FORM_PROMISE_AREA} />
      </PromiseArea>
    </Paper>
  )
}
