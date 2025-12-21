import Image from "next/image";

export default function Home() {
    return (
        <div className="min-h-screen flex flex-col bg-gradient-to-b from-green-100 via-white to-black">
            {/* Navigation */}
            <nav className="flex justify-between items-center px-8 py-4">
                <span className="font-retro text-green-700 text-2xl drop-shadow-sm tracking-wide">DINOSAUR EXPLODER</span>
                <a
                    className="px-4 py-2 rounded border border-green-700 text-green-800 bg-white hover:bg-green-700 hover:text-white font-semibold font-mono transition duration-150"
                    href="https://github.com/jvondermarck/dinosaur-exploder"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    ★ Github
                </a>
            </nav>

            {/* Hero */}
            <section className="flex flex-col-reverse md:flex-row items-center flex-1 max-w-6xl mx-auto w-full px-4 md:px-12 py-12 md:py-24">
                {/* Left/Img */}
                <div className="flex-1 flex justify-center md:justify-end mb-12 md:mb-0">
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
                        {/* halo retro */}
                        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-64 h-64 rounded-full bg-green-400/10 blur-3xl pointer-events-none -z-10"></div>
                    </div>
                </div>
                {/* Right/Text */}
                <div className="flex-1 flex flex-col items-center md:items-start text-center md:text-left">
                    <h1 className="font-retro text-4xl lg:text-6xl font-extrabold text-green-800 mb-6 leading-tight">
                        Retro Arcade<br />
                        <span className="text-black bg-green-200 px-2 rounded">Shoot 'em up</span>
                    </h1>
                    <p className="text-lg mb-8 max-w-xl text-green-900 font-mono bg-white/80 rounded-lg border-l-4 border-green-700 py-4 px-4 shadow-md">
                        <b>Dinosaur Exploder</b> is an open-source, classic arcade shooter.<br />Made with Java, JavaFX & FXGL.<br />Pure pixel action. Pure fun.<br />
                    </p>
                    <div>
                        <a
                            href="https://github.com/jvondermarck/dinosaur-exploder"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-block font-retro px-7 py-3 rounded-full bg-green-700 hover:bg-green-600 text-white text-lg shadow-lg transition hover:scale-110"
                        >
                            ★ View on GitHub ★
                        </a>
                    </div>
                </div>
            </section>

            {/* Features */}
            <section className="max-w-4xl mx-auto w-full py-8 px-4 grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="bg-black/80 border-2 border-green-700 rounded-lg p-6 text-center text-green-100">
                    <span className="block mb-2 text-2xl">🦖</span>
                    <span className="font-retro uppercase tracking-wider text-green-400">Pixel Dino Action</span>
                </div>
                <div className="bg-black/80 border-2 border-green-700 rounded-lg p-6 text-center text-green-100">
                    <span className="block mb-2 text-2xl">🕹️</span>
                    <span className="font-retro uppercase tracking-wider text-green-400">Classic Gameplay</span>
                </div>
                <div className="bg-black/80 border-2 border-green-700 rounded-lg p-6 text-center text-green-100">
                    <span className="block mb-2 text-2xl">🌍</span>
                    <span className="font-retro uppercase tracking-wider text-green-400">Open source &amp; Free</span>
                </div>
            </section>

            {/* Footer */}
            <footer className="text-center text-green-900 text-xs font-mono opacity-70 py-6 mt-auto">
                © {new Date().getFullYear()} Dinosaur Exploder — made with love — Java/JavaFX/FXGL — open source
            </footer>
        </div>
    );
}