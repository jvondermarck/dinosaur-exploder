/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

export const i18n = {
  defaultLocale: 'en',
  locales: ['en', 'el', 'zh_cn'], // language options
} as const

export type Locale = (typeof i18n)['locales'][number]