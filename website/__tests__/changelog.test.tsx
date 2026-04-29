/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen, waitFor } from "@testing-library/react";
import ChangelogPage from "../app/[lang]/changelog/page";
import ReleaseBody from "../components/changelog/ReleaseBody";

type Release = {
  id: number;
  tag_name: string;
  name: string;
  body: string;
  html_url: string;
  published_at: string;
};

type FetchLikeResponse = {
  ok: boolean;
  json: () => Promise<unknown>;
};

// Minimal fetch mock type (no `any`)
type FetchMock = (input: RequestInfo | URL, init?: RequestInit) => Promise<FetchLikeResponse>;

const MOCK_RELEASE: Release = {
  id: 1,
  tag_name: "v1.0.0",
  name: "First Release",
  body: [
    "## What's Changed",
    "* feat: add dino shooting by @alice in https://github.com/jvondermarck/dinosaur-exploder/pull/42",
    "* fix: game crash on start by @bob in https://github.com/jvondermarck/dinosaur-exploder/pull/43",
    "",
    "**Full Changelog**: https://github.com/jvondermarck/dinosaur-exploder/compare/v0.9.0...v1.0.0",
  ].join("\n"),
  html_url: "https://github.com/jvondermarck/dinosaur-exploder/releases/tag/v1.0.0",
  published_at: "2026-01-01T00:00:00Z",
};

describe("ChangelogPage (server component)", () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  it("renders changelog heading when API succeeds", async () => {
    const mockFetch: FetchMock = async () => ({
      ok: true,
      json: async () => [MOCK_RELEASE] satisfies Release[],
    });

    global.fetch = mockFetch as unknown as typeof fetch;

    const ui = await ChangelogPage();
    render(ui);

    expect(screen.getByRole("heading", { name: /changelog/i })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText("v1.0.0")).toBeInTheDocument();
      expect(screen.getByText(/First Release/)).toBeInTheDocument();
    });
  });

  it("shows error message when API fails", async () => {
    const mockFetch: FetchMock = async () => ({
      ok: false,
      json: async () => [],
    });

    global.fetch = mockFetch as unknown as typeof fetch;

    const ui = await ChangelogPage();
    render(ui);

    expect(
      screen.getByText(/couldn't load releases right now/i)
    ).toBeInTheDocument();
  });

  it("shows 'No description provided' for a release with no body", async () => {
    const emptyRelease: Release = { ...MOCK_RELEASE, body: "" };
    const mockFetch: FetchMock = async () => ({
      ok: true,
      json: async () => [emptyRelease] satisfies Release[],
    });

    global.fetch = mockFetch as unknown as typeof fetch;

    const ui = await ChangelogPage();
    render(ui);

    expect(screen.getByText(/no description provided/i)).toBeInTheDocument();
  });
});

describe("ReleaseBody", () => {
  it("renders a section header from ## markdown", () => {
    render(<ReleaseBody body="## What's Changed" />);
    expect(screen.getByText(/what's changed/i)).toBeInTheDocument();
  });

  it("renders a feat commit badge", () => {
    render(<ReleaseBody body="* feat: add dino shooting" />);
    expect(screen.getByText("feat")).toBeInTheDocument();
    expect(screen.getByText(/add dino shooting/i)).toBeInTheDocument();
  });

  it("renders a fix commit badge", () => {
    render(<ReleaseBody body="* fix: game crash on start" />);
    expect(screen.getByText("fix")).toBeInTheDocument();
    expect(screen.getByText(/game crash on start/i)).toBeInTheDocument();
  });

  it("renders a breaking change badge with !", () => {
    render(<ReleaseBody body="* feat!: drop Node 16 support" />);
    expect(screen.getByText("feat!")).toBeInTheDocument();
  });

  it("renders a scoped commit badge", () => {
    render(<ReleaseBody body="* fix(ui): fix button alignment" />);
    expect(screen.getByText("fix(ui)")).toBeInTheDocument();
  });

  it("renders a PR URL as a clean #N link", () => {
    render(
      <ReleaseBody body="* feat: new dino in https://github.com/jvondermarck/dinosaur-exploder/pull/42" />
    );
    const prLink = screen.getByRole("link", { name: "#42" });
    expect(prLink).toBeInTheDocument();
    expect(prLink).toHaveAttribute(
      "href",
      "https://github.com/jvondermarck/dinosaur-exploder/pull/42"
    );
  });

  it("renders @mention as a GitHub profile link", () => {
    render(
      <ReleaseBody body="* feat: new dino by @alice in https://github.com/jvondermarck/dinosaur-exploder/pull/1" />
    );
    const mentionLink = screen.getByRole("link", { name: "@alice" });
    expect(mentionLink).toBeInTheDocument();
    expect(mentionLink).toHaveAttribute("href", "https://github.com/alice");
  });

  it("renders the Full Changelog link shortened to version range", () => {
    render(
      <ReleaseBody body="**Full Changelog**: https://github.com/jvondermarck/dinosaur-exploder/compare/v0.9.0...v1.0.0" />
    );
    const link = screen.getByRole("link", { name: "v0.9.0...v1.0.0" });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute(
      "href",
      "https://github.com/jvondermarck/dinosaur-exploder/compare/v0.9.0...v1.0.0"
    );
  });

  it("renders plain text without a badge", () => {
    render(<ReleaseBody body="This release includes many improvements." />);
    expect(
      screen.getByText(/this release includes many improvements/i)
    ).toBeInTheDocument();
  });
});
