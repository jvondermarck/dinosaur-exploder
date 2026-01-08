import { render, screen } from "@testing-library/react";
import ContactPage from "../app/contact/page";

describe("ContactPage", () => {
  it("renders the Contact heading", () => {
    render(<ContactPage />);
    expect(screen.getByRole("heading", { name: /contact/i })).toBeInTheDocument();
  });

  it("contains the main community links", () => {
    render(<ContactPage />);

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

  it("renders the Contribute section", () => {
    render(<ContactPage />);
    expect(
      screen.getByRole("heading", { name: /contribute/i })
    ).toBeInTheDocument();
    expect(screen.getByText(/\/website/i)).toBeInTheDocument();
  });
});
