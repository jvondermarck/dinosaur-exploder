/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { ReactNode } from "react";

// Conventional commit types → Tailwind color classes (listed statically so Tailwind doesn't purge them)
const COMMIT_STYLES: Record<string, { bg: string; text: string }> = {
  feat:     { bg: "bg-green-100 dark:bg-green-900/50",     text: "text-green-800 dark:text-green-300" },
  fix:      { bg: "bg-red-100 dark:bg-red-900/50",         text: "text-red-800 dark:text-red-300" },
  chore:    { bg: "bg-neutral-200 dark:bg-neutral-700/60", text: "text-neutral-600 dark:text-neutral-400" },
  docs:     { bg: "bg-blue-100 dark:bg-blue-900/50",       text: "text-blue-800 dark:text-blue-300" },
  style:    { bg: "bg-purple-100 dark:bg-purple-900/50",   text: "text-purple-800 dark:text-purple-300" },
  refactor: { bg: "bg-yellow-100 dark:bg-yellow-900/50",   text: "text-yellow-800 dark:text-yellow-300" },
  test:     { bg: "bg-orange-100 dark:bg-orange-900/50",   text: "text-orange-800 dark:text-orange-300" },
  ci:       { bg: "bg-indigo-100 dark:bg-indigo-900/50",   text: "text-indigo-800 dark:text-indigo-300" },
  perf:     { bg: "bg-pink-100 dark:bg-pink-900/50",       text: "text-pink-800 dark:text-pink-300" },
  build:    { bg: "bg-teal-100 dark:bg-teal-900/50",       text: "text-teal-800 dark:text-teal-300" },
  revert:   { bg: "bg-rose-100 dark:bg-rose-900/50",       text: "text-rose-800 dark:text-rose-300" },
};
const FALLBACK_STYLE = { bg: "bg-neutral-200 dark:bg-neutral-700/60", text: "text-neutral-600 dark:text-neutral-400" };

const COMMIT_TYPE_RE    = /^(feat|fix|chore|docs|style|refactor|test|ci|perf|build|revert)(!?)(\([^)]*\))?\s*:/i;
const FULL_CHANGELOG_RE = /^\*{0,2}Full Changelog\*{0,2}:\s*(https:\/\/github\.com\/\S+)/i;

const isSafeGitHubUrl = (url: string): boolean =>
  /^https:\/\/github\.com\//.test(url);

// Splits a text fragment into plain strings and React nodes (PR links, @mentions)
function parseInline(text: string, prefix: string): ReactNode[] {
  // New regex instances per call — no shared mutable lastIndex state
  const PR_URL_RE  = /https:\/\/github\.com\/[^\s/]+\/[^\s/]+\/pull\/(\d+)/g;
  const MENTION_RE = /@([a-zA-Z0-9-]+)/g;

  const hits: Array<{ index: number; end: number; node: ReactNode }> = [];
  let m: RegExpExecArray | null;

  while ((m = PR_URL_RE.exec(text)) !== null) {
    const url   = m[0];
    const prNum = m[1];
    if (!isSafeGitHubUrl(url)) continue;
    hits.push({
      index: m.index,
      end:   m.index + url.length,
      node: (
        <a
          key={`${prefix}-pr-${m.index}`}
          href={url}
          target="_blank"
          rel="noopener noreferrer"
          className="font-semibold text-green-700 dark:text-green-400 hover:underline"
        >
          #{prNum}
        </a>
      ),
    });
  }

  while ((m = MENTION_RE.exec(text)) !== null) {
    const username = m[1];
    const mentionUrl = `https://github.com/${username}`;
    hits.push({
      index: m.index,
      end:   m.index + m[0].length,
      node: (
        <a
          key={`${prefix}-mention-${m.index}`}
          href={mentionUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="font-semibold text-blue-700 dark:text-blue-400 hover:underline"
        >
          @{username}
        </a>
      ),
    });
  }

  // Sort by position and drop overlapping matches
  hits.sort((a, b) => a.index - b.index);
  const safe: typeof hits = [];
  let cursor = 0;
  for (const h of hits) {
    if (h.index >= cursor) { safe.push(h); cursor = h.end; }
  }

  const parts: ReactNode[] = [];
  let last = 0;
  for (const h of safe) {
    if (h.index > last) parts.push(text.slice(last, h.index));
    parts.push(h.node);
    last = h.end;
  }
  if (last < text.length) parts.push(text.slice(last));
  return parts;
}

// Parses a GitHub release body (markdown subset) into styled JSX
export default function ReleaseBody({ body }: { body: string }) {
  const lines   = body.split(/\r?\n/);
  const blocks: ReactNode[] = [];
  let   bullets: ReactNode[] = [];

  const flushBullets = (key: string) => {
    if (bullets.length === 0) return;
    blocks.push(
      <ul key={`ul-${key}`} className="space-y-1.5 mb-2">
        {bullets}
      </ul>
    );
    bullets = [];
  };

  lines.forEach((raw, i) => {
    const line = raw.trim();

    if (!line) {
      flushBullets(`gap-${i}`);
      return;
    }

    // ## / ### → section header
    if (/^#{2,3}\s/.test(line)) {
      flushBullets(`pre-h-${i}`);
      const text = line.replace(/^#{2,3}\s+/, "");
      blocks.push(
        <p key={`h-${i}`} className="font-retro text-xs text-green-700 dark:text-green-400 uppercase tracking-widest mt-4 mb-2 border-b border-green-200 dark:border-green-700/50 pb-1">
          {text}
        </p>
      );
      return;
    }

    // Full Changelog footer link → shorten to version range only
    const clMatch = line.match(FULL_CHANGELOG_RE);
    if (clMatch) {
      flushBullets(`pre-cl-${i}`);
      const url        = clMatch[1];
      const shortLabel = url.replace(/^https:\/\/github\.com\/[^/]+\/[^/]+\/compare\//, "");
      blocks.push(
        <p key={`cl-${i}`} className="mt-3 font-mono text-xs text-green-950 dark:text-green-400 opacity-60">
          Full Changelog:{" "}
          <a href={url} target="_blank" rel="noopener noreferrer" className="hover:underline">
            {shortLabel}
          </a>
        </p>
      );
      return;
    }

    // Bullet line (* or -) → commit-type badge + inline parsing
    const bulletMatch = line.match(/^[*-]\s+(.*)/);
    if (bulletMatch) {
      const content   = bulletMatch[1];
      const typeMatch = content.match(COMMIT_TYPE_RE);
      let badge: ReactNode = null;
      let rest  = content;

      if (typeMatch) {
        const type     = typeMatch[1].toLowerCase();
        const breaking = typeMatch[2] === "!";
        const scope    = typeMatch[3] ?? "";
        const s        = COMMIT_STYLES[type] ?? FALLBACK_STYLE;
        badge = (
          <span className={`inline-block px-1.5 py-0.5 rounded text-[10px] font-mono font-bold flex-shrink-0 ${s.bg} ${s.text}`}>
            {type}{scope}{breaking ? "!" : ""}
          </span>
        );
        rest = content.slice(typeMatch[0].length).trim();
      }

      bullets.push(
        <li key={`li-${i}`} className="flex items-start gap-2">
          <span className="mt-[7px] w-1.5 h-1.5 rounded-full bg-green-400 dark:bg-green-500 flex-shrink-0" />
          <span className="flex-1 font-mono text-sm text-green-950 dark:text-green-100 flex flex-wrap items-center gap-x-1.5 gap-y-0.5">
            {badge}
            {parseInline(rest, `${i}`)}
          </span>
        </li>
      );
      return;
    }

    // Plain text
    flushBullets(`pre-p-${i}`);
    blocks.push(
      <p key={`p-${i}`} className="font-mono text-sm text-green-950 dark:text-green-100 mb-1">
        {parseInline(line, `p-${i}`)}
      </p>
    );
  });

  flushBullets("end");
  return <>{blocks}</>;
}
