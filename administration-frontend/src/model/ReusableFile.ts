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

interface ExtendedFile extends File {
  path?: string
}

function pathPrefixedFileName<T extends ExtendedFile>(file: T) {
  if (file.path && file.path.endsWith(file.name)) {
    return file.path.startsWith('./') ? file.path.substring(2) : file.path
  }

  // get path if there is one and strip all leading slashes
  const path = (file?.webkitRelativePath || '').replace(/\/+$/, '')
  return path.endsWith(file.name) ? path : `${path}/${file.name}`
}

/**
 * A file reference
 */
export class ReusableFile {
  // used for backend class mapping
  readonly class = 'ReusableFileDto' // TODO replace DTOs with request and response objects according to REST API pitfalls
  readonly type: ReusableFileType
  readonly fileName: string
  readonly formFieldName: string
  constructor(type: ReusableFileType, fileName: string, formFieldName: string) {
    this.type = type
    this.fileName = fileName
    this.formFieldName = formFieldName
  }
  static fromFile(file: File, formFieldName: string): ReusableFile {
    return new ReusableFile(ReusableFileType.UPLOAD, pathPrefixedFileName(file), formFieldName)
  }
  static fromJSON(json: any): ReusableFile | null {
    if (json instanceof ReusableFile) {
      return json
    }
    if (typeof json === 'object') {
      const type = ReusableFileType.from(json['type'] as string)
      const fileName = json['fileName'] as string
      const formFieldName = json['formFieldName'] as string
      if (type && fileName && formFieldName) {
        return new ReusableFile(type, fileName, formFieldName)
      }
    }
    return null
  }
}

export class ActionAwareReusableFile {
  readonly reusableFile: ReusableFile
  readonly isServerAvailable: boolean
  isUploadAvailable: boolean
  isDeleted: boolean

  constructor(reusableFile: ReusableFile, isServerAvailable: boolean, isUploadAvailable: boolean) {
    this.reusableFile = reusableFile
    this.isServerAvailable = isServerAvailable
    this.isUploadAvailable = isUploadAvailable
    this.isDeleted = false
  }

  delete() {
    if (this.isUploadAvailable) {
      this.isUploadAvailable = false
    } else {
      this.isDeleted = true
    }
  }

  restore() {
    this.isDeleted = false
  }

  isAvailable() {
    return this.isServerAvailable || this.isUploadAvailable
  }
}
