import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "How it works · Dinosaur Exploder",
  description: "Learn the basic gameplay loop, controls, and goals of Dinosaur Exploder.",
};

export default function HowGameWorksPage() {
  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 mb-6">
        How the game works
      </h1>

      <div className="space-y-6">
        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">Goal</h2>
          <p className="font-mono text-green-950 leading-relaxed">
            You control a spaceship in a retro arcade arena. Your objective is simple: survive waves of incoming
            dinosaurs and score as many points as you can.
          </p>
        </section>

        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">Gameplay loop</h2>
          <ol className="list-decimal pl-5 font-mono text-green-950 space-y-2">
            <li>Move your spaceship to dodge dinosaurs and hazards.</li>
            <li>Shoot dinosaurs to earn points and clear the screen.</li>
            <li>Use your shield to survive tight moments.</li>
            <li>Use a bomb when the screen is crowded (it clears all dinosaurs).</li>
          </ol>
          <p className="font-mono text-green-950 mt-3">
            The difficulty increases over time as more dinosaurs appear.
          </p>
        </section>

        <section className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-800 mb-3">Controls</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse font-mono">
              <thead>
                <tr className="text-green-800">
                  <th className="py-2 pr-3">Key</th>
                  <th className="py-2">Action</th>
                </tr>
              </thead>
              <tbody>
                <tr className="border-t border-green-200">
                  <td className="py-2 pr-3">↑ ↓ ← →</td>
                  <td className="py-2">Move the spaceship</td>
                </tr>
                <tr className="border-t border-green-200">
                  <td className="py-2 pr-3">Space</td>
                  <td className="py-2">Shoot</td>
                </tr>
                <tr className="border-t border-green-200">
                  <td className="py-2 pr-3">E</td>
                  <td className="py-2">Activate shield (short duration)</td>
                </tr>
                <tr className="border-t border-green-200">
                  <td className="py-2 pr-3">B</td>
                  <td className="py-2">Bomb (eliminates all dinosaurs on screen)</td>
                </tr>
                <tr className="border-t border-green-200">
                  <td className="py-2 pr-3">Esc</td>
                  <td className="py-2">Pause</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section className="bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-300 mb-3">
            Gameplay Demo
          </h2>

          <video
            className="w-full max-h-[420px] object-contain rounded-lg border border-green-700"
            autoPlay
            loop
            controls
            preload="metadata"
          >
            <source
              src="https://github.com/user-attachments/assets/4b5a6ed4-2e68-4e12-a9c8-8a6c33178c5e"
              type="video/mp4"
            />
            Your browser does not support the video tag.
          </video>

          <p className="font-mono text-green-100 mt-3 text-sm">
            Short gameplay demo showing movement, shooting, and enemy waves.
          </p>
        </section>

        <section className="bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
          <h2 className="font-retro text-xl text-green-300 mb-3">Want to help?</h2>
          <p className="font-mono text-green-100 leading-relaxed">
            You can contribute to the game (Java/FXGL) or the website (Next.js). Check the GitHub issues and pick
            something small to start.
          </p>
        </section>
      </div>
    </div>
  );
}
