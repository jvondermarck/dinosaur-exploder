/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen, waitFor } from "@testing-library/react";
import CreditsPage from "../app/[lang]/credits/page";

// Mock next/image for Jest (no `any`)
jest.mock("next/image", () => ({
  __esModule: true,
  default: (props: { alt: string; src: string }) => {
    // eslint-disable-next-line @next/next/no-img-element
    return <img alt={props.alt} src={props.src} />;
  },
}));

type Contributor = {
  id: number;
  login: string;
  avatar_url: string;
  html_url: string;
  contributions: number;
};

type FetchLikeResponse = {
  ok: boolean;
  json: () => Promise<unknown>;
};

// Minimal fetch mock type (no `any`)
type FetchMock = (input: RequestInfo | URL, init?: RequestInit) => Promise<FetchLikeResponse>;

describe("CreditsPage (server component)", () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  it("renders credits heading (when API succeeds)", async () => {
    const mockFetch: FetchMock = async () => ({
      ok: true,
      json: async () =>
        [
          {
            id: 1,
            login: "user1",
            avatar_url: "https://avatars.githubusercontent.com/u/1?v=4",
            html_url: "https://github.com/user1",
            contributions: 5,
          },
        ] satisfies Contributor[],
    });

    // Assign mock to global.fetch safely
    global.fetch = mockFetch as unknown as typeof fetch;

    const ui = await CreditsPage();
    render(ui);

    expect(screen.getByRole("heading", { name: /credits/i })).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText(/user1/i)).toBeInTheDocument();
      expect(screen.getByText(/5 contributions/i)).toBeInTheDocument();
    });
  });

  it("shows error message when API fails / returns empty", async () => {
    const mockFetch: FetchMock = async () => ({
      ok: false,
      json: async () => [],
    });

    global.fetch = mockFetch as unknown as typeof fetch;

    const ui = await CreditsPage();
    render(ui);

    expect(
      screen.getByText(/couldn't load contributors right now/i)
    ).toBeInTheDocument();
  });
});
