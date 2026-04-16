/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { render, screen } from "@testing-library/react";
import ContactPage from "../app/[lang]/contact/page";

describe("ContactPage", () => {
  async function renderPage() {
    const ui = await ContactPage({ params: Promise.resolve({ lang: "en" }) });
    render(ui);
  }

  it("renders the Contact heading", async () => {
    await renderPage();
    expect(screen.getByRole("heading", { name: /contact/i })).toBeInTheDocument();
  });

  it("contains the main community links", async () => {
    await renderPage();

    const links = screen.getAllByRole("link");
    const hrefs = links.map((a) => a.getAttribute("href") || "");

    expect(hrefs).toEqual(
      expect.arrayContaining([
        "https://github.com/jvondermarck/dinosaur-exploder/discussions",
        "https://discord.com/invite/nkmCRnXbWm",
        "https://twitter.com/DinosaurExplod1",
        "https://github.com/sponsors/jvondermarck",
      ])
    );
  });

  it("renders the Contribute and review sections", async () => {
    await renderPage();
    expect(
      screen.getByRole("heading", { name: /leave a review/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole("heading", { name: /contribute/i })
    ).toBeInTheDocument();
    expect(screen.getByText(/\/website/i)).toBeInTheDocument();
  });
});
