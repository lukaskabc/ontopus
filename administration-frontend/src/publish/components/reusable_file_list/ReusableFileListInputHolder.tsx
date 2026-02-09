import type { FunctionComponent } from 'preact'
import { type ActionAwareReusableFile } from '@/model/ReusableFile.ts'
import { useEffect, useMemo, useRef } from 'preact/hooks'

export type ReusableFileListInputHolderProps = {
  files: Map<ActionAwareReusableFile, File>
  name: string
}

export const ReusableFileListInputHolder: FunctionComponent<ReusableFileListInputHolderProps> = ({ files, name }) => {
  const inputRef = useRef<HTMLInputElement>(null)
  const filesToTransfer: File[] = useMemo(() => {
    const result: File[] = []
    files.forEach((file: File, actionFile: ActionAwareReusableFile) => {
      if (actionFile.isAvailable()) {
        result.push(file)
      }
    })
    return result
  }, [files])

  useEffect(() => {
    if (!inputRef.current) return

    // https://stackoverflow.com/a/68182158/1068446
    const dataTransfer = new DataTransfer()
    filesToTransfer.forEach((v) => {
      dataTransfer.items.add(v)
    })
    inputRef.current.files = dataTransfer.files
  }, [filesToTransfer])

  return <input type={'file'} name={name} ref={inputRef} style={{ display: 'none' }} />
}
