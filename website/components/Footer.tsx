'use client';

export default function Footer() {
  return (
    <footer className="text-center text-green-900 text-xs font-mono opacity-70 py-6 mt-auto">
      © 2022-{new Date().getFullYear()} Dinosaur Exploder — made with love — Java/JavaFX/FXGL — open source
    </footer>
  );
}