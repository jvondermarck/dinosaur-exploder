/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { NextRequest, NextResponse } from "next/server";
import fs from "fs/promises";
import path from "path";

const DATA_FILE = path.join(process.cwd(), ".data", "reviews-stars.json");

interface ReviewsData {
  count: number;
  voters: string[];
}

async function ensureDataFile() {
  try {
    await fs.access(DATA_FILE);
  } catch {
    const dir = path.dirname(DATA_FILE);
    await fs.mkdir(dir, { recursive: true });
    await fs.writeFile(DATA_FILE, JSON.stringify({ count: 0, voters: [] }));
  }
}

function getVoterIP(request: NextRequest): string {
  const forwardedFor = request.headers.get("x-forwarded-for");
  const ip = forwardedFor ? forwardedFor.split(",")[0].trim() : "unknown";
  return ip;
}

export async function GET(request: NextRequest) {
  try {
    await ensureDataFile();
    const data = await fs.readFile(DATA_FILE, "utf-8");
    const reviews: ReviewsData = JSON.parse(data);
    const voterIP = getVoterIP(request);
    const userVoted = reviews.voters.includes(voterIP);

    return NextResponse.json({
      count: reviews.count,
      userVoted: userVoted,
    });
  } catch (error) {
    console.error("Failed to read reviews data:", error);
    return NextResponse.json({ count: 0, userVoted: false });
  }
}

export async function POST(request: NextRequest) {
  try {
    await ensureDataFile();
    const data = await fs.readFile(DATA_FILE, "utf-8");
    const reviews: ReviewsData = JSON.parse(data);
    const voterIP = getVoterIP(request);

    if (reviews.voters.includes(voterIP)) {
      return NextResponse.json(
        { error: "You already voted" },
        { status: 400 }
      );
    }

    reviews.count += 1;
    reviews.voters.push(voterIP);

    await fs.writeFile(DATA_FILE, JSON.stringify(reviews, null, 2));

    return NextResponse.json({
      count: reviews.count,
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
