import type { FunctionComponent } from 'preact'
import { VersionSeriesListEntryDataSourceContext } from '@/ontologies/OntologyDataSource.ts'
import { useContext, useEffect, useMemo, useState } from 'preact/hooks'
import { type StringRouteParams, useLocation, useRoute } from 'wouter-preact'
import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import { findVersionSeries } from '@/ontologies/detail/actions.ts'
import { Container, FormControl, Grid, InputLabel, MenuItem, Paper, TextField } from '@mui/material'
import Select, { type SelectChangeEvent } from '@mui/material/Select'
import { useTranslation } from 'react-i18next'

function parseUri(params: StringRouteParams<any> | null) {
  if (params && params[0]) {
    return decodeURIComponent(params[0])
  }
  return null
}

const LANGUAGE_SELECT_LABEL_ID = 'label-language-select-id'
const disabledProps = {
  input: { readOnly: true },
}

export const VersionSeriesDetail: FunctionComponent = () => {
  const { t, i18n } = useTranslation()
  const dataSource = useContext(VersionSeriesListEntryDataSourceContext)!
  // TODO assert not null?
  const [_, params] = useRoute('/*')
  const [__, navigate] = useLocation()
  const ontologyURI = parseUri(params)
  const [language, setLanguage] = useState<string>(i18n.language)

  const [versionSeries, setVersionSeries] = useState<VersionSeriesResponse | null>(null)

  const languages = useMemo(() => {
    const set = new Set<string>()
    const attrs = [versionSeries?.title, versionSeries?.description]

    attrs
      .filter((obj) => obj != null)
      .flatMap((str) => Object.getOwnPropertyNames(str))
      .forEach((lang) => set.add(lang))

    return [...set]
  }, [versionSeries])

  useEffect(() => {
    if (!language || !languages.includes(language)) {
      setLanguage(languages[0])
    }
  }, [language, languages, setLanguage])

  useEffect(() => {
    if (!ontologyURI) {
      navigate('/')
      return
    }
    findVersionSeries(ontologyURI).then(setVersionSeries)
  }, [dataSource, navigate, ontologyURI, setVersionSeries])

  return (
    <Container maxWidth="lg" sx={{ mt: 2 }}>
      <Paper sx={{ p: 2 }}>
        <Grid container spacing={2}>
          <Grid size={2}>
            <FormControl fullWidth>
              <InputLabel id={LANGUAGE_SELECT_LABEL_ID}>{t('language')}</InputLabel>
              <Select
                labelId={LANGUAGE_SELECT_LABEL_ID}
                value={language}
                label={t('language')}
                onChange={(ev: SelectChangeEvent) => setLanguage((ev?.target as any).value as string)}
              >
                {languages.map((language) => (
                  <MenuItem key={'language-select-' + languages} value={language}>
                    {language}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid size={10}>
            <TextField
              fullWidth
              id="standard-basic"
              label={t('entity.version-series.detail.identifier')}
              variant="standard"
              slotProps={disabledProps}
              value={versionSeries?.identifier || ' '}
            />
          </Grid>
          <Grid size={6}>
            <TextField
              fullWidth
              id="standard-basic"
              label={t('entity.version-series.detail.title')}
              variant="standard"
              slotProps={disabledProps}
              value={versionSeries?.title[language] || ' '}
            />
          </Grid>
          <Grid size={12}>
            <TextField
              fullWidth
              multiline
              id="standard-basic"
              label={t('entity.version-series.detail.description')}
              variant="standard"
              slotProps={disabledProps}
              value={versionSeries?.description[language] || ' '}
            />
          </Grid>
          <Grid size={3}>
            <TextField
              fullWidth
              multiline
              id="standard-basic"
              label={t('entity.version-series.detail.release-date')}
              variant="standard"
              slotProps={disabledProps}
              value={t('data-format.date', { val: versionSeries?.releaseDate }) || ' '}
            />
          </Grid>
          <Grid size={3}>
            <TextField
              fullWidth
              multiline
              id="standard-basic"
              label={t('entity.version-series.detail.modified-date')}
              variant="standard"
              slotProps={disabledProps}
              value={t('data-format.date', { val: versionSeries?.modifiedDate }) || ' '}
            />
          </Grid>
          <Grid size={6}>
            <TextField
              fullWidth
              multiline
              id="standard-basic"
              label={t('entity.version-series.detail.version')}
              variant="standard"
              slotProps={disabledProps}
              value={versionSeries?.version || ' '}
            />
          </Grid>
        </Grid>
      </Paper>
    </Container>
  )
}
