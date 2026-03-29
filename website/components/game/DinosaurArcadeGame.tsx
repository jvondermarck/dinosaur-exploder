"use client";

import { useState, useCallback, useEffect } from "react";
import GameCanvas from "./GameCanvas";
import GameOverlay from "./GameOverlay";

export type GameState = "menu" | "playing" | "gameOver";

const HIGH_SCORE_KEY = "dinoArenaHighScore";

export default function DinosaurArcadeGame() {
  const [gameState, setGameState] = useState<GameState>("menu");
  const [score, setScore] = useState(0);
  const [highScore, setHighScore] = useState(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem(HIGH_SCORE_KEY);
      return stored ? parseInt(stored, 10) : 0;
    }
    return 0;
  });

  useEffect(() => {
    if (typeof window !== "undefined") {
      localStorage.setItem(HIGH_SCORE_KEY, highScore.toString());
    }
  }, [highScore]);

  const handleGameOver = useCallback((finalScore: number) => {
    setScore(finalScore);
    setHighScore((prev) => Math.max(prev, finalScore));
    setGameState("gameOver");
  }, []);

  const startGame = useCallback(() => {
    setScore(0);
    setGameState("playing");
  }, []);

  return (
    <div className="relative flex flex-col items-center justify-center gap-2">
      <GameCanvas
        gameState={gameState}
        onGameOver={handleGameOver}
        onScoreUpdate={setScore}
      />

      <GameOverlay
        gameState={gameState}
        score={score}
        highScore={highScore}
        onStart={startGame}
      />
    </div>
  );
}
