import type { VersionArtifactResponse } from '@/model/VersionArtifactResponse.ts'
import DatasetResponseDetail from '@/ontologies/detail/DatasetResponseDetail.tsx'
import ReadOnlyField from '@/components/ReadOnlyField.tsx'
import { useTranslation } from 'react-i18next'
import Stack from '@mui/material/Stack'
import Button from '@mui/material/Button'
import { ActionConfirmDialog } from '@/components/ActionConfirmDialog.tsx'
import { useCallback, useState } from 'preact/hooks'
import { PromiseArea } from '@/components/PromiseArea.tsx'
import { trackPromise, useLocation } from '@/utils/hooks.ts'
import { deleteVersionArtifact } from '@/ontologies/detail/artifact/actions.ts'
import Constants from '@/Constants.ts'
import { encodeBase64Uri } from '@/ontologies/actions.ts'

export interface VersionArtifactDetailProps {
  versionArtifact: VersionArtifactResponse | null
}

const DELETE_VERSION_ARTIFACT_PROMISE_AREA = 'DELETE_VERSION_ARTIFACT_PROMISE_AREA'

export default function VersionArtifactResponseDetail({ versionArtifact }: VersionArtifactDetailProps) {
  const { t } = useTranslation()
  const { navigate } = useLocation()

  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)

  const openDeleteDialog = useCallback(() => setIsDeleteDialogOpen(true), [])
  const closeDeleteDialog = useCallback(() => setIsDeleteDialogOpen(false), [])

  const onDelete = useCallback(() => {
    if (!versionArtifact?.uri) return
    trackPromise(deleteVersionArtifact(versionArtifact?.uri), DELETE_VERSION_ARTIFACT_PROMISE_AREA).then(() => {
      setIsDeleteDialogOpen(false)
    })
    navigate('~' + Constants.BASE_URL + '/ontologies/' + encodeBase64Uri(decodeURI(versionArtifact.series)))
  }, [navigate, versionArtifact?.series, versionArtifact?.uri])

  return (
    <>
      <Stack direction={'row'} justifyContent={'space-between'} alignItems={'start'}>
        <ReadOnlyField
          label={t('entity.version-artifact.detail.versionUri')}
          value={versionArtifact?.versionUri}
          sx={{ mb: 3 }}
          isCode
        />
        <Button variant={'outlined'} color={'error'} onClick={openDeleteDialog}>
          {t('delete')}
        </Button>
      </Stack>
      <DatasetResponseDetail dataset={versionArtifact} />
      <PromiseArea area={DELETE_VERSION_ARTIFACT_PROMISE_AREA}>
        <ActionConfirmDialog
          isOpen={isDeleteDialogOpen}
          onConfirm={onDelete}
          onCancel={closeDeleteDialog}
          title={t('delete-dialog.title')}
          positiveColor={'error'}
          negativeColor={'primary'}
        >
          {t('delete-dialog.version-artifact-text', { entityName: t('entity.version-artifact.title') })}
        </ActionConfirmDialog>
      </PromiseArea>
    </>
  )
}
