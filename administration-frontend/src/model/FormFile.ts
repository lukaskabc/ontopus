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

  constructor(fileName: string, path: string) {
    this.fileName = fileName
    this.path = path
  }

  static fromFile(file: DropZoneFile) {
    const fileName = file.name
    const path = pathPrefixedFileName(file)
    return new FormFile(fileName, path)
  }

  static fromJson(jsonObj: any): FormFile | null {
    if (typeof jsonObj !== 'object') {
      return null
    }
    const fileName = jsonObj.fileName
    const path = jsonObj.path
    if (typeof fileName !== 'string' || typeof path !== 'string' || !path.endsWith(fileName)) {
      return null
    }
    return new FormFile(fileName, path)
  }

  equals(other: FormFile): boolean {
    return this.fileName === other.fileName && this.path === other.path
  }
}
