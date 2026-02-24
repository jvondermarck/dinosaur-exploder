/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../../i18n-config";

export const metadata: Metadata = {
  title: "Contact · Dinosaur Exploder",
  description: "How to reach the Dinosaur Exploder community and maintainers.",
};

export default async function ContactPage({params,}: {params: Promise<{lang: string}>;}) {
  const {lang} = await params;
  const dict = await getDictionary(lang as Locale);
  const { contact } = dict;

  const contactLinks = [
    {
      ...contact.links.github,
      href: "https://github.com/jvondermarck/dinosaur-exploder/discussions",
    },
    {
      ...contact.links.discord,
      href: "https://discord.com/invite/nkmCRnXbWm",
    },
    {
      ...contact.links.twitter,
      href: "https://twitter.com/DinosaurExplod1",
    },
    {
      ...contact.links.sponsor,
      href: "https://github.com/sponsors/jvondermarck",
    },
  ];
  
  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-3">
        {contact.title}
      </h1>
      <p className="font-mono text-green-950 dark:text-green-100 mb-8">
        {contact.description}
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {contactLinks.map((l) => (
          <a
            key={l.title}
            href={l.href}
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-5 shadow-sm hover:shadow-md transition"
          >
            <div className="font-retro text-green-800 dark:text-green-200 text-lg mb-1">
              {l.title}
            </div>
            <div className="font-mono text-green-950 dark:text-green-300 opacity-80 text-sm">
              {l.description}
            </div>
          </a>
        ))}
      </div>

      <div className="mt-10 bg-black/80 dark:bg-neutral-800/90 rounded-xl border-2 border-green-700 dark:border-green-500 p-5 shadow-sm">
        <h2 className="font-retro text-xl text-green-300 dark:text-green-400 mb-2"> {contact.contribute.title} </h2>
        <p className="font-mono text-green-100 dark:text-green-200">
          {contact.contribute.description}
        </p>
      </div>
    </div>
  );
}
