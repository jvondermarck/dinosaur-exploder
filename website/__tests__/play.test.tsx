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
  it("renders the browser-play heading and game shell", async () => {
    const ui = await PlayPage({ params: Promise.resolve({ lang: "en" }) });
    render(ui);

    expect(
      screen.getByRole("heading", { name: /play online/i })
    ).toBeInTheDocument();
    expect(screen.getByTestId("arcade-game")).toBeInTheDocument();
  });
});
