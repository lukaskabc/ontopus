export abstract class OntopusError extends Error {
  protected constructor(message: string) {
    super(message)
  }
}

/**
 * An error with additional payload, e.g. response object or parsed JSON body.
 */
export abstract class ErrorWithPayload extends OntopusError {
  readonly payload: any
  protected constructor(message: string, payload: any) {
    super(message)
    this.payload = payload
  }
}

export class UnknownError extends ErrorWithPayload {
  constructor(message: string, payload: any) {
    super(`Unknown error: ${message}`, payload)
  }
}

/**
 * The promise was canceled and could not be fulfilled
 */
export class PromiseCanceledError extends OntopusError {
  constructor() {
    super('Promise was canceled')
  }
}

/**
 * The server returned an unexpected HTTP status code
 */
export class UnexpectedResponseStatusError extends ErrorWithPayload {
  constructor(message: string, payload: any) {
    super(`Server error: ${message}`, payload)
  }
}

/**
 * The server returned HTTP status code 205, the import process was not initialized.
 */
export class ImportProcessNotInitializedError extends OntopusError {
  constructor() {
    super('Import process not initialized')
  }
}
