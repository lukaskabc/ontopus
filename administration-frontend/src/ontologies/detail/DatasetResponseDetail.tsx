import Select, { type SelectChangeEvent } from '@mui/material/Select'
import { useTranslation } from 'react-i18next'
import { useEffect, useMemo, useState } from 'preact/hooks'
import type { DatasetResponse } from '@/model/DcatResponseModel.ts'
import FormControl from '@mui/material/FormControl'
import Grid from '@mui/material/Grid'
import InputLabel from '@mui/material/InputLabel'
import MenuItem from '@mui/material/MenuItem'
import ReadOnlyField from '@/components/ReadOnlyField.tsx'

const LANGUAGE_SELECT_LABEL_ID = 'label-language-select-id'

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
  }, [i18n.language, language, languages])

  return (
    <Grid container spacing={2}>
      <Grid size={2}>
        <FormControl fullWidth variant={'standard'}>
          <InputLabel id={LANGUAGE_SELECT_LABEL_ID}>{t('language')}</InputLabel>
          <Select
            labelId={LANGUAGE_SELECT_LABEL_ID}
            value={language}
            label={t('language')}
            onChange={(ev: SelectChangeEvent) => {
              const target = ev.target as HTMLSelectElement
              if (target.value) {
                setLanguage(target.value)
              }
            }}
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
        <ReadOnlyField label={t('entity.dataset.detail.dcat-identifier')} value={dataset?.identifier} isCode={true} />
      </Grid>

      <Grid size={6}>
        <ReadOnlyField label={t('entity.dataset.detail.title')} value={dataset?.title[language]} />
      </Grid>

      <Grid size={12}>
        <ReadOnlyField label={t('entity.dataset.detail.description')} value={dataset?.description[language]} />
      </Grid>

      <Grid size={6}>
        <ReadOnlyField label={t('entity.dataset.detail.version')} value={dataset?.version} />
      </Grid>

      <Grid size={3}>
        <ReadOnlyField
          label={t('entity.dataset.detail.release-date')}
          value={dataset?.releaseDate ? t('data-format.date', { val: dataset.releaseDate }) : undefined}
        />
      </Grid>

      <Grid size={3}>
        <ReadOnlyField
          label={t('entity.dataset.detail.modified-date')}
          value={dataset?.modifiedDate ? t('data-format.date', { val: dataset.modifiedDate }) : undefined}
        />
      </Grid>
    </Grid>
  )
}
