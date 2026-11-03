/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { getSiteMetadata, SITE_NAME } from "../lib/siteMetadata";

describe("getSiteMetadata", () => {
  it("sets the site title and a description", () => {
    const metadata = getSiteMetadata();
    expect(metadata.title).toBe(SITE_NAME);
    expect(metadata.description).toMatch(/Dinosaur Exploder/);
  });

  it("includes Open Graph and Twitter card metadata", () => {
    const metadata = getSiteMetadata();
    expect(metadata.openGraph).toMatchObject({ type: "website", siteName: SITE_NAME });
    expect(metadata.twitter).toMatchObject({ card: "summary", title: SITE_NAME });
  });

  it("keeps the Google site verification", () => {
    expect(getSiteMetadata().verification?.google).toBeTruthy();
  });

  it("uses NEXT_PUBLIC_SITE_URL as metadataBase when set", () => {
    process.env.NEXT_PUBLIC_SITE_URL = "https://example.com";
    expect(String(getSiteMetadata().metadataBase)).toBe("https://example.com/");
    delete process.env.NEXT_PUBLIC_SITE_URL;
  });

  it("falls back to the default site URL", () => {
    delete process.env.NEXT_PUBLIC_SITE_URL;
    expect(String(getSiteMetadata().metadataBase)).toBe("https://dinosaur-exploder.vercel.app/");
  });
});
