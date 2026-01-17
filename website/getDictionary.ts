import 'server-only'

//define language options
const dictionaries = {
    en: () => import('./dictionaries/en.json').then((module) => module.default),
    el: () => import('./dictionaries/el.json').then((module) => module.default)
}

export const getDictionary = async (locale: 'en' | 'el') => {
    //set english as default language
    return dictionaries[locale] ? dictionaries[locale]() : dictionaries.en()
}