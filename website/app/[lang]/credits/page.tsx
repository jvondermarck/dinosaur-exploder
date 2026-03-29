/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import Image from "next/image";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../../i18n-config";

type Contributor = {
  id: number;
  login: string;
  avatar_url: string;
  html_url: string;
  contributions: number;
};

export const metadata: Metadata = {
  title: "Credits · Dinosaur Exploder",
  description: "A big thank-you to everyone who contributed to Dinosaur Exploder.",
};

async function getContributors(): Promise<Contributor[]> {
  const res = await fetch(
    "https://api.github.com/repos/jvondermarck/dinosaur-exploder/contributors?per_page=100",
    {
      next: { revalidate: 3600 },
      headers: {
        "User-Agent": "dinosaur-exploder-website",
        Accept: "application/vnd.github+json",
      },
    }
  );

  if (!res.ok) return [];
  return res.json();
}

export default async function CreditsPage({params,}: {params: Promise<{lang: string}>;}) {
  const contributors = await getContributors();
  const {lang} = await params;
  const dict = await getDictionary(lang as Locale);

  return (
    <div className="max-w-5xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-3">
        {dict.credits.title}
      </h1>
      <p className="font-mono text-green-950 dark:text-green-100 mb-8">
        {dict.credits.description}
      </p>

      {contributors.length === 0 ? (
        <div className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-5 shadow-sm">
          <p className="font-mono text-green-950 dark:text-green-100">
            {dict.credits.error}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {contributors.map((c) => (
            <a
              key={c.id}
              href={c.html_url}
              target="_blank"
              rel="noopener noreferrer"
              className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-4 shadow-sm hover:shadow-md transition"
            >
              <div className="flex items-center gap-3">
                <Image
                  src={c.avatar_url}
                  alt={c.login}
                  width={48}
                  height={48}
                  className="rounded-full border border-green-200 dark:border-green-500/40"
                />
                <div className="min-w-0">
                  <div className="font-retro text-green-800 dark:text-green-200 text-base truncate">
                    {c.login}
                  </div>
                  <div className="font-mono text-xs text-green-950 dark:text-green-300 opacity-80">
                    {c.contributions} {c.contributions === 1 ? dict.credits.single : dict.credits.plural}
                  </div>
                </div>
              </div>
            </a>
          ))}
        </div>
      )}

      <div className="mt-10 bg-black/80 dark:bg-neutral-800/90 rounded-xl border-2 border-green-700 dark:border-green-500 p-5 shadow-sm">
        <h2 className="font-retro text-xl text-green-300 dark:text-green-400 mb-2"> {dict.credits.addName.title} </h2>
        <p className="font-mono text-green-100 dark:text-green-200">
          {dict.credits.addName.description}
        </p>
      </div>
    </div>
  );
}
