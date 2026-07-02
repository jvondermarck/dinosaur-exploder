/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import Link from "next/link";
import { PostMeta, formatDateShort } from "@/lib/news";

type Props = {
  year: number;
  posts: PostMeta[];
  lang: string;
  dict: { postedBy: string; readMore: string };
};

export default function NewsYearGroup({ year, posts, lang, dict }: Props) {
  return (
    <div className="mb-8">
      {/* Year header bar */}
      <div className="bg-green-800 dark:bg-green-700 px-5 py-2.5 flex items-center rounded-t-lg">
        <span className="font-retro text-white text-base tracking-widest">{year}</span>
      </div>

      {/* Posts panel */}
      <div className="bg-white/80 dark:bg-neutral-800/80 border border-t-0 border-green-200 dark:border-green-500/40 rounded-b-lg divide-y divide-green-100 dark:divide-green-500/20">
        {posts.map((post) => (
          <div
            key={post.slug}
            className="px-5 py-4 flex flex-col sm:flex-row sm:items-start sm:justify-between gap-1"
          >
            <div className="flex-1 min-w-0">
              <Link
                href={`/${lang}/news/${post.slug}`}
                className="font-mono font-bold text-green-700 dark:text-green-300 hover:text-green-500 dark:hover:text-green-100 hover:underline underline-offset-2 transition-colors"
              >
                {post.title}
              </Link>
              {post.excerpt && (
                <p className="font-mono text-sm text-green-800/60 dark:text-green-300/60 mt-0.5 line-clamp-1">
                  {post.excerpt}
                </p>
              )}
              <p className="font-mono text-xs text-green-600/60 dark:text-green-400/50 mt-1">
                {dict.postedBy} <span className="font-bold">{post.author}</span>
              </p>
            </div>
            <div className="flex-shrink-0 flex items-center gap-3 mt-1 sm:mt-0 sm:ml-6">
              <span className="font-mono text-xs text-green-600/50 dark:text-green-400/50 whitespace-nowrap">
                {formatDateShort(post.date)}
              </span>
              <Link
                href={`/${lang}/news/${post.slug}`}
                className="font-mono text-xs border border-green-600 dark:border-green-500 text-green-700 dark:text-green-400 px-2 py-0.5 hover:bg-green-700 hover:text-white dark:hover:bg-green-600 dark:hover:text-white transition-colors whitespace-nowrap"
              >
                {dict.readMore}
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
