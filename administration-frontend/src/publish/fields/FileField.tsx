import { type FieldProps } from '@rjsf/utils'
import { useCallback, useState } from 'preact/hooks'
import { StyledFileDropzone } from '@/publish/components/StyledFileDropzone.tsx'
import { useTranslation } from 'react-i18next'
import FormFileList from '@/publish/components/file_list/FormFileList.tsx'
import { FormFileListInputHolder } from '@/publish/components/file_list/FormFileListInputHolder.tsx'
import { FormFile } from '@/model/FormFile.ts'

function parseFormData(fieldName: string, formData?: any) {
  if (formData && Object.hasOwn(formData, fieldName) && formData[fieldName] instanceof Array) {
    return formData[fieldName].map(FormFile.fromJson).filter((file) => file != null)
  }
  return []
}

function acceptNewFiles(
  acceptedFiles: File[],
  currentValue: FormFile[],
  currentFileMap: Map<string, File>,
  formFieldName: string
) {
  const value: FormFile[] = [...currentValue]
  const fileMap = new Map<string, File>(currentFileMap)

  acceptedFiles.forEach((file) => {
    const formFile = FormFile.fromFile(file, formFieldName)
    const existingFile = value.find((f) => formFile.equals(f))
    if (!existingFile) {
      value.push(formFile)
    }
    fileMap.set(formFile.fileName, file)
  })

  return { value, fileMap }
}

/**
 *  File field holding an array of {@link FormFile FormFiles}
 *  that can be added and removed from a list.
 *  Supports {@code multiple} property to allow multiple file selection and drop.
 */
function FileField(props: FieldProps) {
  const { t } = useTranslation()
  const { onChange, name, fieldPathId } = props
  const multiple = !!(props.uiSchema?.multiple || props.schema?.multiple)

  if (!name || name.trim() === '') {
    console.error('No field name specified for file field', props)
  }

  const [value, setValue] = useState<FormFile[]>(() => parseFormData(name, props.formData))
  const [fileMap, setFileMap] = useState<Map<string, File>>(() => new Map())

  // set the state and propagate the value change to the form data
  const updateValue = useCallback(
    (newValue: FormFile[]) => {
      setValue(newValue)
      onChange(newValue, fieldPathId.path)
    },
    [setValue, onChange, fieldPathId]
  )

  const onDropAccepted = useCallback(
    (acceptedFiles: File[]) => {
      const update = acceptNewFiles(acceptedFiles, value, fileMap, name)

      if (!multiple && update.value.length > 1) {
        const singleValue = update.value.pop()!
        const singleValueFile = update.fileMap.get(singleValue.fileName)!
        update.value = [singleValue]
        update.fileMap = new Map([[singleValue.fileName, singleValueFile]])
      }

      setFileMap(update.fileMap)
      updateValue(update.value)
    },
    [value, name, setFileMap, updateValue, fileMap, multiple]
  )

  const onFileDelete = useCallback(
    (file: FormFile) => {
      const newValue = value.filter((f) => !file.equals(f))
      updateValue(newValue)

      const newFileMap = new Map<string, File>(fileMap)
      newFileMap.delete(file.fileName)
      setFileMap(newFileMap)
    },
    [value, updateValue, fileMap, setFileMap]
  )

  return (
    <>
      <FormFileListInputHolder files={fileMap} name={name} />
      <StyledFileDropzone
        text={t('publish.file-dropzone.text')}
        textSecondary={t('publish.file-dropzone.text-secondary')}
        onDropAccepted={onDropAccepted}
        multiple={multiple}
      />
      <FormFileList files={value} onDelete={onFileDelete} />
    </>
  )
}

export default FileField
