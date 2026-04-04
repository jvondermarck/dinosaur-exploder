/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import {
  Geist,
  Geist_Mono,
  IBM_Plex_Mono,
  IBM_Plex_Sans,
  Press_Start_2P,
} from "next/font/google";
import "../globals.css";
import React from "react";
import NavBar from "@/components/NavBar";
import Footer from "@/components/Footer";
import LocaleSwitcher from "@/components/LocaleSwitcher";
import ThemeToggle from "@/components/ThemeToggle";
import { ThemeProvider } from "@/components/ThemeProvider";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../i18n-config";
import localFont from "next/font/local";
import {
  buildPageMetadata,
  getLocaleScript,
  getLocaleTag,
  resolveLocale,
  SITE_NAME,
} from "@/lib/site";

const fushion_12 = localFont({
  src: "../fonts/zh_hans-12px.ttf.woff2",
  variable: "--font-fushion-12",
  display: "swap",
});

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

const pressStart2P = Press_Start_2P({
  weight: "400",
  variable: "--font-press-start-2p",
  subsets: ["latin"],
  display: "swap",
});

const ibmPlexSans = IBM_Plex_Sans({
  weight: ["400", "500", "600", "700"],
  variable: "--font-ibm-plex-sans",
  subsets: ["latin", "cyrillic", "greek"],
  display: "swap",
});

const ibmPlexMono = IBM_Plex_Mono({
  weight: ["400", "500", "600", "700"],
  variable: "--font-ibm-plex-mono",
  subsets: ["latin", "cyrillic", "greek"],
  display: "swap",
});

export async function generateMetadata({
  params,
}: {
  params: Promise<{ lang: string }>;
}): Promise<Metadata> {
  const { lang } = await params;
  const locale = resolveLocale(lang);
  const dict = await getDictionary(locale);

  return {
    ...buildPageMetadata({
      locale,
      title: SITE_NAME,
      description: dict.homePage.description.join(" "),
      pathname: "/",
      keywords: ["java fxgl game", "next.js docs site", "browser arcade game"],
    }),
    verification: {
      google: "gdP0ZxOyLsUaoHTFpX-fgS6eCOdbAr1AhMnFgLj330g",
    },
  };
}

export default async function RootLayout({
        children,
        params,
    }: {
    children: React.ReactNode;
    params: Promise<{ lang: string }>;
}) {
    const {lang} = await params;
    const locale = resolveLocale(lang);
    const dict = await getDictionary(locale as Locale);

    return (
        <html
            lang={getLocaleTag(locale)}
            data-locale={locale}
            data-script={getLocaleScript(locale)}
            suppressHydrationWarning
            className={`${geistSans.variable} ${geistMono.variable} ${pressStart2P.variable} ${fushion_12.variable} ${ibmPlexSans.variable} ${ibmPlexMono.variable}`}
        >
            <body className="antialiased">
                <ThemeProvider>
                    <div className="min-h-screen flex flex-col page-gradient">
                        <div className="flex flex-wrap items-center justify-center sm:justify-end gap-2 px-6 py-2 dark:border-b dark:border-green-500/20 dark:bg-white/5">
                            <LocaleSwitcher />
                            <ThemeToggle />
                        </div>
                        <NavBar lang={locale} dict={dict}/>
                        <main className="flex-1 flex flex-col">{children}</main>
                        <Footer text={dict.footer}/>
                    </div>
                </ThemeProvider>
            </body>
        </html>
    );
}
