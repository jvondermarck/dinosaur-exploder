/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import fs from "fs";
import path from "path";
import matter from "gray-matter";
import { remark } from "remark";
import remarkHtml from "remark-html";

export type PostMeta = {
  slug: string;
  title: string;
  author: string;
  date: string; // "YYYY-MM-DD"
  excerpt?: string;
};

export type Post = PostMeta & {
  contentHtml: string;
};

const postsDir = path.join(process.cwd(), "content", "news");

export function getAllPostsMeta(): PostMeta[] {
  if (!fs.existsSync(postsDir)) return [];

  const files = fs
    .readdirSync(postsDir)
    .filter((f) => f.endsWith(".md"))
    .sort()
    .reverse();

  return files.map((filename) => {
    const slug = filename
      .replace(/\.md$/, "")
      .replace(/^\d{4}-\d{2}-\d{2}-/, "");
    const raw = fs.readFileSync(path.join(postsDir, filename), "utf8");
    const { data } = matter(raw);
    return {
      slug,
      title: String(data.title ?? ""),
      author: String(data.author ?? ""),
      date: String(data.date ?? ""),
      excerpt: data.excerpt ? String(data.excerpt) : undefined,
    };
  });
}

/** Returns [[year, posts[]], ...] sorted newest first */
export function groupByYear(posts: PostMeta[]): [number, PostMeta[]][] {
  const map = new Map<number, PostMeta[]>();
  for (const post of posts) {
    const year = new Date(post.date + "T12:00:00").getFullYear();
    if (!map.has(year)) map.set(year, []);
    map.get(year)!.push(post);
  }
  return Array.from(map.entries()).sort(([a], [b]) => b - a);
}

export async function getPostBySlug(slug: string): Promise<Post | null> {
  if (!fs.existsSync(postsDir)) return null;

  const files = fs.readdirSync(postsDir).filter((f) => f.endsWith(".md"));
  const file = files.find(
    (f) =>
      f.replace(/\.md$/, "").replace(/^\d{4}-\d{2}-\d{2}-/, "") === slug
  );
  if (!file) return null;

  const raw = fs.readFileSync(path.join(postsDir, file), "utf8");
  const { data, content } = matter(raw);
  const processed = await remark()
    .use(remarkHtml as any, { sanitize: false })
    .process(content);

  return {
    slug,
    title: String(data.title ?? ""),
    author: String(data.author ?? ""),
    date: String(data.date ?? ""),
    excerpt: data.excerpt ? String(data.excerpt) : undefined,
    contentHtml: processed.toString(),
  };
}

export function formatDate(dateStr: string): string {
  return new Date(dateStr + "T12:00:00").toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

export function formatDateShort(dateStr: string): string {
  return new Date(dateStr + "T12:00:00").toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
}
