/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

"use client";

import { useState, useEffect } from "react";

export default function ReviewsStarRating() {
  const [count, setCount] = useState(0);
  const [hasClicked, setHasClicked] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("/api/reviews-star");
        const data = await response.json();
        setCount(data.count || 0);
        setHasClicked(data.userVoted || false);
      } catch (error) {
        console.error("Failed to load star count:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleClick = async () => {
    if (hasClicked) return;

    try {
      const response = await fetch("/api/reviews-star", {
        method: "POST",
      });

      if (response.ok) {
        const data = await response.json();
        setCount(data.count);
        setHasClicked(true);
      } else {
        const error = await response.json();
        console.error("Vote failed:", error.error);
      }
    } catch (error) {
      console.error("Failed to update star count:", error);
    }
  };

  if (loading) {
    return (
      <div className="inline-flex items-center gap-2">
        <div className="w-6 h-6 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
        <span className="text-green-800 dark:text-green-200 font-mono">0</span>
      </div>
    );
  }

  return (
    <button
      onClick={handleClick}
      disabled={hasClicked}
      className={`inline-flex items-center gap-2 transition-all ${
        hasClicked
          ? "cursor-not-allowed opacity-70"
          : "cursor-pointer hover:scale-110"
      }`}
      title={hasClicked ? "You already voted!" : "Click to rate!"}
    >
      <span className="text-2xl" role="img" aria-label="star">
        ⭐
      </span>
      <span className="text-green-800 dark:text-green-200 font-mono font-bold">
        {count}
      </span>
    </button>
  );
}
