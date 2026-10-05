/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { Press_Start_2P } from "next/font/google";
import "../globals.css";
import React from "react";
import NavBar from "@/components/NavBar";
import Footer from "@/components/Footer";
import LocaleSwitcher from "@/components/LocaleSwitcher";
import ThemeToggle from "@/components/ThemeToggle";
import { ThemeProvider } from "@/components/ThemeProvider";
import { FaGithub } from "react-icons/fa";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../i18n-config";
import localFont from "next/font/local";

const ubuntuSansMono = localFont({
  src: "../fonts/UbuntuSansMono-VariableFont_wght.ttf",
  variable: "--font-ubuntu-sans-mono",
  display: "swap",
});

const geistSans = Geist({
  variable: "--cyrilic-font",
  subsets: ["latin", "cyrillic"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin", "cyrillic"],
});

const pressStart2P = Press_Start_2P({
  weight: "400",
  variable: "--font-press-start-2p",
  subsets: ["latin"],
  display: "swap",
});

export const metadata: Metadata = {
    title: "Dinosaur Exploder",
    description: "Open source retro shoot 'em up game.",
    verification: {
      google: 'gdP0ZxOyLsUaoHTFpX-fgS6eCOdbAr1AhMnFgLj330g',
    },
};

export default async function RootLayout({
        children,
        params,
    }: {
    children: React.ReactNode;
    params: Promise<{ lang: string }>;
}) {
    const {lang} = await params;
    const dict = await getDictionary(lang as Locale);

    return (
        <html lang={lang} suppressHydrationWarning className={`${geistSans.variable} ${geistMono.variable} ${pressStart2P.variable} ${ubuntuSansMono.variable}`}>
            <body className="antialiased">
                <ThemeProvider>
                    <div className="min-h-screen flex flex-col page-gradient overflow-x-hidden">
                        <div className="flex items-center justify-between gap-2 px-4 sm:px-6 py-2 border-b border-green-200/60 dark:border-green-500/20 dark:bg-white/5">
                            {/* Left: action icon buttons */}
                            <div className="flex items-center gap-1.5">
                                <a
                                    href="https://github.com/jvondermarck/dinosaur-exploder"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    title="GitHub"
                                    className="p-1.5 border-2 border-green-700 dark:border-green-500 text-green-700 dark:text-green-400 bg-white dark:bg-neutral-800 hover:bg-green-700 dark:hover:bg-green-600 hover:text-white transition-all shadow-[2px_2px_0px_0px_rgba(21,128,61,1)] dark:shadow-[2px_2px_0px_0px_theme(colors.green.500)] active:shadow-none active:translate-x-[1px] active:translate-y-[1px]"
                                >
                                    <FaGithub size={16} />
                                </a>
                                <a
                                    href="https://github.com/jvondermarck/dinosaur-exploder/stargazers"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    title="Star"
                                    className="p-1.5 border-2 border-yellow-500 dark:border-yellow-400 text-yellow-600 dark:text-yellow-300 bg-white dark:bg-neutral-800 hover:bg-yellow-500 dark:hover:bg-yellow-500 hover:text-white transition-all shadow-[2px_2px_0px_0px_theme(colors.yellow.400)] dark:shadow-[2px_2px_0px_0px_theme(colors.yellow.500)] active:shadow-none active:translate-x-[1px] active:translate-y-[1px]"
                                >
                                    <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
                                        <path d="M8 .25a.75.75 0 0 1 .673.418l1.882 3.815 4.21.612a.75.75 0 0 1 .416 1.279l-3.046 2.97.719 4.192a.751.751 0 0 1-1.088.791L8 12.347l-3.766 1.98a.75.75 0 0 1-1.088-.79l.72-4.194L.818 6.374a.75.75 0 0 1 .416-1.28l4.21-.611L7.327.668A.75.75 0 0 1 8 .25Z" />
                                    </svg>
                                </a>
                                <a
                                    href="https://github.com/sponsors/jvondermarck"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    title="Sponsor"
                                    className="p-1.5 border-2 border-pink-700 dark:border-pink-500 text-white bg-pink-600 dark:bg-pink-600 hover:bg-pink-700 dark:hover:bg-pink-700 transition-all shadow-[2px_2px_0px_0px_#A3245C] dark:shadow-[2px_2px_0px_0px_#db2777] active:shadow-none active:translate-x-[1px] active:translate-y-[1px]"
                                >
                                    <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
                                        <path d="M8 15C-7.333 4.868 3.278-3.04 7.824 1.143c.06.055.119.112.176.171a3.12 3.12 0 0 1 .176-.17C12.723-3.042 23.333 4.868 8 15z" />
                                    </svg>
                                </a>
                            </div>
                            {/* Right: locale + theme dropdowns */}
                            <div className="flex items-center gap-2">
                                <LocaleSwitcher />
                                <ThemeToggle />
                            </div>
                        </div>
                        <NavBar lang={lang} dict={dict}/>
                        <main className="flex-1 flex flex-col">{children}</main>
                        <Footer text={dict.footer}/>
                    </div>
                </ThemeProvider>
            </body>
        </html>
    );
}
