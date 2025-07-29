import i18n from 'i18next'
import FetchBackend, { type FetchOptions } from 'i18next-fetch-backend'
import { initReactI18next } from 'react-i18next'
import en_lang from '@/language/en.json'
import I18NextChainedBackend, {
  type ChainedBackendOptions,
} from 'i18next-chained-backend'
import resourceToBackend from 'i18next-resources-to-backend'
import Constants from '@/Constants.ts'

const bundledTranslations = {
  en: {
    local: en_lang,
  },
}

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
    retryTimeout: 500,
    maxRetries: 5,
    interpolation: {
      escapeValue: false, // react already safes from xss
    },
    backend: {
      backends: [FetchBackend, resourceToBackend(bundledTranslations)],
      backendOptions: [
        {
          loadPath:
            new URL('locale', Constants.BACKEND_URL.toString()).toString() +
            '/{{ns}}/{{lng}}.json',
          allowMultiLoading: false,
          // requestOptions: {
          //   mode: 'cors',
          //   credentials: 'include',
          //   cache: 'default',
          // },
        } as FetchOptions,
      ],
    },
  })

export default i18n
