import type { FunctionComponent, RefCallback } from 'preact'
import { useCallback } from 'preact/hooks'

export interface FormFileListInputHolderProps {
  files: Map<string, File>
  name: string
}

export const FormFileListInputHolder: FunctionComponent<FormFileListInputHolderProps> = ({ files, name }) => {
  const inputRefCallback: RefCallback<HTMLInputElement> = useCallback(
    (element: HTMLInputElement | null) => {
      if (!element) return

      // https://stackoverflow.com/a/68182158/1068446
      const dataTransfer = new DataTransfer()
      files.forEach((v: File, fileName: string) => {
        dataTransfer.items.add(new File([v], fileName, { type: v.type, lastModified: v.lastModified }))
      })
      element.files = dataTransfer.files
    },
    [files]
  )

  return <input type={'file'} name={name} ref={inputRefCallback} style={{ display: 'none' }} />
}
