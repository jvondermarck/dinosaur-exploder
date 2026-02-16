/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import 'server-only'

//define language options
const dictionaries = {
    en: () => import('./dictionaries/en.json').then((module) => module.default),
    el: () => import('./dictionaries/el.json').then((module) => module.default),
    zh_cn: () => import('./dictionaries/zh_cn.json').then((module) => module.default)
}

export const getDictionary = async (locale: 'en' | 'el' | 'zh_cn') => {
    //set english as default language
    return dictionaries[locale] ? dictionaries[locale]() : dictionaries.en()
}