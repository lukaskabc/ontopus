import { type FieldProps } from '@rjsf/utils'
import { ActionAwareReusableFile, ReusableFile } from '@/model/ReusableFile.ts'
import { useCallback, useState } from 'preact/hooks'
import { StyledFileDropzone } from '@/publish/components/StyledFileDropzone.tsx'
import { useTranslation } from 'react-i18next'
import ReusableFileList from '@/publish/components/reusable_file_list/ReusableFileList.tsx'
import { ReusableFileListInputHolder } from '@/publish/components/reusable_file_list/ReusableFileListInputHolder.tsx'

function toUploadedActionAware(file: ReusableFile) {
  return new ActionAwareReusableFile(file, false, true)
}

function toServerActionAware(file: ReusableFile) {
  return new ActionAwareReusableFile(file, true, false)
}

function unpackActionAware(files: ActionAwareReusableFile[]): ReusableFile[] {
  return files.map((file) => file.reusableFile)
}

/**
 *  File field holding an array of {@link ReusableFile ReusableFiles}
 *  that can be added and removed from a list.
 */
function ReusableFileField(props: FieldProps) {
  const { t } = useTranslation()
  const { onChange, name, fieldPathId } = props
  props.multiple = !!(props.multiple || props.options?.multiple)
  if (!name || name.trim() === '') {
    console.error('No field name specified for reusable file field', props)
  }

  const [value, setValue] = useState<ActionAwareReusableFile[]>(() => {
    if (props.formData && Object.hasOwn(props.formData, name) && props.formData[name] instanceof Array) {
      return props.formData[name]
        .map(ReusableFile.fromJSON)
        .filter((file) => file != null)
        .map(toServerActionAware)
    }
    return []
  })
  const [fileMap, setFileMap] = useState<Map<ActionAwareReusableFile, File>>(new Map())

  const updateValue = useCallback(
    (newValue: ActionAwareReusableFile[]) => {
      setValue(newValue)
      onChange(unpackActionAware(newValue), fieldPathId.path)
    },
    [setValue, onChange, fieldPathId]
  )

  const onDropAccepted = useCallback(
    (acceptedFiles: File[]) => {
      const newValue: ActionAwareReusableFile[] = []
      const fileIndex = new Set<string>()
      const newFileMap = new Map<ActionAwareReusableFile, File>()
      const addValue = (actionFile: ActionAwareReusableFile, file?: File) => {
        if (!actionFile) {
          return
        }

        const existingFile = newValue.find((f) => f.reusableFile.fileName === actionFile.reusableFile.fileName)
        if (!file) {
          file = newFileMap.get(existingFile || actionFile)
        }

        if (fileIndex.has(actionFile.reusableFile.fileName) && existingFile) {
          existingFile.isUploadAvailable = true
          existingFile.isDeleted = false
        } else {
          fileIndex.add(actionFile.reusableFile.fileName)
          newValue.push(actionFile)
        }
        if (file) {
          newFileMap.set(existingFile || actionFile, file)
        }
      }
      value.forEach((file) => addValue(file))
      acceptedFiles.forEach((file) => {
        const reusable = ReusableFile.fromFile(file, name)
        const actionAware = toUploadedActionAware(reusable)
        newFileMap.set(actionAware, file)
        addValue(actionAware)
      })

      setFileMap(newFileMap)
      updateValue(newValue)
    },
    [value, onChange, name, setFileMap, updateValue]
  )

  const onFileDelete = useCallback(
    (file: ActionAwareReusableFile) => {
      file.delete()
      const newValue = value.filter((f) => f.isAvailable())
      updateValue(newValue)
    },
    [value, updateValue]
  )

  return (
    <>
      <ReusableFileListInputHolder files={fileMap} name={name} />
      <StyledFileDropzone
        text={t('publish.file-dropzone.text')}
        textSecondary={t('publish.file-dropzone.text-secondary')}
        onDropAccepted={onDropAccepted}
      />
      <ReusableFileList files={value} onDelete={onFileDelete} />
    </>
  )
}

export default ReusableFileField
