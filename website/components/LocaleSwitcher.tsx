"use client";


/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { usePathname } from "next/navigation";
import Link from "next/link";
import { i18n } from "../i18n-config";
import { useState } from "react";

const languageNames: Record<string, string> = {
  en: "English",
  el: "Ελληνικά",
};

export default function LocaleSwitcher() {
  const pathname = usePathname();
  const [isOpen, setIsOpen] = useState(false);

  const redirectedPathname = (locale: string) => {
    if (!pathname) return "/";
    const segments = pathname.split("/");
    segments[1] = locale;
    return segments.join("/");
  };

  const currentLocale = pathname.split("/")[1] || i18n.defaultLocale;

  return (
    <div className="flex justify-center sm:justify-end px-6 py-2 bg-black/5 border-b border-green-700/10">
      <div className="relative inline-block text-left">
        <div className="flex items-center gap-2">
          
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="bg-white border-2 border-green-700 text-green-700 px-3 py-1 font-mono text-xs font-bold uppercase hover:bg-green-700 hover:text-white transition-all shadow-[2px_2px_0px_0px_rgba(21,128,61,1)] active:shadow-none active:translate-x-[1px] active:translate-y-[1px]"
          >
            {languageNames[currentLocale]} {isOpen ? "▲" : "▼"}
          </button>
        </div>

        {isOpen && (
          <div className="absolute right-0 mt-2 w-40 bg-white border-2 border-green-700 shadow-[4px_4px_0px_0px_rgba(21,128,61,1)] z-[100]">
            <div className="py-1">
              {i18n.locales.map((locale) => (
                <Link
                  key={locale}
                  href={redirectedPathname(locale)}
                  onClick={() => setIsOpen(false)}
                  className={`block px-4 py-2 text-xs font-mono font-bold uppercase transition-colors ${
                    currentLocale === locale
                      ? "bg-green-100 text-green-800"
                      : "text-green-700 hover:bg-green-700 hover:text-white"
                  }`}
                >
                  {languageNames[locale]}
                </Link>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}