/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { notFound } from "next/navigation";
import Link from "next/link";
import { getDictionary } from "@/getDictionary";
import { Locale } from "../../../../i18n-config";
import { getPostBySlug, getAllPostsMeta, formatDate } from "@/lib/news";
import PostBody from "@/components/news/PostBody";
import GiscusNews from "@/components/GiscusNews";

type Props = { params: Promise<{ lang: string; slug: string }> };

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const post = await getPostBySlug(slug);
  if (!post) return { title: "Not Found · Dinosaur Exploder" };
  return {
    title: `${post.title} · Dinosaur Exploder`,
    description: post.excerpt,
  };
}

export async function generateStaticParams() {
  const posts = getAllPostsMeta();
  return posts.map((p) => ({ slug: p.slug }));
}

export default async function NewsPostPage({ params }: Props) {
  const { lang, slug } = await params;
  const dict = await getDictionary(lang as Locale);
  const { news } = dict as any;

  const post = await getPostBySlug(slug);
  if (!post) notFound();

  return (
    <div className="max-w-3xl mx-auto w-full px-4 md:px-8 py-10">

      {/* Breadcrumb bar */}
      <div className="inline-block mb-8">
        <Link
          href={`/${lang}/news`}
          className="font-mono text-sm text-green-700 dark:text-green-300 bg-green-100 dark:bg-green-900/40 border border-green-300 dark:border-green-600 px-4 py-2 hover:bg-green-700 hover:text-white dark:hover:bg-green-700 transition-colors"
        >
          ← {news.backToNews}
        </Link>
      </div>

      {/* Post metadata */}
      <p className="font-mono text-xs text-green-600/70 dark:text-green-400/60 mb-3 uppercase tracking-widest">
        {news.postedBy} <strong>{post.author}</strong> {news.on}{" "}
        {formatDate(post.date)}
      </p>

      {/* Title */}
      <h1 className="font-retro text-2xl md:text-3xl text-green-800 dark:text-green-200 mb-8 leading-snug">
        {post.title}
      </h1>

      {/* Post body */}
      <div className="bg-white/80 dark:bg-neutral-800/80 rounded-xl border border-green-200 dark:border-green-500/40 p-6 md:p-8 shadow-sm mb-12">
        <PostBody contentHtml={post.contentHtml} />
      </div>

      {/* Divider */}
      <div className="border-t-2 border-green-200 dark:border-green-500/30 mb-10" />

      {/* Comments */}
      <h2 className="font-retro text-xl text-green-800 dark:text-green-200 mb-6">
        {news.comments}
      </h2>
      <GiscusNews />

    </div>
  );
}
