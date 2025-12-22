'use client';

export default function NavBar() {
  return (
    <nav className="flex justify-between items-center px-8 py-4">
      <span className="font-retro text-green-700 text-2xl drop-shadow-sm tracking-wide">
        DINOSAUR EXPLODER
      </span>
      <div className="flex gap-2">
        <a
          className="px-4 py-2 rounded border border-green-700 text-green-800 bg-white hover:bg-green-700 hover:text-white font-semibold font-mono transition duration-150"
          href="https://github.com/jvondermarck/dinosaur-exploder"
          target="_blank"
          rel="noopener noreferrer"
        >
          Github
        </a>
        <a
          className="px-4 py-2 rounded border border-pink-700 text-white bg-pink-600 hover:bg-pink-700 font-semibold font-mono transition duration-150 flex items-center gap-2"
          href="https://github.com/sponsors/jvondermarck"
          target="_blank"
          rel="noopener noreferrer"
        >
          <svg width="18" height="18" viewBox="0 0 16 16" fill="currentColor" aria-hidden="true">
            <path d="M8 2.748-.717-1.737C5.6-.34 8 .223 8 4.42c0 4.197-2.4 4.76-7.717 2.016zM8 15C-7.333 4.868 3.278-3.04 7.824 1.143c.06.055.119.112.176.171a3.12 3.12 0 0 1 .176-.17C12.723-3.042 23.333 4.868 8 15z"></path>
          </svg>
          Sponsor
        </a>
      </div>
    </nav>
  );
}