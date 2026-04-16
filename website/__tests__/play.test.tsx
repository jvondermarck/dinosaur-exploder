/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen } from "@testing-library/react";
import PlayPage from "../app/[lang]/play/page";

jest.mock("../components/game/DinosaurArcadeGame", () => ({
  __esModule: true,
  default: () => <div data-testid="arcade-game" />,
}));

describe("PlayPage", () => {
  it("prioritizes the official game links and labels the browser version as a demo", async () => {
    const ui = await PlayPage({ params: Promise.resolve({ lang: "en" }) });
    render(ui);

    expect(
      screen.getByRole("heading", { name: /try the browser demo/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /download dinosaur exploder/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: /open github releases/i })
    ).toHaveAttribute(
      "href",
      "https://github.com/jvondermarck/dinosaur-exploder/releases"
    );
    expect(
      screen.getByRole("link", { name: /open readme install guide/i })
    ).toHaveAttribute(
      "href",
      "https://github.com/jvondermarck/dinosaur-exploder#java-installation"
    );
    expect(
      screen.getByText(/not the official dinosaur exploder game/i)
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /browser demo controls/i })
    ).toBeInTheDocument();
    expect(screen.getByTestId("arcade-game")).toBeInTheDocument();
  });
});
