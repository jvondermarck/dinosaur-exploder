import FeatureSection from "@/components/FeatureSection";
import Image from "next/image";
import { getDictionary } from "@/getDictionary";

export default async function Home({params}: {params: Promise<{lang: string}>}) {
  const {lang} = await params;
  const dict = await getDictionary(lang as any);

  return (
    <div className="flex flex-col">
      {/* Hero */}
      <section className="flex flex-col-reverse md:flex-row items-center flex-1 max-w-6xl mx-auto w-full px-4 md:px-12 py-12 md:py-24">
        {/* Left/Img */}
        <div className="flex-1 flex justify-center md:justify-center mb-12 md:mb-0">
          <div className="relative">
            <Image
              src="/dinomenu.png"
              alt="Dino icon"
              width={230}
              height={230}
              className="drop-shadow-2xl select-none"
              style={{
                imageRendering: "pixelated",
                background: "none",
              }}
              priority
            />
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-64 h-64 rounded-full bg-green-400/10 blur-3xl pointer-events-none -z-10"></div>
          </div>
        </div>
        {/* Right/Text */}
        <div className="flex-1 flex flex-col items-center md:items-start text-center md:text-left">
          <h1 className="font-retro text-4xl lg:text-6xl font-extrabold text-green-800 mb-6 leading-tight">
            Retro Arcade<br />
            <span className="text-black bg-green-200 px-2 rounded">Shoot &apos;em up </span>
          </h1>
          <p className="text-lg mb-6 max-w-xl text-green-900 font-mono bg-white/80 rounded-lg border-l-4 border-green-700 py-4 px-4 shadow-md">
           {dict.homePage.description.map((line: string, index: number) => (
    <span key={index}>
      {line}
      {/* add line change (<br />), except from last row */}
      {index !== dict.homePage.description.length - 1 && <br />}
    </span>
  ))}
          </p>
          <div className="flex gap-4 flex-wrap justify-center md:justify-start mb-2">
            <a
              href="https://github.com/jvondermarck/dinosaur-exploder"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-block font-retro px-7 py-3 rounded-full bg-green-700 hover:bg-green-600 text-white text-lg shadow-lg transition hover:scale-110"
            >
              {dict.homePage.button}
            </a>
          </div>
        </div>
      </section>

      {/* Sponsor Section (Card) */}
      <section className="w-full flex justify-center pb-8">
        <div className="overflow-auto rounded-xl shadow-xl bg-white/90 p-2 max-w-full">
          <iframe
            src="https://github.com/sponsors/jvondermarck/card"
            title="Sponsor jvondermarck"
            width="600"
            height="200"
            style={{
              minWidth: "280px",
              minHeight: "200px",
              border: 0,
              borderRadius: "1rem",
              background: "transparent"
            }}
            className="max-w-full max-h-full"
            allow="payment"
          />
        </div>
      </section>

      {/* Features */}
      <FeatureSection dict={dict.features}/>
    </div>
  );
}