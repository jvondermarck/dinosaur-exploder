/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";

type Release = {
  id: number;
  tag_name: string;
  name: string;
  body: string;
  html_url: string;
  published_at: string;
};

export const metadata: Metadata = {
  title: "Changelog · Dinosaur Exploder",
  description: "Release history and version notes for Dinosaur Exploder.",
};

async function getReleases(): Promise<Release[]> {
  const res = await fetch(
    "https://api.github.com/repos/jvondermarck/dinosaur-exploder/releases?per_page=100",
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

export default async function ChangelogPage() {
  const releases = await getReleases();

  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-3">
        Changelog
      </h1>
      <p className="font-mono text-green-950 dark:text-green-100 mb-8">
        All notable releases of Dinosaur Exploder, pulled directly from GitHub.
      </p>

      {releases.length === 0 ? (
        <div className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-5 shadow-sm">
          <p className="font-mono text-green-950 dark:text-green-100">
            Couldn&apos;t load releases right now (GitHub API limit or network). Please try again later.
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {releases.map((release) => (
            <div
              key={release.id}
              className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-5 shadow-sm"
            >
              <div className="flex flex-wrap items-center gap-3 mb-3">
                <a
                  href={release.html_url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="font-retro text-lg text-green-800 dark:text-green-200 hover:underline"
                >
                  {release.tag_name}
                </a>
                {release.name && release.name !== release.tag_name && (
                  <span className="font-mono text-sm text-green-950 dark:text-green-300 opacity-80">
                    — {release.name}
                  </span>
                )}
                <span className="ml-auto font-mono text-xs text-green-950 dark:text-green-400 opacity-70">
                  {new Date(release.published_at).toLocaleDateString(undefined, {
                    year: "numeric",
                    month: "long",
                    day: "numeric",
                  })}
                </span>
              </div>
              {release.body ? (
                <pre className="font-mono text-sm text-green-950 dark:text-green-100 whitespace-pre-wrap leading-relaxed">
                  {release.body}
                </pre>
              ) : (
                <p className="font-mono text-sm text-green-950 dark:text-green-300 opacity-60 italic">
                  No description provided.
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
