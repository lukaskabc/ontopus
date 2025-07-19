import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import en_lang from '@/language/en.json'

await i18n
  // pass the i18n instance to react-i18next.
  .use(initReactI18next)
  // init i18next
  // for all options read: https://www.i18next.com/overview/configuration-options
  .init({
    lng: 'en',
    fallbackLng: 'en',
    debug: import.meta.env.DEV,
    resources: {
      en: {
        translation: en_lang,
      },
    },
  })

export default i18n
