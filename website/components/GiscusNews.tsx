"use client";

/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import Giscus from "@giscus/react";
import { useTheme } from "next-themes";
import { useEffect, useState } from "react";

export default function GiscusNews() {
  const { theme, systemTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  const currentTheme = theme === "system" ? systemTheme : theme;
  const giscusTheme = currentTheme === "dark" ? "dark" : "light";

  if (!mounted) {
    return (
      <div className="animate-pulse h-48 rounded-xl bg-green-100 dark:bg-neutral-700" />
    );
  }

  return (
    <Giscus
      id="news-comments"
      repo="jvondermarck/dinosaur-exploder"
      repoId="R_kgDOG5cCmg"
      category="Reviews"
      categoryId="DIC_kwDOG5cCms4C1MYy"
      mapping="pathname"
      reactionsEnabled="1"
      emitMetadata="0"
      inputPosition="top"
      theme={giscusTheme}
      lang="en"
      loading="lazy"
    />
  );
}
