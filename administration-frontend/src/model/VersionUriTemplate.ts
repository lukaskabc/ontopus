import { validateValue } from '@/model/ModelUtils.ts'

export default class VersionUriTemplate {
  readonly uri: string
  readonly version: string

  constructor(uri: string, version: string) {
    this.uri = validateValue(uri, 'string', 'uri')
    this.version = validateValue(version, 'string', 'version')
  }

  static fromJson(jsonObj: any): VersionUriTemplate {
    if (typeof jsonObj !== 'object') {
      throw new Error(`Expected an object to create VersionUriTemplate, got ${typeof jsonObj}`)
    }
    return new VersionUriTemplate(jsonObj.uri, jsonObj.version)
  }
}
