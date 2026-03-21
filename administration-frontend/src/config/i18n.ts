import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import en_lang from '@/language/en.json'
import I18NextChainedBackend, { type ChainedBackendOptions } from 'i18next-chained-backend'
import resourceToBackend from 'i18next-resources-to-backend'
import Constants from '@/Constants.ts'
import HttpBackend, { type HttpBackendOptions } from 'i18next-http-backend'

const bundledTranslations = {
  en: {
    local: en_lang,
    server: {}, // required to keep server namespace as successfully loaded
  },
}

if (!i18n.isInitialized && !i18n.isInitializing) {
  await i18n
    // pass the i18n instance to react-i18next.
    .use(initReactI18next)
    .use(I18NextChainedBackend)
    // init i18next
    // for all options read: https://www.i18next.com/overview/configuration-options
    .init<ChainedBackendOptions>({
      lng: 'en',
      fallbackLng: 'en',
      ns: ['local', 'server'],
      fallbackNS: 'local',
      defaultNS: 'server',
      debug: import.meta.env.DEV,
      maxRetries: 2,
      interpolation: {
        escapeValue: false, // react already safes from xss
      },
      backend: {
        backends: [HttpBackend, resourceToBackend(bundledTranslations)],
        backendOptions: [
          {
            loadPath: new URL('locale', Constants.BACKEND_URL.toString()).toString() + '/{{ns}}/{{lng}}.json',
            crossDomain: true,
            requestOptions: {
              credentials: 'include',
            },
          } as HttpBackendOptions,
        ],
      },
    })
}

i18n.services.formatter?.addCached('iso-date', () => (value) => {
  if (value instanceof Date) {
    return value.toISOString().split('T')[0] // Returns YYYY-MM-DD
  }
  return value
})

export default i18n
