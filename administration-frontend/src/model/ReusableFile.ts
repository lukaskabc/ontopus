import { makeEnum } from '@/utils/Enum.ts'

/**
 * The type of the {@link ReusableFile}
 */
export const ReusableFileType = makeEnum(
  {
    /**
     * The file is already stored on the server.
     */
    SERVER: 'SERVER',
    /**
     * The file is being uploaded to the server as a part of the request.
     */
    UPLOAD: 'UPLOAD',
  },
  'ReusableFileType'
)
export type ReusableFileType = (typeof ReusableFileType)[keyof typeof ReusableFileType]

function pathPrefixedFileName(file: File) {
  // get path if there is one and strip all leading slashes
  const path = (file?.webkitRelativePath || '').replace(/\/+$/, '')
  return path.endsWith(file.name) ? path : `${path}/${file.name}`
}

/**
 * A file reference
 */
export class ReusableFile {
  readonly type: ReusableFileType
  readonly fileName: string
  constructor(type: ReusableFileType, fileName: string) {
    this.type = type
    this.fileName = fileName
  }
  static fromFile(file: File) {
    return new ReusableFile(ReusableFileType.UPLOAD, pathPrefixedFileName(file))
  }
  static fromJSON(json: any): ReusableFile | null {
    if (json instanceof ReusableFile) {
      return json
    }
    if (typeof json === 'object') {
      const type = ReusableFileType.from(json['type'] as string)
      const fileName = json['fileName'] as string
      if (type && fileName) {
        return new ReusableFile(type, fileName)
      }
    }
    return null
  }
}
