/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import { Locale } from "../../../i18n-config";
import { getAllPostsMeta, groupByYear } from "@/lib/news";
import NewsYearGroup from "@/components/news/NewsYearGroup";

export const metadata: Metadata = {
  title: "News · Dinosaur Exploder",
  description: "Latest updates, releases and announcements from the Dinosaur Exploder project.",
};

export default async function NewsPage({
  params,
}: {
  params: Promise<{ lang: string }>;
}) {
  const { lang } = await params;
  const dict = await getDictionary(lang as Locale);
  const { news } = dict as any;

  const posts = getAllPostsMeta();
  const grouped = groupByYear(posts);

  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-3">
        {news.title}
      </h1>
      <p className="font-mono text-green-950 dark:text-green-100 mb-10">
        {news.description}
      </p>

      {grouped.length === 0 ? (
        <p className="font-mono text-green-700 dark:text-green-400">
          No posts yet. Check back soon!
        </p>
      ) : (
        grouped.map(([year, yearPosts]) => (
          <NewsYearGroup
            key={year}
            year={year}
            posts={yearPosts}
            lang={lang}
            dict={news}
          />
        ))
      )}
    </div>
  );
}
