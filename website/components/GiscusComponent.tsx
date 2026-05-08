"use client";

import Giscus from "@giscus/react";
import { useTheme } from "next-themes";
import { useEffect, useState } from "react";

export default function GiscusComponent() {
  const { theme, systemTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  const currentTheme = theme === "system" ? systemTheme : theme;
  const giscusTheme = currentTheme === "dark" ? "dark" : "light";

  if (!mounted) {
    return <div className="animate-pulse h-96 bg-gray-200 dark:bg-gray-700 rounded"></div>;
  }

  return (
    <Giscus
      id="comments"
      repo="jvondermarck/dinosaur-exploder"
      repoId="R_kgDOG5cCmg"
      category="Reviews"
      categoryId="DIC_kwDOG5cCms4C1MYy"
      mapping="specific"
      term="/reviews"
      reactionsEnabled="1"
      emitMetadata="0"
      inputPosition="top"
      theme={giscusTheme}
      lang="en"
      loading="lazy"
    />
  );
}