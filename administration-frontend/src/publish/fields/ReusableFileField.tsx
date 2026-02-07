import { type FieldProps } from '@rjsf/utils'
import { ReusableFile } from '@/model/ReusableFile.ts'
import { useCallback, useState } from 'preact/hooks'
import { StyledFileDropzone } from '@/publish/components/StyledFileDropzone.tsx'
import { useTranslation } from 'react-i18next'
import ReusableFileList from '@/publish/components/ReusableFileList.tsx'

/**
 *  File field holding an array of {@link ReusableFile ReusableFiles}
 *  that can be added and removed from a list.
 */
function ReusableFileField(props: FieldProps) {
  const { t } = useTranslation()
  const { onChange, name, fieldPathId } = props
  props.multiple = !!(props.multiple || props.options?.multiple)
  const [value, setValue] = useState<ReusableFile[]>(props.formData instanceof Array ? props.formData : [])

  const onDropAccepted = useCallback(
    (acceptedFiles: File[]) => {
      const newValue: ReusableFile[] = []
      const fileIndex = new Set<string>()
      const addValue = (file: ReusableFile) => {
        if (file && !fileIndex.has(file.fileName)) {
          fileIndex.add(file.fileName)
          newValue.push(file)
        }
      }
      value.forEach(addValue)
      acceptedFiles.map((file) => ReusableFile.fromFile(file, name)).forEach(addValue)

      setValue(newValue)
      onChange(newValue, fieldPathId.path)
      console.debug(newValue)
    },
    [value, setValue, onChange, name, fieldPathId]
  )

  return (
    <>
      <StyledFileDropzone text={t('publish.file-dropzone.text')} onDropAccepted={onDropAccepted} />
      <ReusableFileList files={value} />
    </>
  )
}

export default ReusableFileField
