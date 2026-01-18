"use client";

import Link from "next/link";
import { usePathname, useParams } from "next/navigation";

export default function NavBar({lang, dict}: {lang: string; dict: any}) {
  const pathname = usePathname();
  const navItems = [
    { href: "/how-game-works", label: dict.NavBar.howGameWorks},
    { href: "/credits", label: dict.NavBar.credits },
    { href: "/contact", label: dict.NavBar.contact },
  ];

  return (
    <nav className="w-full px-2 sm:px-6 py-4 bg-transparent">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 sm:gap-0">
        {/* Title */}
        <Link
          href={`/${lang}`}
          className="font-retro text-green-700 text-2xl drop-shadow-sm tracking-wide text-center sm:text-left hover:opacity-90"
        >
          DINOSAUR EXPLODER
        </Link>

        {/* Navigation */}
        <div className="flex flex-col sm:flex-row gap-2 sm:items-center justify-center sm:justify-end">

          {/* Internal links */}
          <div className="flex flex-col xs:flex-row gap-2 justify-center">
            {navItems.map((item) => {
              const localisedHref = `/${lang}${item.href}`;
              const isActive = pathname === localisedHref;

              return (
                <Link
                  key={item.href}
                  href={localisedHref}
                  className={
                    "px-4 py-2 w-full xs:w-auto rounded border font-semibold font-mono transition duration-150 text-center " +
                    (isActive
                      ? "border-green-800 bg-green-700 text-white"
                      : "border-green-700 text-green-800 bg-white hover:bg-green-700 hover:text-white")
                  }
                >
                  {item.label}
                </Link>
              );
            })}
          </div>

          {/* External links */}
          <div className="flex flex-col xs:flex-row gap-2 justify-center">
            <a
              href="https://github.com/jvondermarck/dinosaur-exploder"
              target="_blank"
              rel="noopener noreferrer"
              className="px-4 py-2 w-full xs:w-auto rounded border border-green-700 text-green-800 bg-white hover:bg-green-700 hover:text-white font-semibold font-mono transition duration-150 text-center"
            >
              GitHub
            </a>

            <a
              href="https://github.com/jvondermarck/dinosaur-exploder/stargazers"
              target="_blank"
              rel="noopener noreferrer"
              className="px-4 py-2 w-full xs:w-auto rounded border border-yellow-500 text-yellow-700 bg-white hover:bg-yellow-500 hover:text-white font-semibold font-mono transition duration-150 flex items-center gap-2 justify-center"
            >
              <svg
                width="18"
                height="18"
                viewBox="0 0 16 16"
                fill="currentColor"
                aria-hidden="true"
              >
                <path d="M8 .25a.75.75 0 0 1 .673.418l1.882 3.815 4.21.612a.75.75 0 0 1 .416 1.279l-3.046 2.97.719 4.192a.751.751 0 0 1-1.088.791L8 12.347l-3.766 1.98a.75.75 0 0 1-1.088-.79l.72-4.194L.818 6.374a.75.75 0 0 1 .416-1.28l4.21-.611L7.327.668A.75.75 0 0 1 8 .25Z" />
              </svg>
              Star
            </a>

            <a
              href="https://github.com/sponsors/jvondermarck"
              target="_blank"
              rel="noopener noreferrer"
              className="px-4 py-2 w-full xs:w-auto rounded border border-pink-700 text-white bg-pink-600 hover:bg-pink-700 font-semibold font-mono transition duration-150 flex items-center gap-2 justify-center"
            >
              <svg
                width="18"
                height="18"
                viewBox="0 0 16 16"
                fill="currentColor"
                aria-hidden="true"
              >
                <path d="M8 15C-7.333 4.868 3.278-3.04 7.824 1.143c.06.055.119.112.176.171a3.12 3.12 0 0 1 .176-.17C12.723-3.042 23.333 4.868 8 15z" />
              </svg>
              Sponsor
            </a>
          </div>
        </div>
      </div>
    </nav>
  );
}
