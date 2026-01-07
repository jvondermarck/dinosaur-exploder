import type { Metadata } from "next";
import Image from "next/image";

type Contributor = {
  id: number;
  login: string;
  avatar_url: string;
  html_url: string;
  contributions: number;
};

export const metadata: Metadata = {
  title: "Credits · Dinosaur Exploder",
  description: "A big thank-you to everyone who contributed to Dinosaur Exploder.",
};

async function getContributors(): Promise<Contributor[]> {
  const res = await fetch(
    "https://api.github.com/repos/jvondermarck/dinosaur-exploder/contributors?per_page=100",
    {
      next: { revalidate: 3600 },
      headers: {
        "User-Agent": "dinosaur-exploder-website",
        Accept: "application/vnd.github+json",
      },
    }
  );

  if (!res.ok) return [];
  return res.json();
}

export default async function CreditsPage() {
  const contributors = await getContributors();

  return (
    <div className="max-w-5xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 mb-3">
        Credits
      </h1>
      <p className="font-mono text-green-950 mb-8">
        This project exists thanks to the community. Here are the GitHub contributors ❤️
      </p>

      {contributors.length === 0 ? (
        <div className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <p className="font-mono text-green-950">
            Couldn&apos;t load contributors right now (GitHub API limit or network). Please try again later.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {contributors.map((c) => (
            <a
              key={c.id}
              href={c.html_url}
              target="_blank"
              rel="noopener noreferrer"
              className="bg-white/80 rounded-xl border border-green-200 p-4 shadow-sm hover:shadow-md transition"
            >
              <div className="flex items-center gap-3">
                <Image
                  src={c.avatar_url}
                  alt={c.login}
                  width={48}
                  height={48}
                  className="rounded-full border border-green-200"
                />
                <div className="min-w-0">
                  <div className="font-retro text-green-800 text-base truncate">
                    {c.login}
                  </div>
                  <div className="font-mono text-xs text-green-950 opacity-80">
                    {c.contributions} contribution{c.contributions === 1 ? "" : "s"}
                  </div>
                </div>
              </div>
            </a>
          ))}
        </div>
      )}

      <div className="mt-10 bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
        <h2 className="font-retro text-xl text-green-300 mb-2">Add your name here</h2>
        <p className="font-mono text-green-100">
          Want to appear on this list? Make a PR (game or website), and GitHub will automatically include you.
        </p>
      </div>
    </div>
  );
}
