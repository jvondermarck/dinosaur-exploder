'use client'

import { GameState } from "./DinosaurArcadeGame";
import { useState, useEffect } from "react";

interface Props {
  gameState: GameState;
  score: number;
  highScore: number;
  onStart: () => void;
}

export default function GameOverlay({
  gameState,
  score,
  highScore,
  onStart,
}: Props) {
  const [isMobile, setIsMobile] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768);
      setIsLoading(false);
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  if (gameState === "playing" || isLoading) return null;

  return (
    <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
      {gameState === "menu" && (
        <>
          <h1 className="text-4xl font-bold text-green-500 mb-4 font-retro">
            DINO ARENA
          </h1>
          
          {isMobile ? (
            <>
              <p className="text-white mb-4">Use LEFT and RIGHT buttons to move</p>
            </>
          ) : (
            <>
              <p className="text-white mb-4">Hold SPACE to move up</p>
              <p className="text-white mb-6">Release SPACE to move down</p>
            </>
          )}
          <button
            onClick={onStart}
            className="bg-green-500 hover:bg-green-600 text-black font-bold py-3 px-8 rounded-lg text-xl font-retro"
          >
            START
          </button>
          
        </>
      )}

      {gameState === "gameOver" && (
        <>
          <h1 className="text-4xl font-bold text-red-600 mb-4 font-retro">
            GAME OVER
          </h1>
          {score === highScore && score > 0 && (
            <p className="text-yellow-400 mb-2">NEW HIGH SCORE!</p>
          )}
          <p className="text-cyan-400 mb-4">High Score: {highScore}</p>
          <p className="text-white text-2xl mb-2">Score: {score}</p>
          <button
            onClick={onStart}
            className="bg-green-600 hover:bg-green-500 text-black font-bold py-3 px-8 rounded text-xl font-retro"
          >
            RESTART
          </button>
        </>
      )}
    </div>
  );
}
