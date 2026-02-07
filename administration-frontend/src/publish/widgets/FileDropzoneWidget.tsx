import type { WidgetProps } from '@rjsf/utils'
import type { ReusableFile } from '@/model/ReusableFile.ts'
import { StyledFileDropzone } from '@/publish/components/StyledFileDropzone.tsx'

export const FileDropzoneWidget = function <T extends ReusableFile[]>(props: WidgetProps<T>) {
  return <StyledFileDropzone text={props.label} />
}
