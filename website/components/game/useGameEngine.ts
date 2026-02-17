'use client';

import { useRef } from 'react';

interface Bullet {
  x: number;
  y: number;
  id: number;
}

interface Dinosaur {
  x: number;
  y: number;
  id: number;
  speed: number;
}

interface Explosion {
  x: number;
  y: number;
  id: number;
  frame: number;
}

export function useGameEngine(onScoreUpdate: (score: number) => void) {
  const spaceshipXRef = useRef(200);
  const bulletsRef = useRef<Bullet[]>([]);
  const dinosaursRef = useRef<Dinosaur[]>([]);
  const explosionsRef = useRef<Explosion[]>([]);
  const moveLeftRef = useRef(false);
  const moveRightRef = useRef(false);
  const lastShotTimeRef = useRef(0);
  const lastDinoSpawnRef = useRef(0);
  const gameTimeRef = useRef(0);
  const bulletIdRef = useRef(0);
  const dinoIdRef = useRef(0);
  const explosionIdRef = useRef(0);
  const animationRef = useRef<number | null>(null);
  const scoreRef = useRef(0);

  const reset = () => {
    spaceshipXRef.current = 200;
    bulletsRef.current = [];
    dinosaursRef.current = [];
    explosionsRef.current = [];
    moveLeftRef.current = false;
    moveRightRef.current = false;
    scoreRef.current = 0;
    gameTimeRef.current = 0;
    lastShotTimeRef.current = 0;
    lastDinoSpawnRef.current = 0;
  };

  const updateScore = (points: number) => {
    scoreRef.current += points;
    onScoreUpdate(scoreRef.current);
  };

  return {
    spaceshipXRef,
    bulletsRef,
    dinosaursRef,
    explosionsRef,
    moveLeftRef,
    moveRightRef,
    lastShotTimeRef,
    lastDinoSpawnRef,
    gameTimeRef,
    bulletIdRef,
    dinoIdRef,
    explosionIdRef,
    animationRef,
    scoreRef,
    reset,
    updateScore,
  };
}
