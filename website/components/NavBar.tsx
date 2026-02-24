"use client";


/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import Link from "next/link";
import { usePathname, useParams } from "next/navigation";
import { useState } from "react";
import { FaGithub } from "react-icons/fa";
import { RxHamburgerMenu } from "react-icons/rx";
import { MdClose } from "react-icons/md";

export default function NavBar({ lang, dict }: { lang: string; dict: any }) {
  const [isOpen, setIsOpen] = useState(false);
  const pathname = usePathname();
  const navItems = [
    { href: "/how-game-works", label: dict.NavBar.howGameWorks },
    { href: "/credits", label: dict.NavBar.credits },
    { href: "/contact", label: dict.NavBar.contact },
  ];
  const buttonStyle = "px-4 py-2 whitespace-nowrap rounded border font-semibold font-mono transition-all duration-150 text-center flex items-center gap-2 text-sm";


  return (
    <nav className="w-full px-2 sm:px-6 py-4 bg-transparent">
      <div className="flex flex-row sm:justify-between sm:items-center gap-3 sm:gap-0 items-center">
        {/* Title */}
        <Link
          href={`/${lang}`}
          className="font-retro text-green-700 dark:text-green-400 text-3xl drop-shadow-sm tracking-wide text-center sm:text-left hover:opacity-90 items-center justify-center"
        >
          DINOSAUR EXPLODER
        </Link>

        {/* Navigation */}
        <div className={`font-retro flex flex-col sm:flex-row gap-2 sm:items-center justify-between `}>

          {/* Internal links */}
          <div className={`hidden lg:flex  xs:flex-row gap-2 justify-center`}>
            {navItems.map((item) => {
              const localisedHref = `/${lang}${item.href}`;
              const isActive = pathname === localisedHref;

              return (
                <Link
                  key={item.href}
                  href={localisedHref}
                  className={
                    "px-4 py-2.25 whitespace-nowrap rounded border font-semibold font-mono transition-all duration-150 text-center flex transform " +
                    (isActive
                      ? "border-green-800 dark:border-green-500 bg-green-800 dark:bg-green-600 text-white shadow-none translate-x-[3px] translate-y-[3px]"
                      : "border-green-700 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 hover:bg-green-800 dark:hover:bg-green-600 hover:text-white shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:shadow-[3px_3px_0px_0px_theme(colors.green.500)] hover:border-green-800 dark:hover:border-green-500")
                  }
                >
                  {item.label}
                </Link>
              );
            })}
          </div>

          {/* External links */}
          <div className={`hidden xl:flex  xs:flex-row gap-2 justify-center`}>
            <a
              href="https://github.com/jvondermarck/dinosaur-exploder"
              target="_blank"
              rel="noopener noreferrer"
              className={`${buttonStyle} shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:shadow-[3px_3px_0px_0px_theme(colors.green.500)] border-green-800 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 hover:bg-green-800 dark:hover:bg-green-600 hover:text-white hover:border-green-800 dark:hover:border-green-500`}
            >
              <FaGithub size={25} />
              Github
            </a>

            <a
              href="https://github.com/jvondermarck/dinosaur-exploder/stargazers"
              target="_blank"
              rel="noopener noreferrer"
              className={`${buttonStyle} shadow-[3px_3px_0px_0px_theme(colors.yellow.400)] dark:shadow-[3px_3px_0px_0px_theme(colors.yellow.500)] border-yellow-500 dark:border-yellow-400 text-yellow-700 dark:text-yellow-300 bg-white dark:bg-neutral-800 hover:bg-yellow-500 dark:hover:bg-yellow-500 hover:text-white justify-center `}
            >
              <svg
                width="22"
                height="22"
                viewBox="0 0 16 16"
                fill="currentColor"
                aria-hidden="true"
              >
                <path d="M8 .25a.75.75 0 0 1 .673.418l1.882 3.815 4.21.612a.75.75 0 0 1 .416 1.279l-3.046 2.97.719 4.192a.751.751 0 0 1-1.088.791L8 12.347l-3.766 1.98a.75.75 0 0 1-1.088-.79l.72-4.194L.818 6.374a.75.75 0 0 1 .416-1.28l4.21-.611L7.327.668A.75.75 0 0 1 8 .25Z" />
              </svg>
              {dict.NavBar.star}
            </a>

            <a
              href="https://github.com/sponsors/jvondermarck"
              target="_blank"
              rel="noopener noreferrer"
              className={`${buttonStyle} shadow-[3px_3px_0px_0px_#A3245C] dark:shadow-[3px_3px_0px_0px_#db2777] border-pink-700 dark:border-pink-500 text-white bg-pink-600 dark:bg-pink-600 hover:bg-pink-700 dark:hover:bg-pink-700 justify-center`}
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
              {dict.NavBar.sponsor}
            </a>
          </div>

          {/* Small Screen / Mobile Toggle Button */}
          <button
            onClick={() => setIsOpen(!isOpen)}
            className=" xl:hidden p-2 border-2 border-green-800 dark:border-green-500 rounded shadow-[2px_2px_0px_0px_theme(colors.green.700)] dark:shadow-[2px_2px_0px_0px_theme(colors.green.500)] active:shadow-none active:translate-x-[2px] active:translate-y-[2px] text-green-800 dark:text-green-400 cursor-pointer bg-transparent dark:bg-transparent"
          >
            {isOpen ? <MdClose size={20} /> : <RxHamburgerMenu size={20} />}
          </button>
        </div>

      </div>


      {/* +++++ Hidden menu items +++++ */}
      
      {isOpen && (
        <div className="mx-auto md:absolute md:right-6 sm:right-6 z-50 md:ml-auto xl:hidden w-fit flex flex-col gap-4 mt-4 p-4 md:border-2 md:border-green-800 dark:md:border-green-500 md:bg-white dark:md:bg-neutral-800 md:shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:md:shadow-[3px_3px_0px_0px_theme(colors.green.500)] rounded">

          {/* Internal links */}
          <div className="lg:hidden flex flex-col sm:flex-row md:flex-col  gap-2">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={`/${lang}${item.href}`}
                onClick={() => setIsOpen(false)}
                className={`${buttonStyle} border-green-700 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 justify-center shadow-[2px_2px_0px_0px_theme(colors.green.700)] dark:shadow-[2px_2px_0px_0px_theme(colors.green.500)]`}
              >
                {item.label}
              </Link>
            ))}
          </div>

          <hr className="sm:hidden border-green-800/20 dark:border-green-500/30 w-full" />

          {/* External Links */}
          <div className="flex flex-col sm:flex-row md:flex-col gap-2">
            <a
              href="https://github.com/jvondermarck/dinosaur-exploder"
              target="_blank"
              rel="noopener noreferrer"
              className={`${buttonStyle} justify-center shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:shadow-[3px_3px_0px_0px_theme(colors.green.500)] border-green-800 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 hover:bg-green-800 dark:hover:bg-green-600 hover:text-white hover:border-green-800 dark:hover:border-green-500`}
            >
              <FaGithub size={25} />
              Github
            </a>


            <a
              href="https://github.com/jvondermarck/dinosaur-exploder/stargazers"
              target="_blank"
              rel="noopener noreferrer"
              className={`${buttonStyle} shadow-[3px_3px_0px_0px_theme(colors.yellow.400)] dark:shadow-[3px_3px_0px_0px_theme(colors.yellow.500)] border-yellow-500 dark:border-yellow-400 text-yellow-700 dark:text-yellow-300 bg-white dark:bg-neutral-800 hover:bg-yellow-500 hover:text-white justify-center `}
            >
              <svg
                width="22"
                height="22"
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
              className={`${buttonStyle} shadow-[3px_3px_0px_0px_#A3245C] dark:shadow-[3px_3px_0px_0px_#db2777] border-pink-700 dark:border-pink-500 text-white bg-pink-600 hover:bg-pink-700 justify-center`}
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
      )}
    </nav>
  );
}


