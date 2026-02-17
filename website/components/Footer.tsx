'use client';


/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

export default function Footer({text}: {text: string}) {
  return (
    <footer className="text-center text-green-900 text-xs font-mono opacity-70 py-6 mt-auto">
      © 2022-{new Date().getFullYear()} {text}
    </footer>
  );
}