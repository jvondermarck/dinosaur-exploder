/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import { Locale } from "../../../i18n-config";
import { FaGithub, FaDiscord, FaHeart, FaCode } from "react-icons/fa";
import { FaXTwitter } from "react-icons/fa6";

export const metadata: Metadata = {
  title: "Contact · Dinosaur Exploder",
  description: "How to reach the Dinosaur Exploder community and maintainers.",
};

export default async function ContactPage({params,}: {params: Promise<{lang: string}>;}) {
  const {lang} = await params;
  const dict = await getDictionary(lang as Locale);
  const { contact } = dict;

  const socialLinks = [
    {
      ...contact.links.github,
      href: "https://github.com/jvondermarck/dinosaur-exploder/discussions",
      icon: <FaGithub size={36} />,
      color: "text-green-700 dark:text-green-300",
      border: "border-green-200 dark:border-green-500/40",
      hover: "hover:border-green-400 dark:hover:border-green-400",
    },
    {
      ...contact.links.discord,
      href: "https://discord.com/invite/nkmCRnXbWm",
      icon: <FaDiscord size={36} />,
      color: "text-indigo-600 dark:text-indigo-400",
      border: "border-indigo-200 dark:border-indigo-500/40",
      hover: "hover:border-indigo-400 dark:hover:border-indigo-400",
    },
    {
      ...contact.links.twitter,
      href: "https://twitter.com/DinosaurExplod1",
      icon: <FaXTwitter size={36} />,
      color: "text-neutral-800 dark:text-neutral-200",
      border: "border-neutral-200 dark:border-neutral-500/40",
      hover: "hover:border-neutral-400 dark:hover:border-neutral-400",
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

      {/* Social links — 3 columns, 1 row */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        {socialLinks.map((l) => (
          <a
            key={l.title}
            href={l.href}
            target="_blank"
            rel="noopener noreferrer"
            className={`bg-white/80 dark:bg-neutral-800/80 rounded-xl border ${l.border} ${l.hover} p-6 shadow-sm hover:shadow-md transition flex flex-col items-center text-center gap-3`}
          >
            <div className={l.color}>{l.icon}</div>
            <div>
              <div className="font-retro text-green-800 dark:text-green-200 text-sm mb-1">
                {l.title}
              </div>
              <div className="font-mono text-green-950 dark:text-green-300 opacity-80 text-sm">
                {l.description}
              </div>
            </div>
          </a>
        ))}
      </div>

      {/* Action row — Sponsor + Contribute, same weight */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Sponsor */}
        <a
          href="https://github.com/sponsors/jvondermarck"
          target="_blank"
          rel="noopener noreferrer"
          className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-pink-300 dark:border-pink-500/40 hover:border-pink-500 dark:hover:border-pink-400 p-5 shadow-sm hover:shadow-md transition flex flex-row items-center gap-4"
        >
          <div className="text-pink-600 dark:text-pink-400 flex-shrink-0">
            <FaHeart size={28} />
          </div>
          <div>
            <div className="font-retro text-pink-700 dark:text-pink-300 text-sm mb-1">
              {contact.links.sponsor.title}
            </div>
            <div className="font-mono text-green-950 dark:text-green-300 opacity-80 text-sm">
              {contact.links.sponsor.description}
            </div>
          </div>
        </a>

        {/* Contribute */}
        <div className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-300 dark:border-green-500/40 p-5 shadow-sm flex flex-row items-center gap-4">
          <div className="text-green-700 dark:text-green-400 flex-shrink-0">
            <FaCode size={28} />
          </div>
          <div>
            <div className="font-retro text-green-800 dark:text-green-200 text-sm mb-1">
              {contact.contribute.title}
            </div>
            <p className="font-mono text-green-950 dark:text-green-200 opacity-80 text-sm">
              {contact.contribute.description}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
