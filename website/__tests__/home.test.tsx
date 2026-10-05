/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen } from "@testing-library/react";
import Home from "../app/[lang]/page";

describe("Home", () => {
  it("renders the English sponsor heading and description", async () => {
    render(await Home({ params: Promise.resolve({ lang: "en" }) }));

    expect(
      screen.getByRole("heading", {
        name: "Sponsor Julien Von Der Marck on GitHub Sponsors",
      })
    ).toBeInTheDocument();
    expect(
      screen.getByText(
        "Passionate open-source dev. I maintain Dinosaur Exploder, a free Java/FXGL shoot ’em up. Welcoming & guiding new contributors every day! 🦖✨"
      )
    ).toBeInTheDocument();
  });

  it("renders a safe external Sponsor link", async () => {
    render(await Home({ params: Promise.resolve({ lang: "en" }) }));

    const sponsorLink = screen.getByRole("link", { name: "Sponsor" });
    expect(sponsorLink).toHaveAttribute(
      "href",
      "https://github.com/sponsors/jvondermarck"
    );
    expect(sponsorLink).toHaveAttribute("target", "_blank");
    expect(sponsorLink).toHaveAttribute("rel", "noopener noreferrer");
  });

  it("does not render an iframe", async () => {
    const { container } = render(
      await Home({ params: Promise.resolve({ lang: "en" }) })
    );

    expect(container.querySelector("iframe")).not.toBeInTheDocument();
  });
});
