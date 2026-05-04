"use client";

import Giscus from "@giscus/react";
import { useTheme } from "next-themes";
import { useEffect, useState } from "react";

// Map app language codes to Giscus supported languages
const languageMap: Record<string, string> = {
  en: "en",
  fr: "fr",
  es: "es",
  el: "en", // Greek not supported, fallback to English
  zh_cn: "zh-CN",
};

export default function GiscusComponent({ lang }: { lang: string }) {
  const { theme, systemTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  const currentTheme = theme === "system" ? systemTheme : theme;
  const giscusTheme = currentTheme === "dark" ? "dark" : "light";
  const giscusLang = languageMap[lang] || "en";

  if (!mounted) {
    return <div className="animate-pulse h-96 bg-gray-200 dark:bg-gray-700 rounded"></div>;
  }

  return (
    <Giscus
      id="comments"
      repo="jvondermarck/dinosaur-exploder"
      repoId="462881434"
      category="Reviews"
      categoryId="DIC_kwDOG0hP4s4CfBQO"
      mapping="pathname"
      reactionsEnabled="1"
      emitMetadata="0"
      inputPosition="top"
      theme={giscusTheme}
      lang={giscusLang}
      loading="lazy"
    />
  );
}