import type { FormContextType, RJSFSchema, StrictRJSFSchema, WidgetProps } from '@rjsf/utils'
import CloudUploadIcon from '@mui/icons-material/CloudUpload'
import { ReusableFile } from '@/model/ReusableFile.ts'
import { useCallback } from 'preact/hooks'
import type { ChangeEvent } from 'preact/compat'

function processFiles(files: FileList): ReusableFile[] {
  const result: ReusableFile[] = []
  for (let file of files) {
    result.push(ReusableFile.fromFile(file))
  }
  return result
}

export default function ReusableFileWidget<
  T extends ReusableFile[],
  S extends StrictRJSFSchema = RJSFSchema,
  F extends FormContextType = any,
>(props: WidgetProps<T, S, F>) {
  const { options } = props

  const inputProps: any = {}

  if (options.directory) {
    inputProps.directory = true
    inputProps.webkitdirectory = true
  }

  const onChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    const files = (e?.target as any).files
    const result = processFiles(files)
    props.onChange(result)
  }, [])

  return (
    <>
      <div class={'drop-area'}>
        <label>
          <CloudUploadIcon />
          {props.label}
        </label>
        <input {...inputProps} type={'file'} onChange={onChange} />
      </div>
    </>
  )
}
