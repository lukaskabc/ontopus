/**
 * Map<language, value>
 */
export type MultilingualString = Record<string, string>

export function getAnyLang(str: MultilingualString) {
  const sortedLangs = Object.keys(str).sort()
  if (sortedLangs.length > 0) {
    return sortedLangs[0]
  }
  return null
}
