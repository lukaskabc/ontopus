import i18n from 'i18next'
import HttpBackend from 'i18next-http-backend'
import { initReactI18next } from 'react-i18next'
import en_lang from '@/language/en.json'

await i18n
  // pass the i18n instance to react-i18next.
  .use(initReactI18next)
  .use(HttpBackend)
  // init i18next
  // for all options read: https://www.i18next.com/overview/configuration-options
  .init({
    lng: 'en',
    fallbackLng: 'en',
    debug: import.meta.env.DEV,
    load: 'languageOnly',
    resources: {
      en: {
        translation: en_lang,
      },
    },
    backend: {
      // TODO: https://www.i18next.com/how-to/caching#browser-caching-with-local-storage
    },
  })

export default i18n
