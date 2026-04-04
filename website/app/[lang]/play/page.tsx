/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import Link from "next/link";
import DinosaurArcadeGame from "@/components/game/DinosaurArcadeGame";
import { getDictionary } from "@/getDictionary";
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
    title: `${dict.playPage.title} | ${SITE_NAME}`,
    description: dict.playPage.description,
    pathname: "/play",
    keywords: ["browser game", "play in browser", "retro shooter", "web arcade"],
  });
}

export default async function PlayPage({
  params,
}: {
  params: Promise<{ lang: string }>;
}) {
  const { lang } = await params;
  const locale = resolveLocale(lang);
  const dict = await getDictionary(locale);
  const { playPage, howGameWorks, contact } = dict;

  return (
    <div className="max-w-6xl mx-auto w-full px-4 md:px-8 py-10">
      <div className="grid gap-8 lg:grid-cols-[1.3fr_0.7fr]">
        <section className="space-y-5">
          <div className="space-y-3">
            <p className="font-mono text-sm uppercase tracking-[0.25em] text-green-700 dark:text-green-400">
              {playPage.kicker}
            </p>
            <h1 className="font-retro text-3xl md:text-5xl text-green-800 dark:text-green-200">
              {playPage.title}
            </h1>
            <p className="font-mono text-green-950 dark:text-green-100 max-w-3xl leading-relaxed">
              {playPage.description}
            </p>
          </div>

          <div className="rounded-2xl border-2 border-green-700/60 dark:border-green-500/60 bg-black/85 p-4 shadow-xl">
            <DinosaurArcadeGame />
          </div>
        </section>

        <aside className="space-y-6">
          <section className="rounded-2xl border border-green-200 dark:border-green-500/40 bg-white/85 dark:bg-neutral-800/85 p-5 shadow-sm">
            <h2 className="font-retro text-xl text-green-800 dark:text-green-200 mb-3">
              {playPage.noteTitle}
            </h2>
            <p className="font-mono text-green-950 dark:text-green-100 leading-relaxed">
              {playPage.note}
            </p>
          </section>

          <section className="rounded-2xl border border-green-200 dark:border-green-500/40 bg-white/85 dark:bg-neutral-800/85 p-5 shadow-sm">
            <h2 className="font-retro text-xl text-green-800 dark:text-green-200 mb-3">
              {howGameWorks.controls.title}
            </h2>
            <div className="space-y-3">
              {howGameWorks.controls.list.map(
                (item: { key: string; action: string }, index: number) => (
                  <div
                    key={`${item.key}-${index}`}
                    className="rounded-xl border border-green-200/80 dark:border-green-500/30 bg-green-50/80 dark:bg-neutral-900/70 px-4 py-3"
                  >
                    <div className="font-retro text-sm text-green-700 dark:text-green-300">
                      {item.key}
                    </div>
                    <div className="font-mono text-sm text-green-950 dark:text-green-100 mt-1">
                      {item.action}
                    </div>
                  </div>
                )
              )}
            </div>
          </section>

          <section className="rounded-2xl border-2 border-green-700 dark:border-green-500 bg-black/85 p-5 shadow-sm">
            <h2 className="font-retro text-xl text-green-300 dark:text-green-400 mb-3">
              {contact.review.title}
            </h2>
            <p className="font-mono text-green-100 dark:text-green-200 leading-relaxed mb-4">
              {contact.review.description}
            </p>
            <div className="flex flex-wrap gap-3">
              <a
                href="https://github.com/jvondermarck/dinosaur-exploder/discussions"
                target="_blank"
                rel="noopener noreferrer"
                className="font-retro inline-flex items-center justify-center rounded-full bg-green-600 px-4 py-2 text-sm text-white transition hover:bg-green-500"
              >
                {contact.review.links.github.title}
              </a>
              <a
                href="https://github.com/jvondermarck/dinosaur-exploder/stargazers"
                target="_blank"
                rel="noopener noreferrer"
                className="font-retro inline-flex items-center justify-center rounded-full border border-yellow-400 px-4 py-2 text-sm text-yellow-300 transition hover:bg-yellow-500 hover:text-white"
              >
                {contact.review.links.star.title}
              </a>
            </div>
            <Link
              href={`/${locale}/contact`}
              className="mt-4 inline-flex font-mono text-sm text-green-200 underline underline-offset-4 hover:text-white"
            >
              {contact.title}
            </Link>
          </section>
        </aside>
      </div>
    </div>
  );
}
