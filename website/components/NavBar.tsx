"use client";


/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";
import { RxHamburgerMenu } from "react-icons/rx";
import { MdClose } from "react-icons/md";

export default function NavBar({ lang, dict }: { lang: string; dict: any }) {
  const [isOpen, setIsOpen] = useState(false);
  const pathname = usePathname();
  const navItems = [
    { href: "/how-game-works", label: dict.NavBar.howGameWorks },
    { href: "/news", label: dict.NavBar.news },
    { href: "/credits", label: dict.NavBar.credits },
    { href: "/changelog", label: "Changelog" },
    { href: "/contact", label: dict.NavBar.contact },
    { href: "/reviews", label: dict.NavBar.reviews },
  ];

  return (
    <nav className="w-full px-4 sm:px-8 py-4 bg-transparent">
      <div className="flex flex-row justify-between items-center gap-6">
        {/* Logo — left aligned */}
        <Link
          href={`/${lang}`}
          className="font-retro text-green-700 dark:text-green-400 text-3xl drop-shadow-sm tracking-wide text-left hover:opacity-90 flex-none leading-tight"
        >
          DINOSAUR<br />EXPLODER
        </Link>

        {/* Navigation — right aligned */}
        <div className="font-retro flex flex-row gap-2 items-center">

          {/* Internal links (hidden below lg) */}
          <div className="hidden lg:flex flex-row gap-2">
            {navItems.map((item) => {
              const href = `/${lang}${item.href}`;
              const isActive = pathname === href;
              const isHowGameWorks = item.href === "/how-game-works";

              const activeClass = isHowGameWorks
                ? "border-orange-600 dark:border-orange-400 bg-orange-600 dark:bg-orange-500 text-white shadow-none translate-x-[3px] translate-y-[3px]"
                : "border-green-800 dark:border-green-500 bg-green-800 dark:bg-green-600 text-white shadow-none translate-x-[3px] translate-y-[3px]";

              const inactiveClass = isHowGameWorks
                ? "border-orange-500 dark:border-orange-400 text-orange-700 dark:text-orange-300 bg-white dark:bg-neutral-800 hover:bg-orange-500 dark:hover:bg-orange-500 hover:text-white shadow-[3px_3px_0px_0px_theme(colors.orange.500)] dark:shadow-[3px_3px_0px_0px_theme(colors.orange.400)] hover:border-orange-500 dark:hover:border-orange-400 ring-1 ring-orange-400/40 dark:ring-orange-400/30"
                : "border-green-700 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 hover:bg-green-800 dark:hover:bg-green-600 hover:text-white shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:shadow-[3px_3px_0px_0px_theme(colors.green.500)] hover:border-green-800 dark:hover:border-green-500";

              return (
                <Link
                  key={item.href}
                  href={href}
                  className={
                    "px-4 py-2.25 whitespace-nowrap rounded border font-semibold font-mono transition-all duration-150 text-center flex transform " +
                    (isActive ? activeClass : inactiveClass)
                  }
                >
                  {item.label}
                </Link>
              );
            })}
          </div>

          {/* Small Screen / Mobile Toggle Button */}
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="lg:hidden p-2 border-2 border-green-800 dark:border-green-500 rounded shadow-[2px_2px_0px_0px_theme(colors.green.700)] dark:shadow-[2px_2px_0px_0px_theme(colors.green.500)] active:shadow-none active:translate-x-[2px] active:translate-y-[2px] text-green-800 dark:text-green-400 cursor-pointer bg-transparent dark:bg-transparent"
          >
            {isOpen ? <MdClose size={20} /> : <RxHamburgerMenu size={20} />}
          </button>
        </div>

      </div>


      {/* +++++ Hidden menu items +++++ */}
      
      {isOpen && (
        <div className="mx-auto md:absolute md:right-8 z-50 md:ml-auto lg:hidden w-fit flex flex-col gap-4 mt-4 p-4 md:border-2 md:border-green-800 dark:md:border-green-500 md:bg-white dark:md:bg-neutral-800 md:shadow-[3px_3px_0px_0px_theme(colors.green.700)] dark:md:shadow-[3px_3px_0px_0px_theme(colors.green.500)] rounded">

          {/* Internal links */}
          <div className="flex flex-col sm:flex-row md:flex-col gap-2">
            {navItems.map((item) => {
              const isHowGameWorks = item.href === "/how-game-works";
              return (
                <Link
                  key={item.href}
                  href={`/${lang}${item.href}`}
                  onClick={() => setIsOpen(false)}
                  className={`px-4 py-2 whitespace-nowrap rounded border font-semibold font-mono transition-all duration-150 text-center flex items-center gap-2 text-sm justify-center ${
                    isHowGameWorks
                      ? "border-orange-500 dark:border-orange-400 text-orange-700 dark:text-orange-300 bg-white dark:bg-neutral-800 shadow-[2px_2px_0px_0px_theme(colors.orange.500)] dark:shadow-[2px_2px_0px_0px_theme(colors.orange.400)]"
                      : "border-green-700 dark:border-green-500 text-green-800 dark:text-green-300 bg-white dark:bg-neutral-800 shadow-[2px_2px_0px_0px_theme(colors.green.700)] dark:shadow-[2px_2px_0px_0px_theme(colors.green.500)]"
                  }`}
                >
                  {item.label}
                </Link>
              );
            })}
          </div>
        </div>
      )}
    </nav>
  );
}