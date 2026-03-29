import DinosaurArcadeGame from "@/components/game/DinosaurArcadeGame";
import "./globals.css";

export default function NotFound() {
  return (
    <div className="min-h-screen bg-[#171717] text-white flex flex-col items-center pt-8 md:justify-center">
      <div className="text-center mb-6">
        <h1 className="font-retro text-2xl md:text-4xl text-green-700 mb-4">
          404 ERROR
        </h1>
        <p className="font-retro text-sm md:text-lg text-gray-200">
          PAGE NOT FOUND
        </p>
      </div>
      <DinosaurArcadeGame />
    </div>
  );
}
