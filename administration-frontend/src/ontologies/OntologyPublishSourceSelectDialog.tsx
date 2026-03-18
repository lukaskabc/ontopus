import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogActions from '@mui/material/DialogActions'
import { type DialogProps } from '@toolpad/core'
import List from '@mui/material/List'
import ListItem from '@mui/material/ListItem'
import ListItemButton from '@mui/material/ListItemButton'
import { useTranslation } from 'react-i18next'
import { useCallback, useEffect, useState } from 'preact/hooks'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { trackPromise } from 'react-promise-tracker'
import { fetchImportSources } from '@/ontologies/actions.ts'
import { useLocation } from '@/utils/hooks.ts'

import DialogContent from '@mui/material/DialogContent'
import ListItemText from '@mui/material/ListItemText'

const IMPORT_SOURCES_PROMISE_AREA = 'OntologyPublishSourceSelectDialog_fetchImportSources'

export default function OntologyPublishSourceSelectDialog({ open, onClose }: DialogProps) {
  const { t } = useTranslation()
  const { navigate } = useLocation()
  const [importSources, setImportSources] = useState<string[]>([])

  useEffect(() => {
    trackPromise(fetchImportSources(), IMPORT_SOURCES_PROMISE_AREA).then(setImportSources).catch(console.error)
  }, [])

  const onSourceSelect = useCallback(
    (source: string) => {
      onClose().then(() => navigate('/publish', { state: { importSource: source } }))
    },
    [navigate, onClose]
  )

  return (
    <Dialog open={open} onClose={() => onClose()}>
      <DialogTitle>Select Ontology source</DialogTitle>
      <DialogContent>
        <PromiseArea area={IMPORT_SOURCES_PROMISE_AREA}>
          <List>
            {importSources.map((source) => (
              <ListItem key={'SourceNameListItem' + source} disablePadding>
                <ListItemButton component={'a'} onClick={() => onSourceSelect(source)}>
                  <ListItemText primary={t(source)} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </PromiseArea>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose()}>Close me</Button>
      </DialogActions>
    </Dialog>
  )
}
