/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";

export const SITE_NAME = "Dinosaur Exploder";
export const SITE_DESCRIPTION =
  "Dinosaur Exploder is a free, open source retro shoot 'em up game. Pilot your spaceship, blast dinosaurs and play it right in your browser.";

export function getSiteUrl(): string {
  return process.env.NEXT_PUBLIC_SITE_URL || "https://dinosaur-exploder.vercel.app";
}

/**
 * Site-wide metadata (title, description, Open Graph, Twitter card, keywords)
 * so search engines and link previews can identify the website.
 */
export function getSiteMetadata(): Metadata {
  return {
    metadataBase: new URL(getSiteUrl()),
    applicationName: SITE_NAME,
    title: SITE_NAME,
    description: SITE_DESCRIPTION,
    keywords: [
      "Dinosaur Exploder",
      "open source game",
      "retro shoot 'em up",
      "space shooter",
      "JavaFX",
      "FXGL",
    ],
    openGraph: {
      type: "website",
      siteName: SITE_NAME,
      title: SITE_NAME,
      description: SITE_DESCRIPTION,
    },
    twitter: {
      card: "summary",
      title: SITE_NAME,
      description: SITE_DESCRIPTION,
    },
    verification: {
      google: "gdP0ZxOyLsUaoHTFpX-fgS6eCOdbAr1AhMnFgLj330g",
    },
  };
}
