/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../../i18n-config";
import { buildPageMetadata, resolveLocale, SITE_NAME } from "@/lib/site";

export async function generateMetadata({
  params,
}: {
  params: Promise<{ lang: string }>;
}): Promise<Metadata> {
  const { lang } = await params;
  const locale = resolveLocale(lang);
  const dict = await getDictionary(locale);

  return buildPageMetadata({
    locale,
    title: `${dict.contact.title} | ${SITE_NAME}`,
    description: dict.contact.description,
    pathname: "/contact",
    keywords: ["community", "contact", "feedback", "review"],
  });
}

export default async function ContactPage({params,}: {params: Promise<{lang: string}>;}) {
  const {lang} = await params;
  const locale = resolveLocale(lang);
  const dict = await getDictionary(locale as Locale);
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

  const reviewLinks = [
    {
      ...contact.review.links.github,
      href: "https://github.com/jvondermarck/dinosaur-exploder/discussions",
    },
    {
      ...contact.review.links.star,
      href: "https://github.com/jvondermarck/dinosaur-exploder/stargazers",
    },
    {
      ...contact.review.links.discord,
      href: "https://discord.com/invite/nkmCRnXbWm",
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

      <div className="mt-10 rounded-xl border border-green-200 dark:border-green-500/40 bg-white/85 dark:bg-neutral-800/85 p-5 shadow-sm">
        <h2 className="font-retro text-xl text-green-800 dark:text-green-200 mb-2">
          {contact.review.title}
        </h2>
        <p className="font-mono text-green-950 dark:text-green-100 mb-5">
          {contact.review.description}
        </p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {reviewLinks.map((link) => (
            <a
              key={link.title}
              href={link.href}
              target="_blank"
              rel="noopener noreferrer"
              className="rounded-xl border border-green-200 dark:border-green-500/40 bg-green-50/80 dark:bg-neutral-900/60 p-4 transition hover:-translate-y-0.5 hover:shadow-md"
            >
              <div className="font-retro text-green-800 dark:text-green-200 text-base mb-2">
                {link.title}
              </div>
              <div className="font-mono text-sm text-green-950 dark:text-green-300 opacity-80">
                {link.description}
              </div>
            </a>
          ))}
        </div>
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
