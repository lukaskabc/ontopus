import request from '@/config/rest-client.ts'

/**
 * A pair of strings [UUID, label]
 * <p>
 * Label is expected to be an i18n key
 */
export type MenuEntry = [string, string]

export function loadSettingsMenuEntries(): Promise<MenuEntry[]> {
  return request('GET', '/settings')
    .then((response) => response.json())
    .then((data) => {
      return Object.entries(data)
    })
}
