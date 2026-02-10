import { type FieldProps } from '@rjsf/utils'
import { ActionAwareReusableFile, ReusableFile } from '@/model/ReusableFile.ts'
import { useCallback, useState } from 'preact/hooks'
import { StyledFileDropzone } from '@/publish/components/StyledFileDropzone.tsx'
import { useTranslation } from 'react-i18next'
import ReusableFileList from '@/publish/components/reusable_file_list/ReusableFileList.tsx'
import { ReusableFileListInputHolder } from '@/publish/components/reusable_file_list/ReusableFileListInputHolder.tsx'

function unpackActionAware(files: ActionAwareReusableFile[]): ReusableFile[] {
  return files.filter((file) => file.isAvailable()).map((file) => file.reusableFile)
}

function parseFormData(fieldName: string, formData?: any) {
  if (formData && Object.hasOwn(formData, fieldName) && formData[fieldName] instanceof Array) {
    return formData[fieldName]
      .map(ReusableFile.fromJSON)
      .filter((file) => file != null)
      .map((file) => new ActionAwareReusableFile(file, true, false))
  }
  return []
}

function acceptNewFiles(
  acceptedFiles: File[],
  currentValue: ActionAwareReusableFile[],
  currentFileMap: Map<string, File>,
  fieldName: string
) {
  const value: ActionAwareReusableFile[] = [...currentValue]
  const fileMap = new Map<string, File>(currentFileMap)

  acceptedFiles.forEach((file) => {
    const reusable = ReusableFile.fromFile(file, fieldName)
    const existingFile = value.find((e) => e.reusableFile.fileName === reusable.fileName)
    if (existingFile) {
      existingFile.isDeleted = false
      existingFile.isUploadAvailable = true
    } else {
      const actionAware = new ActionAwareReusableFile(reusable, false, true)
      value.push(actionAware)
    }
    fileMap.set(reusable.fileName, file)
  })

  return { value, fileMap }
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

  const [value, setValue] = useState<ActionAwareReusableFile[]>(() => parseFormData(name, props.formData))
  const [fileMap, setFileMap] = useState<Map<string, File>>(() => new Map())

  const updateValue = useCallback(
    (newValue: ActionAwareReusableFile[]) => {
      setValue(newValue)
      onChange(unpackActionAware(newValue), fieldPathId.path)
    },
    [setValue, onChange, fieldPathId]
  )

  const onDropAccepted = useCallback(
    (acceptedFiles: File[]) => {
      const update = acceptNewFiles(acceptedFiles, value, fileMap, name)
      setFileMap(update.fileMap)
      updateValue(update.value)
    },
    [value, name, setFileMap, updateValue, fileMap]
  )

  const onFileDelete = useCallback(
    (file: ActionAwareReusableFile) => {
      file.delete()
      const newValue = value.filter((f) => f.isAvailable())
      updateValue(newValue)

      for (let key of fileMap.keys()) {
        if (key === file.reusableFile.fileName) {
          const newFileMap = new Map<string, File>(fileMap)
          newFileMap.delete(key)
          setFileMap(newFileMap)
          break
        }
      }
    },
    [value, updateValue, fileMap, setFileMap]
  )

  const onFileUpdate = useCallback(
    // @ts-ignore
    (file: ActionAwareReusableFile) => {
      setValue([...value])
    },
    [setValue, value]
  )

  return (
    <>
      <ReusableFileListInputHolder files={fileMap} name={name} />
      <StyledFileDropzone
        text={t('publish.file-dropzone.text')}
        textSecondary={t('publish.file-dropzone.text-secondary')}
        onDropAccepted={onDropAccepted}
      />
      <ReusableFileList files={value} onDelete={onFileDelete} onUpdate={onFileUpdate} />
    </>
  )
}

export default ReusableFileField
