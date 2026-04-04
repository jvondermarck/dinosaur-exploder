/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import type { Locale } from "@/i18n-config";

type LocaleScript = "latin" | "greek" | "cyrillic" | "han";

type LocaleDetails = {
  label: string;
  languageTag: string;
  script: LocaleScript;
};

export const SITE_NAME = "Dinosaur Exploder";
const DEFAULT_SITE_URL = "https://dinosaur-exploder.vercel.app";

export const LOCALE_DETAILS: Record<Locale, LocaleDetails> = {
  en: {
    label: "English",
    languageTag: "en-US",
    script: "latin",
  },
  el: {
    label: "Ελληνικά",
    languageTag: "el-GR",
    script: "greek",
  },
  es: {
    label: "Español",
    languageTag: "es-ES",
    script: "latin",
  },
  ru: {
    label: "Русский",
    languageTag: "ru-RU",
    script: "cyrillic",
  },
  zh_cn: {
    label: "简体中文",
    languageTag: "zh-CN",
    script: "han",
  },
};

const localeEntries = Object.entries(LOCALE_DETAILS) as [Locale, LocaleDetails][];

const configuredSiteUrl = process.env.NEXT_PUBLIC_SITE_URL ?? DEFAULT_SITE_URL;
const normalizedSiteUrl = configuredSiteUrl.startsWith("http")
  ? configuredSiteUrl
  : `https://${configuredSiteUrl}`;

export const SITE_URL = new URL(normalizedSiteUrl);

export function isLocale(value: string): value is Locale {
  return localeEntries.some(([locale]) => locale === value);
}

export function resolveLocale(value: string): Locale {
  return isLocale(value) ? value : "en";
}

function normalizePath(pathname: string) {
  if (!pathname || pathname === "/") {
    return "";
  }

  return pathname.startsWith("/") ? pathname : `/${pathname}`;
}

export function getLocalizedPath(locale: Locale, pathname = "") {
  const normalizedPath = normalizePath(pathname);

  return normalizedPath ? `/${locale}${normalizedPath}` : `/${locale}`;
}

export function getLocaleLabel(locale: Locale) {
  return LOCALE_DETAILS[locale].label;
}

export function getLocaleTag(locale: Locale) {
  return LOCALE_DETAILS[locale].languageTag;
}

export function getLocaleScript(locale: Locale) {
  return LOCALE_DETAILS[locale].script;
}

export function buildLanguageAlternates(pathname = "") {
  return Object.fromEntries(
    localeEntries.map(([locale, details]) => [
      details.languageTag,
      getLocalizedPath(locale, pathname),
    ])
  );
}

export function buildPageMetadata({
  locale,
  title,
  description,
  pathname = "/",
  keywords = [],
}: {
  locale: Locale;
  title: string;
  description: string;
  pathname?: string;
  keywords?: string[];
}): Metadata {
  const localizedPath = getLocalizedPath(locale, pathname);
  const canonicalUrl = new URL(localizedPath, SITE_URL).toString();
  const imageUrl = new URL("/dinomenu.png", SITE_URL).toString();

  return {
    metadataBase: SITE_URL,
    title,
    description,
    applicationName: SITE_NAME,
    keywords: [...new Set([SITE_NAME, "retro arcade shooter", ...keywords])],
    alternates: {
      canonical: canonicalUrl,
      languages: buildLanguageAlternates(pathname),
    },
    openGraph: {
      type: "website",
      siteName: SITE_NAME,
      title,
      description,
      url: canonicalUrl,
      locale: getLocaleTag(locale),
      images: [
        {
          url: imageUrl,
          width: 230,
          height: 230,
          alt: SITE_NAME,
        },
      ],
    },
    twitter: {
      card: "summary_large_image",
      title,
      description,
      images: [imageUrl],
    },
  };
}
