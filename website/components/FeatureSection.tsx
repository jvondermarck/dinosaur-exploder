/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

type Feature = {
  icon: string;
  title: string;
};

export default function FeatureSection({dict}: {dict: Feature[] }) {
  return (
    <section className="max-w-4xl mx-auto w-full py-8 px-4 grid grid-cols-1 md:grid-cols-3 gap-4">
      {dict.map((feature, idx) => (
        <div
          key={idx}
          className="bg-black/80 dark:bg-neutral-800/90 border-2 border-green-700 dark:border-green-500 rounded-lg p-6 text-center text-green-100 dark:text-green-200"
        >
          <span className="block mb-2 text-2xl">{feature.icon}</span>
          <span className="font-retro uppercase tracking-wider text-green-400 dark:text-green-400">{feature.title}</span>
        </div>
      ))}
    </section>
  );
}