export abstract class OntopusError extends Error {
  protected constructor(message: string) {
    super(message)
  }
}

/**
 * An error with additional payload, e.g. response object or parsed JSON body.
 */
export abstract class ErrorWithPayload<P = unknown> extends OntopusError {
  readonly payload: P
  protected constructor(message: string, payload: P) {
    super(message)
    this.payload = payload
  }
}

export class UnknownError extends ErrorWithPayload {
  constructor(message: string, payload: unknown) {
    super(`Unknown error: ${message}`, payload)
  }
}

export class NotLoggedInError extends OntopusError {
  constructor() {
    super('Not logged in')
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
export class UnexpectedResponseStatusError extends ErrorWithPayload<Response> {
  constructor(message: string, payload: Response) {
    super(`Server error: ${message} (${payload.status})`, payload)
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
