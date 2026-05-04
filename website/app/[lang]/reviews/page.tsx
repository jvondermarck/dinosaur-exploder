/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import { Locale } from "../../../i18n-config";
import GiscusComponent from "@/components/GiscusComponent";

export const metadata: Metadata = {
  title: "Reviews · Dinosaur Exploder",
  description: "Community reviews and player feedback for Dinosaur Exploder.",
};

export default async function ReviewsPage({
  params,
}: {
  params: Promise<{ lang: string }>;
}) {
  const { lang } = await params;
  const dict = await getDictionary(lang as Locale);

  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-6">
        {dict.reviews.title}
      </h1>
      <p className="font-mono text-green-800 dark:text-green-100 leading-relaxed mb-6">
        {dict.reviews.description}
      </p>

      <GiscusComponent lang={lang} />
    </div>
  );
}

