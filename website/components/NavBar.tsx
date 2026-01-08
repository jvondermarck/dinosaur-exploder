'use client';

import Link from "next/link";
import { usePathname } from "next/navigation";

const navItems = [
  { href: "/how-game-works", label: "How game works" },
  { href: "/credits", label: "Credits" },
  { href: "/contact", label: "Contact" },
];

export default function NavBar() {
  const pathname = usePathname();
  return (
    <nav className="w-full px-2 sm:px-6 py-4 bg-transparent">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 sm:gap-0">
        {/* Title */}
        <Link
          href="/"
          className="font-retro text-green-700 text-2xl drop-shadow-sm tracking-wide text-center sm:text-left hover:opacity-90"
        >
          DINOSAUR EXPLODER
        </Link>

        {/* Navigation */}
        <div className="flex flex-col sm:flex-row gap-2 sm:items-center justify-center sm:justify-end">

          {/* Internal links */}
          <div className="flex flex-col xs:flex-row gap-2 justify-center">
            {navItems.map((item) => {
              const isActive = pathname === item.href;

              return (
                <Link
                  key={item.href}
                  href={item.href}
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