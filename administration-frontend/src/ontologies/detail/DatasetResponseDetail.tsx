import { FormControl, Grid, InputLabel, MenuItem, TextField } from '@mui/material'
import Select, { type SelectChangeEvent } from '@mui/material/Select'
import { useTranslation } from 'react-i18next'
import { useEffect, useMemo, useState } from 'preact/hooks'
import type { DatasetResponse } from '@/model/DcatResponseModel.ts'

const LANGUAGE_SELECT_LABEL_ID = 'label-language-select-id'
const disabledProps = {
  input: { readOnly: true },
}

export interface DatasetDetailProps {
  dataset: DatasetResponse | null
}

export default function DatasetResponseDetail({ dataset }: DatasetDetailProps) {
  const { t, i18n } = useTranslation()
  const [language, setLanguage] = useState<string>('')

  const languages = useMemo(() => {
    if (!dataset) {
      return []
    }

    const set = new Set<string>()
    const attrs = [dataset?.title, dataset?.description]

    attrs
      .filter((obj) => obj != null)
      .flatMap((str) => Object.getOwnPropertyNames(str))
      .forEach((lang) => set.add(lang))

    return [...set]
  }, [dataset])

  useEffect(() => {
    if (languages.length === 0) {
      setLanguage('')
    } else if (language === '' && languages.includes(i18n.language)) {
      setLanguage(i18n.language)
      return
    } else if (!languages.includes(language)) {
      setLanguage(languages[0])
    }
  }, [language, languages, setLanguage])

  return (
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
            {languages.map((lang) => (
              <MenuItem key={'language-select-' + lang} value={lang}>
                {lang}
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
          value={dataset?.identifier || ' '}
        />
      </Grid>
      <Grid size={6}>
        <TextField
          fullWidth
          id="standard-basic"
          label={t('entity.version-series.detail.title')}
          variant="standard"
          slotProps={disabledProps}
          value={dataset?.title[language] || ' '}
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
          value={dataset?.description[language] || ' '}
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
          value={t('data-format.date', { val: dataset?.releaseDate }) || ' '}
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
          value={t('data-format.date', { val: dataset?.modifiedDate }) || ' '}
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
          value={dataset?.version || ' '}
        />
      </Grid>
    </Grid>
  )
}
