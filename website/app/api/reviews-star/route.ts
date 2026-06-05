/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { NextRequest, NextResponse } from "next/server";
import { kv } from "@vercel/kv";

const REVIEWS_COUNT_KEY = "reviews-stars-count";
const REVIEWS_VOTERS_KEY = "reviews-stars-voters";

function getVoterIP(request: NextRequest): string {
  const forwardedFor = request.headers.get("x-forwarded-for");

  if (!forwardedFor) {
    return "anonymous";
  }

  return forwardedFor.split(",")[0].trim();
}

export async function GET(request: NextRequest) {
  try {
    const voterIP = getVoterIP(request);

    const [count, userVoted] = await Promise.all([
      kv.get<number>(REVIEWS_COUNT_KEY),
      kv.sismember(REVIEWS_VOTERS_KEY, voterIP),
    ]);

    return NextResponse.json({
      count: count || 0,
      userVoted: Boolean(userVoted),
    });
  } catch (error) {
    console.error("Failed to fetch reviews data:", error);

    return NextResponse.json(
      {
        error: "Failed to fetch reviews data",
        count: 0,
        userVoted: false,
      },
      { status: 500 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const voterIP = getVoterIP(request);

    const alreadyVoted = await kv.sismember(
      REVIEWS_VOTERS_KEY,
      voterIP
    );

    if (alreadyVoted) {
      return NextResponse.json(
        { error: "You already voted" },
        { status: 400 }
      );
    }

    await kv.sadd(REVIEWS_VOTERS_KEY, voterIP);

    const newCount = await kv.incr(REVIEWS_COUNT_KEY);

    return NextResponse.json({
      success: true,
      count: newCount,
    });
  } catch (error) {
    console.error("Failed to update reviews data:", error);

    return NextResponse.json(
      { error: "Failed to update reviews count" },
      { status: 500 }
    );
  }
}
