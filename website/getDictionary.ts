/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import 'server-only'
import type { Locale } from './i18n-config'

type Dictionary = typeof import('./dictionaries/en.json')

//define language options
const dictionaries: Record<Locale, () => Promise<Dictionary>> = {
  en: () => import('./dictionaries/en.json').then((module) => module.default),
  el: () => import('./dictionaries/el.json').then((module) => module.default),
  es: () => import('./dictionaries/es.json').then((module) => module.default),
  ru: () => import('./dictionaries/ru.json').then((module) => module.default),
  zh_cn: () => import('./dictionaries/zh_cn.json').then((module) => module.default),
  fr: () => import('./dictionaries/fr.json').then((module) => module.default),
}

export const getDictionary = async (locale: Locale) => dictionaries[locale]()
