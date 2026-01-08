import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Contact · Dinosaur Exploder",
  description: "How to reach the Dinosaur Exploder community and maintainers.",
};

const links = [
  {
    title: "GitHub Discussions",
    description: "Ask questions, propose ideas, and get help.",
    href: "https://github.com/jvondermarck/dinosaur-exploder/discussions",
  },
  {
    title: "Discord",
    description: "Quick chat with the community.",
    href: "https://discord.com/invite/nkmCRnXbWm",
  },
  {
    title: "Twitter",
    description: "Follow updates and announcements.",
    href: "https://twitter.com/DinosaurExplod1",
  },
  {
    title: "Sponsor",
    description: "Support development if you like the project.",
    href: "https://github.com/sponsors/jvondermarck",
  },
];

export default function ContactPage() {
  return (
    <div className="max-w-4xl mx-auto w-full px-4 md:px-8 py-10">
      <h1 className="font-retro text-3xl md:text-4xl text-green-800 mb-3">
        Contact
      </h1>
      <p className="font-mono text-green-950 mb-8">
        The fastest way to reach us is through the community channels below.
      </p>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {links.map((l) => (
          <a
            key={l.title}
            href={l.href}
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white/80 rounded-xl border border-green-200 p-5 shadow-sm hover:shadow-md transition"
          >
            <div className="font-retro text-green-800 text-lg mb-1">
              {l.title}
            </div>
            <div className="font-mono text-green-950 opacity-80 text-sm">
              {l.description}
            </div>
          </a>
        ))}
      </div>

      <div className="mt-10 bg-black/80 rounded-xl border-2 border-green-700 p-5 shadow-sm">
        <h2 className="font-retro text-xl text-green-300 mb-2">Contribute</h2>
        <p className="font-mono text-green-100">
          Want to help? Pick an issue on GitHub, or contribute to the website in the <code>/website</code> folder.
        </p>
      </div>
    </div>
  );
}
