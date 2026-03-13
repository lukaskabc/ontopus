import type { GenericObjectType } from '@rjsf/utils'

/**
 * {@link File} extended by DropZone with an optional path property
 */
export interface DropZoneFile extends File {
  path?: string
}

function pathPrefixedFileName(file: DropZoneFile) {
  // use the dropzone path property if it exists
  if (file.path && file.path.endsWith(file.name)) {
    return file.path.startsWith('./') ? file.path.substring(2) : file.path
  }

  // get path if there is one and strip all leading slashes
  const path = (file?.webkitRelativePath || '').replace(/\/+$/, '')
  return path.endsWith(file.name) ? path : `${path}/${file.name}`
}

export class FormFile {
  readonly class = 'FormFileRequest'
  readonly fileName: string
  readonly path: string
  readonly formFieldName: string

  constructor(fileName: string, path: string, formFieldName: string) {
    this.fileName = fileName
    this.path = path
    this.formFieldName = formFieldName
  }

  static fromFile(file: DropZoneFile, formFieldName: string): FormFile {
    const fileName = file.name
    const path = pathPrefixedFileName(file)
    return new FormFile(fileName, path, formFieldName)
  }

  static fromJson(jsonObj: GenericObjectType): FormFile | null {
    if (typeof jsonObj !== 'object') {
      return null
    }
    const fileName = jsonObj.fileName
    const path = jsonObj.path
    const formFieldName = jsonObj.formFieldName
    if (
      typeof fileName !== 'string' ||
      typeof path !== 'string' ||
      !path.endsWith(fileName) ||
      typeof formFieldName !== 'string'
    ) {
      return null
    }
    return new FormFile(fileName, path, formFieldName)
  }

  equals(other: FormFile): boolean {
    return this.fileName === other.fileName && this.path === other.path
  }
}
