/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

import type { Metadata } from "next";
import { getDictionary } from "@/getDictionary";
import {Locale} from "../../../i18n-config";

export const metadata: Metadata = {
  title: "How it works · Dinosaur Exploder",
  description: "Learn the basic gameplay loop, controls, and goals of Dinosaur Exploder.",
};

export default async function HowGameWorksPage({params,}: {params: Promise<{lang: string}>;}) {
  const {lang} = await params;
  const dict = await getDictionary(lang as Locale);
  const { title, goal, gameplay, controls, demo, help } = dict.howGameWorks;
  
  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 mb-6">
        {title}
      </h1>

      <div className="space-y-6">
        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">{goal.title}</h2>
          <p className="font-handjet text-green-950 leading-relaxed">
            {goal.descr}
          </p>
        </section>

        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">{gameplay.title}</h2>
          <ol className="list-decimal pl-5 font-handjet text-green-950 space-y-2">
            {gameplay.steps.map((step: string, index: number) => (
      <li key={index}>{step}</li>
    ))}
          </ol>
          <p className="font-handjet text-green-950 mt-3">
            {gameplay.difficulty}
          </p>
        </section>

        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">{controls.title}</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse font-handjet">
              <thead>
                <tr className="text-green-800">
                  <th className="py-2 pr-3">{controls.headers.key}</th>
                  <th className="py-2">{controls.headers.action}</th>
                </tr>
              </thead>
              <tbody>
                {controls.list.map((item: any, index: number) => (
                  <tr key={index} className="border-t border-green-200 text-black">
                    <td className="py-2 pr-3 font-bold text-green-700">{item.key}</td>
                    <td className="py-2">{item.action}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-300 mb-3">
            {demo.title}
          </h2>

          <video
            className="w-full max-h-[420px] object-contain rounded-lg border border-green-700"
            autoPlay={false}
            loop
            controls
            preload="metadata"
          >
            <source
              src="https://github.com/user-attachments/assets/4b5a6ed4-2e68-4e12-a9c8-8a6c33178c5e"
              type="video/mp4"
            />
            {demo.error}
          </video>

          <p className="font-handjet text-green-100 mt-3 text-sm">
            {demo.description}
          </p>
        </section>

        <section className="bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-300 mb-3"> {help.title} </h2>
          <p className="font-handjet text-green-100 leading-relaxed">
            {help.description}
          </p>
        </section>
      </div>
    </div>
  );
}
