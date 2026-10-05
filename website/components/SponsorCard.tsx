/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { FaHeart } from "react-icons/fa";

type SponsorCardProps = {
  title: string;
  description: string;
  ctaLabel: string;
};

export default function SponsorCard({
  title,
  description,
  ctaLabel,
}: SponsorCardProps) {
  return (
    <section aria-labelledby="sponsor-card-title" className="w-full px-4 pb-10">
      <div className="mx-auto flex w-full max-w-4xl flex-col gap-6 rounded-xl border-2 border-green-700 bg-white/90 p-6 shadow-[4px_4px_0px_0px_theme(colors.green.700)] dark:border-green-500 dark:bg-neutral-900/90 dark:shadow-[4px_4px_0px_0px_theme(colors.green.500)] sm:flex-row sm:items-center sm:justify-between md:px-8">
        <div className="flex items-start gap-4">
          <div className="flex-shrink-0 rounded-lg border-2 border-pink-300 bg-pink-100 p-3 text-pink-700 dark:border-pink-500/60 dark:bg-pink-950/60 dark:text-pink-300">
            <FaHeart aria-hidden="true" size={28} />
          </div>

          <div>
            <h2
              id="sponsor-card-title"
              className="font-retro text-lg leading-relaxed text-green-800 dark:text-green-200 md:text-xl"
            >
              {title}
            </h2>
            <p className="mt-3 max-w-2xl font-mono text-sm leading-relaxed text-green-950 dark:text-green-100 md:text-base">
              {description}
            </p>
          </div>
        </div>

        <a
          href="https://github.com/sponsors/jvondermarck"
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex w-full flex-shrink-0 items-center justify-center gap-2 rounded-full border-2 border-pink-900 bg-pink-700 px-6 py-3 font-retro text-sm text-white shadow-[3px_3px_0px_0px_theme(colors.pink.950)] transition hover:bg-pink-800 focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-pink-400 focus-visible:ring-offset-4 focus-visible:ring-offset-white dark:border-pink-300 dark:shadow-[3px_3px_0px_0px_theme(colors.pink.300)] dark:focus-visible:ring-pink-300 dark:focus-visible:ring-offset-neutral-900 sm:w-auto"
        >
          <FaHeart aria-hidden="true" />
          <span>{ctaLabel}</span>
        </a>
      </div>
    </section>
  );
}
