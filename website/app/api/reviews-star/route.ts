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
  const ip = forwardedFor ? forwardedFor.split(",")[0].trim() : "unknown";
  return ip;
}

export async function GET(request: NextRequest) {
  try {
    const count = (await kv.get<number>(REVIEWS_COUNT_KEY)) || 0;
    const voterIP = getVoterIP(request);
    const voters = (await kv.get<string[]>(REVIEWS_VOTERS_KEY)) || [];
    const userVoted = voters.includes(voterIP);

    return NextResponse.json({
      count: count,
      userVoted: userVoted,
    });
  } catch (error) {
    console.error("Failed to read reviews data:", error);
    return NextResponse.json({ count: 0, userVoted: false });
  }
}

export async function POST(request: NextRequest) {
  try {
    const voterIP = getVoterIP(request);
    const voters = (await kv.get<string[]>(REVIEWS_VOTERS_KEY)) || [];

    if (voters.includes(voterIP)) {
      return NextResponse.json(
        { error: "You already voted" },
        { status: 400 }
      );
    }

    const currentCount = (await kv.get<number>(REVIEWS_COUNT_KEY)) || 0;
    const newCount = currentCount + 1;
    voters.push(voterIP);

    await kv.set(REVIEWS_COUNT_KEY, newCount);
    await kv.set(REVIEWS_VOTERS_KEY, voters);

    return NextResponse.json({
      count: newCount,
      success: true,
    });
  } catch (error) {
    console.error("Failed to update reviews data:", error);
    return NextResponse.json(
      { error: "Failed to update count" },
      { status: 500 }
    );
  }
}
