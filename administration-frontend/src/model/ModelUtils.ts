import type { MultilingualString } from '@/model/MultilingualString.ts'

export function validateValue<T>(value: T, expectedType: string, fieldName: string): T {
  if (value === undefined || value === null || typeof value !== expectedType) {
    throw new Error(`Missing or invalid field: '${fieldName}'. Expected ${expectedType}, got ${typeof value}.`)
  }
  return value
}

export function validateNullableValue<T>(value: T, expectedType: string, fieldName: string): T | null {
  if (value != null && typeof value !== expectedType) {
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

export function validateDate(value: any | undefined | null, fieldName: string) {
  if (!value || (typeof value !== 'string' && typeof value !== 'number')) {
    throw new Error(`Field ${fieldName}' must be a string or number (Date).`)
  }
  const dateValue = new Date(value)
  if (isNaN(dateValue.getTime())) {
    throw new Error(`Invalid Date format for field '${fieldName}': ${value}`)
  }
  return dateValue
}
