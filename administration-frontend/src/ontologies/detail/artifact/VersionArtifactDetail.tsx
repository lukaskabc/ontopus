import type { ResourceDetailProps } from '@/ontologies/ResourceEntryList.tsx'
import type { FunctionComponent } from 'preact'
import type { VersionArtifactListEntry } from '@/model/VersionArtifactListEntry.ts'
import { useTranslation } from 'react-i18next'
import { useState } from 'preact/hooks'
import { parseUri } from '@/ontologies/actions.ts'

export interface VersionArtifactDetailProps extends ResourceDetailProps<VersionArtifactListEntry> {}

export const VersionArtifactDetail: FunctionComponent<VersionArtifactDetailProps> = ({ dataSource, identifier }) => {
  console.debug('VersionArtifactDetail', identifier)
  const { i18n } = useTranslation()
  // TODO assert not null?

  const versionArtifactUri = parseUri(identifier)
  const [language, setLanguage] = useState<string>(i18n.language)

  console.debug(versionArtifactUri)
  return <>${versionArtifactUri}</>
}
