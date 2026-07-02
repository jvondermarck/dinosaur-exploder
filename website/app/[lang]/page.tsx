/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import { getDictionary } from "@/getDictionary";

export default async function Home({params}: {params: Promise<{lang: string}>}) {
  const {lang} = await params;
  const dict = await getDictionary(lang as any);

  return (
    <div className="flex flex-col">
      {/* Hero */}
      <section className="flex flex-col md:flex-row md:items-stretch gap-0 flex-1 max-w-6xl mx-auto w-full px-4 md:px-12 py-10 md:py-16">

        {/* Left — Gameplay video 9:16, height matches text column on desktop */}
        <div className="flex-1 flex justify-center items-center order-last md:order-first md:pr-6">
          <div
            className="w-[240px] md:w-auto md:h-full"
            style={{ aspectRatio: "9/16", maxHeight: "520px" }}
          >
            <video
              className="w-full h-full object-cover rounded-2xl border-2 border-green-700 dark:border-green-500 shadow-[4px_4px_0px_0px_theme(colors.green.700)] dark:shadow-[4px_4px_0px_0px_theme(colors.green.500)]"
              autoPlay
              loop
              muted
              playsInline
              controls
              preload="metadata"
            >
              <source
                src="https://github.com/user-attachments/assets/4b5a6ed4-2e68-4e12-a9c8-8a6c33178c5e"
                type="video/mp4"
              />
            </video>
          </div>
        </div>

        {/* Right — Text, equal half */}
        <div className="flex-1 flex flex-col justify-center items-center md:items-start text-center md:text-left order-first md:order-last md:pl-6">
          <h1 className="font-retro text-4xl lg:text-5xl font-extrabold text-green-800 dark:text-green-200 mb-4 leading-tight">
            Retro Arcade<br />
            <span className="text-black dark:text-white bg-green-200 dark:bg-green-700 px-2 rounded">Shoot &apos;em up</span>
          </h1>

          {/* Feature tags — más grandes */}
          <div className="flex flex-wrap gap-2.5 justify-center md:justify-start mb-5">
            {dict.features.map((f: { icon: string; title: string }, i: number) => (
              <span
                key={i}
                className="inline-flex items-center gap-2 font-mono text-sm font-bold uppercase tracking-wide px-4 py-1.5 rounded-full border-2 border-green-600 dark:border-green-500 text-green-800 dark:text-green-300 bg-green-50 dark:bg-green-900/30"
              >
                {f.icon} {f.title}
              </span>
            ))}
          </div>

          <p className="text-base mb-6 max-w-md text-green-900 dark:text-green-100 font-mono bg-white/80 dark:bg-neutral-800/80 rounded-lg border-l-4 border-green-700 dark:border-green-500 py-4 px-4 shadow-md">
            {dict.homePage.description.map((line: string, index: number) => (
              <span key={index}>
                {line}
                {index !== dict.homePage.description.length - 1 && <br />}
              </span>
            ))}
          </p>

          <div className="flex gap-4 flex-wrap justify-center md:justify-start">
            <a
              href="https://github.com/jvondermarck/dinosaur-exploder"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block font-retro px-7 py-3 rounded-full bg-green-700 hover:bg-green-600 dark:bg-green-600 dark:hover:bg-green-500 text-white text-base shadow-lg transition hover:scale-110"
            >
              {dict.homePage.button}
            </a>
          </div>
        </div>

      </section>

      {/* Sponsor Section (Card) */}
      <section className="w-full flex items-center justify-center pb-8">
        <div className="rounded-xl shadow-lg shadow-black/30 dark:shadow-xl bg-neutral-800/90 dark:bg-white/90 border border-neutral-600 dark:border-green-200/70 p-[5px] max-w-full flex items-center justify-center">
          <iframe
            src="https://github.com/sponsors/jvondermarck/card"
            title="Sponsor jvondermarck"
            width="600"
            height="225"
            style={{
              minWidth: "280px",
              border: 0,
              borderRadius: "0.75rem",
              background: "transparent",
              display: "block",
              verticalAlign: "middle",
            }}
            className="max-w-full"
            allow="payment"
          />
        </div>
      </section>
    </div>
  );
}