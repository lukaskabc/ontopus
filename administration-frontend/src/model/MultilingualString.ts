export interface MultilingualString {
  [language: string]: string
}

export function getAnyLang(str: MultilingualString) {
  const sortedLangs = Object.keys(str).sort()
  if (sortedLangs.length > 0) {
    return sortedLangs[0]
  }
  return null
}
