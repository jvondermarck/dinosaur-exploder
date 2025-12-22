'use client';

export default function NavBar() {
  return (
    <nav className="w-full px-2 sm:px-6 py-4 bg-transparent">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 sm:gap-0">
        {/* Title */}
        <span className="font-retro text-green-700 text-2xl drop-shadow-sm tracking-wide text-center sm:text-left">
          DINOSAUR EXPLODER
        </span>
        {/* Buttons */}
        <div className="flex flex-col xs:flex-row gap-2 justify-center sm:justify-end">
          <a
            className="px-4 py-2 w-full xs:w-auto rounded border border-green-700 text-green-800 bg-white hover:bg-green-700 hover:text-white font-semibold font-mono transition duration-150 text-center"
            href="https://github.com/jvondermarck/dinosaur-exploder"
            target="_blank"
            rel="noopener noreferrer"
          >
            Github
          </a>
          <a
            className="px-4 py-2 w-full xs:w-auto rounded border border-pink-700 text-white bg-pink-600 hover:bg-pink-700 font-semibold font-mono transition duration-150 flex items-center gap-2 justify-center"
            href="https://github.com/sponsors/jvondermarck"
            target="_blank"
            rel="noopener noreferrer"
          >
            <svg width="18" height="18" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
              <path d="M8 2.748-.717-1.737C5.6-.34 8 .223 8 4.42c0 4.197-2.4 4.76-7.717 2.016zM8 15C-7.333 4.868 3.278-3.04 7.824 1.143c.06.055.119.112.176.171a3.12 3.12 0 0 1 .176-.17C12.723-3.042 23.333 4.868 8 15z"/>
            </svg>
            Sponsor
          </a>
        </div>
      </div>
    </nav>
  );
}