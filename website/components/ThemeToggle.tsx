"use client";

/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { useTheme } from "next-themes";
import { useState, useEffect } from "react";

type Theme = "light" | "dark" | "system";

const themeLabels: Record<Theme, string> = {
  light: "Light",
  dark: "Dark",
  system: "System",
};

export default function ThemeToggle() {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <div className="bg-white border-2 border-green-700 text-green-700 px-3 py-1 font-mono text-xs font-bold uppercase w-24 h-8 animate-pulse rounded" />
    );
  }

  const [isOpen, setIsOpen] = useState(false);
  const current = (theme ?? "system") as Theme;

  return (
    <div className="relative inline-block text-left">
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="bg-white border-2 border-green-700 text-green-700 px-3 py-1 font-mono text-xs font-bold uppercase hover:bg-green-700 hover:text-white transition-all shadow-[2px_2px_0px_0px_rgba(21,128,61,1)] active:shadow-none active:translate-x-[1px] active:translate-y-[1px] dark:bg-neutral-800 dark:border-green-500 dark:text-green-400 dark:hover:bg-green-600 dark:hover:text-white"
        title={`Theme: ${themeLabels[current]}`}
      >
        {themeLabels[current]} {isOpen ? "▲" : "▼"}
      </button>
      {isOpen && (
        <div className="absolute right-0 mt-2 w-28 bg-white border-2 border-green-700 shadow-[4px_4px_0px_0px_rgba(21,128,61,1)] rounded z-[100] dark:bg-neutral-800 dark:border-green-500">
          {(["light", "dark", "system"] as const).map((t) => (
            <button
              key={t}
              type="button"
              onClick={() => {
                setTheme(t);
                setIsOpen(false);
              }}
              className={`block w-full text-left px-4 py-2 text-xs font-mono font-bold uppercase transition-colors ${
                current === t
                  ? "bg-green-100 text-green-800 dark:bg-green-900/50 dark:text-green-300"
                  : "text-green-700 hover:bg-green-700 hover:text-white dark:text-green-400 dark:hover:bg-green-600 dark:hover:text-white"
              }`}
            >
              {themeLabels[t]}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
