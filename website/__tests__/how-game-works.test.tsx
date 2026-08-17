/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen } from "@testing-library/react";
import HowGameWorksPage from "../app/[lang]/how-game-works/page";

describe("HowGameWorksPage", () => {
  it("renders the main heading", async () => {
    render(await HowGameWorksPage({ params: Promise.resolve({ lang: "en" }) }));
    expect(
      screen.getByRole("heading", { name: /how the game works/i })
    ).toBeInTheDocument();
  });

  it("renders the key sections", async () => {
    render(await HowGameWorksPage({ params: Promise.resolve({ lang: "en" }) }));
    expect(screen.getByRole("heading", { name: /goal/i })).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /gameplay loop/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /controls/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /gameplay demo/i })
    ).toBeInTheDocument();
  });

  it("renders the gameplay video with correct source", async () => {
    const { container } = render(
      await HowGameWorksPage({ params: Promise.resolve({ lang: "en" }) })
    );
    const video = container.querySelector("video");
    expect(video).toBeInTheDocument();

    const source = container.querySelector("video source");
    expect(source).toBeInTheDocument();
    expect(source).toHaveAttribute(
      "src",
      "https://github.com/user-attachments/assets/4b5a6ed4-2e68-4e12-a9c8-8a6c33178c5e"
    );
  });

  it("shows controls for Space and Esc (exact match)", async () => {
    render(await HowGameWorksPage({ params: Promise.resolve({ lang: "en" }) }));
    expect(screen.getByText(/^Space$/)).toBeInTheDocument();
    expect(screen.getByText(/^Esc$/)).toBeInTheDocument();
  });
});
