/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import GiscusComponent from "@/components/GiscusComponent";
import ReviewsStarRating from "@/components/ReviewsStarRating";

export const metadata: Metadata = {
  title: "Reviews · Dinosaur Exploder",
  description: "Community reviews and player feedback for Dinosaur Exploder.",
};

export default async function ReviewsPage() {

  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <div className="flex items-center justify-between">
        <h1 className="font-retro text-3xl md:text-4xl text-green-800 dark:text-green-200 mb-6">
          Reviews
        </h1>
        <div className="mb-6">
          <ReviewsStarRating />
        </div>
      </div>
      <p className="font-mono text-green-800 dark:text-green-100 leading-relaxed mb-6">
        Leave a review about the game or website
      </p>

      <GiscusComponent />
    </div>
  );
}
