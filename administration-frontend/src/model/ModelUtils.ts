import type { MultilingualString } from '@/model/MultilingualString.ts'

export function validateValue<T>(value: T, expectedType: string, fieldName: string): T {
  if (value === undefined || value === null || typeof value !== expectedType) {
    throw new Error(`Missing or invalid field: '${fieldName}'. Expected ${expectedType}, got ${typeof value}.`)
  }
  return value
}

/**
 * Validates that the input is a MultilingualString
 */
export function validateMultilingual(value: any, fieldName: string): MultilingualString {
  if (!value || typeof value !== 'object' || Array.isArray(value)) {
    throw new Error(`Field '${fieldName}' must be an object (MultilingualString).`)
  }

  // Ensure every value inside the object is a string
  for (const [lang, text] of Object.entries(value)) {
    if (typeof text !== 'string') {
      throw new Error(`Invalid entry in '${fieldName}': Key '${lang}' must map to a string value.`)
    }
  }

  return value as MultilingualString
}
