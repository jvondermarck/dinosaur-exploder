'use client'

import { useEffect, useRef, useState } from 'react';
import { GameState } from './DinosaurArcadeGame';
import { useGameEngine } from './useGameEngine';
import { useImageLoader } from './useImageLoader';

interface Props {
  gameState: GameState;
  onGameOver: (score: number) => void;
  onScoreUpdate: (score: number) => void;
}

export default function GameCanvas({ gameState, onGameOver, onScoreUpdate }: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const gameEngine = useGameEngine(onScoreUpdate);
  const images = useImageLoader();
  const prevGameStateRef = useRef<GameState>('menu');
  const [isMobile, setIsMobile] = useState(false);
  
  const {
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
    updateScore
  } = gameEngine;

  const CANVAS_WIDTH = isMobile ? 350 : 800;
  const CANVAS_HEIGHT = isMobile ? 550 : 500;
  const SPACESHIP_POS = isMobile ? 450 : 50;
  const SPACESHIP_SIZE = isMobile ? 50 : 60;
  const SPACESHIP_SPEED = 6;
  const BULLET_SPEED = 8;
  const BULLET_WIDTH = isMobile ? 12 : 30;
  const BULLET_HEIGHT = isMobile ? 25 : 15;
  const DINO_SIZE = isMobile ? 50 : 60;
  const EXPLOSION_FRAMES = 16;
  const EXPLOSION_SIZE = isMobile ? 50 : 64;
  const SHOOT_INTERVAL = 200;

  useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768);
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  useEffect(() => {
    if (!isMobile) {
      const handleKeyDown = (e: KeyboardEvent) => {
        if (e.code === 'Space') {
          e.preventDefault();
          moveLeftRef.current = true;
        }
      };

      const handleKeyUp = (e: KeyboardEvent) => {
        if (e.code === 'Space') {
          e.preventDefault();
          moveLeftRef.current = false;
        }
      };

      window.addEventListener('keydown', handleKeyDown);
      window.addEventListener('keyup', handleKeyUp);

      return () => {
        window.removeEventListener('keydown', handleKeyDown);
        window.removeEventListener('keyup', handleKeyUp);
      };
    }
  }, [isMobile, moveLeftRef]);

  useEffect(() => {
    if (gameState !== 'playing') {
      prevGameStateRef.current = gameState;
      return;
    }

    const canvas = canvasRef.current;
    if (!canvas || !images.loaded) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    if (prevGameStateRef.current !== 'playing') {
      reset();
      if (isMobile) {
        spaceshipXRef.current = CANVAS_WIDTH / 2 - SPACESHIP_SIZE / 2;
      } else {
        spaceshipXRef.current = CANVAS_HEIGHT / 2 - SPACESHIP_SIZE / 2;
      }
    }
    prevGameStateRef.current = 'playing';
    
    let lastTime = performance.now();

    const gameLoop = (time: number) => {
      const deltaTime = time - lastTime;
      lastTime = time;

      gameTimeRef.current += deltaTime;

      ctx.fillStyle = '#000000';
      ctx.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

      if (isMobile) {
        if (moveLeftRef.current) {
          spaceshipXRef.current -= SPACESHIP_SPEED;
          if (spaceshipXRef.current < 0) spaceshipXRef.current = 0;
        }
        if (moveRightRef.current) {
          spaceshipXRef.current += SPACESHIP_SPEED;
          if (spaceshipXRef.current > CANVAS_WIDTH - SPACESHIP_SIZE) {
            spaceshipXRef.current = CANVAS_WIDTH - SPACESHIP_SIZE;
          }
        }
      } else {
        if (moveLeftRef.current) {
          spaceshipXRef.current -= SPACESHIP_SPEED;
          if (spaceshipXRef.current < 0) spaceshipXRef.current = 0;
        } else {
          spaceshipXRef.current += SPACESHIP_SPEED;
          if (spaceshipXRef.current > CANVAS_HEIGHT - SPACESHIP_SIZE) {
            spaceshipXRef.current = CANVAS_HEIGHT - SPACESHIP_SIZE;
          }
        }
      }

      if (time - lastShotTimeRef.current > SHOOT_INTERVAL) {
        if (isMobile) {
          bulletsRef.current.push({
            x: spaceshipXRef.current + SPACESHIP_SIZE / 2,
            y: SPACESHIP_POS,
            id: bulletIdRef.current++,
          });
        } else {
          bulletsRef.current.push({
            x: SPACESHIP_POS + SPACESHIP_SIZE,
            y: spaceshipXRef.current + SPACESHIP_SIZE / 2,
            id: bulletIdRef.current++,
          });
        }
        lastShotTimeRef.current = time;
      }

      const spawnInterval = Math.max(800 - gameTimeRef.current / 50, 300);
      if (time - lastDinoSpawnRef.current > spawnInterval) {
        const dinoSpeed = 2 + gameTimeRef.current / 10000;
        if (isMobile) {
          dinosaursRef.current.push({
            x: Math.random() * (CANVAS_WIDTH - DINO_SIZE),
            y: -DINO_SIZE,
            id: dinoIdRef.current++,
            speed: dinoSpeed,
          });
        } else {
          dinosaursRef.current.push({
            x: CANVAS_WIDTH,
            y: Math.random() * (CANVAS_HEIGHT - DINO_SIZE),
            id: dinoIdRef.current++,
            speed: dinoSpeed,
          });
        }
        lastDinoSpawnRef.current = time;
      }

      bulletsRef.current = bulletsRef.current.filter((bullet) => {
        if (isMobile) {
          bullet.y -= BULLET_SPEED;
          return bullet.y > -BULLET_HEIGHT;
        } else {
          bullet.x += BULLET_SPEED;
          return bullet.x < CANVAS_WIDTH;
        }
      });

      dinosaursRef.current = dinosaursRef.current.filter((dino) => {
        if (isMobile) {
          dino.y += dino.speed;
          return dino.y < CANVAS_HEIGHT;
        } else {
          dino.x -= dino.speed;
          return dino.x > -DINO_SIZE;
        }
      });

      bulletsRef.current = bulletsRef.current.filter((bullet) => {
        let hit = false;
        dinosaursRef.current = dinosaursRef.current.filter((dino) => {
          let collision = false;
          if (isMobile) {
            collision = bullet.x > dino.x &&
                       bullet.x < dino.x + DINO_SIZE &&
                       bullet.y > dino.y &&
                       bullet.y < dino.y + DINO_SIZE;
          } else {
            collision = bullet.x < dino.x + DINO_SIZE &&
                       bullet.x + BULLET_WIDTH > dino.x &&
                       bullet.y < dino.y + DINO_SIZE &&
                       bullet.y + BULLET_HEIGHT > dino.y;
          }
          
          if (collision) {
            hit = true;
            updateScore(10);
            explosionsRef.current.push({
              x: dino.x,
              y: dino.y,
              id: explosionIdRef.current++,
              frame: 0,
            });
            return false;
          }
          return true;
        });
        return !hit;
      });

      explosionsRef.current = explosionsRef.current.filter((explosion) => {
        explosion.frame += 0.5;
        return explosion.frame < EXPLOSION_FRAMES;
      });

      for (const dino of dinosaursRef.current) {
        let collision = false;
        if (isMobile) {
          collision = spaceshipXRef.current < dino.x + DINO_SIZE &&
                     spaceshipXRef.current + SPACESHIP_SIZE > dino.x &&
                     SPACESHIP_POS < dino.y + DINO_SIZE &&
                     SPACESHIP_POS + SPACESHIP_SIZE > dino.y;
        } else {
          collision = SPACESHIP_POS < dino.x + DINO_SIZE &&
                     SPACESHIP_POS + SPACESHIP_SIZE > dino.x &&
                     spaceshipXRef.current < dino.y + DINO_SIZE &&
                     spaceshipXRef.current + SPACESHIP_SIZE > dino.y;
        }
        
        if (collision) {
          onGameOver(scoreRef.current);
          return;
        }
      }

      if (images.spaceship) {
        if (isMobile) {
          ctx.drawImage(
            images.spaceship,
            spaceshipXRef.current,
            SPACESHIP_POS,
            SPACESHIP_SIZE,
            SPACESHIP_SIZE
          );
        } else {
          ctx.save();
          ctx.translate(
            SPACESHIP_POS + SPACESHIP_SIZE / 2,
            spaceshipXRef.current + SPACESHIP_SIZE / 2
          );
          ctx.rotate(Math.PI / 2);
          ctx.drawImage(
            images.spaceship,
            -SPACESHIP_SIZE / 2,
            -SPACESHIP_SIZE / 2,
            SPACESHIP_SIZE,
            SPACESHIP_SIZE
          );
          ctx.restore();
        }
      }

      bulletsRef.current.forEach((bullet) => {
        if (images.bullet) {
          if (isMobile) {
            ctx.save();
            ctx.translate(bullet.x, bullet.y);
            ctx.rotate(Math.PI / 2);
            ctx.drawImage(
              images.bullet,
              -BULLET_HEIGHT / 2,
              -BULLET_WIDTH / 2,
              BULLET_HEIGHT,
              BULLET_WIDTH
            );
            ctx.restore();
          } else {
            ctx.drawImage(
              images.bullet,
              bullet.x,
              bullet.y - BULLET_HEIGHT / 2,
              BULLET_WIDTH,
              BULLET_HEIGHT
            );
          }
        }
      });

      dinosaursRef.current.forEach((dino) => {
        if (images.dino) {
          if (isMobile) {
            ctx.save();
            ctx.translate(dino.x + DINO_SIZE / 2, dino.y + DINO_SIZE / 2);
            ctx.rotate(-Math.PI / 2);
            ctx.drawImage(images.dino, -DINO_SIZE / 2, -DINO_SIZE / 2, DINO_SIZE, DINO_SIZE);
            ctx.restore();
          } else {
            ctx.drawImage(images.dino, dino.x, dino.y, DINO_SIZE, DINO_SIZE);
          }
        }
      });

      explosionsRef.current.forEach((explosion) => {
        if (images.explosion) {
          const frameIndex = Math.floor(explosion.frame);
          const frameWidth = images.explosion.width / EXPLOSION_FRAMES;
          const frameHeight = images.explosion.height;

          ctx.drawImage(
            images.explosion,
            frameIndex * frameWidth,
            0,
            frameWidth,
            frameHeight,
            explosion.x - EXPLOSION_SIZE / 2,
            explosion.y - EXPLOSION_SIZE / 2,
            EXPLOSION_SIZE,
            EXPLOSION_SIZE
          );
        }
      });

      ctx.fillStyle = '#ffffff';
      ctx.font = '20px Arial, Helvetica, sans-serif';
      ctx.fillText(`Score: ${scoreRef.current}`, 10, 30);

      animationRef.current = requestAnimationFrame(gameLoop);
    };

    animationRef.current = requestAnimationFrame(gameLoop);

    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, [gameState, images, onGameOver, isMobile]);

  if (!images.loaded) {
    return <div className="text-white text-xl">...</div>;
  }

  return (
    <div className="flex flex-col items-center gap-4">
      <canvas
        ref={canvasRef}
        width={CANVAS_WIDTH}
        height={CANVAS_HEIGHT}
        className="border-4 border-green-600 rounded-lg"
      />
      
      {gameState === 'playing' && isMobile && (
        <div className="flex gap-20 max-w-md">
          <button
            onTouchStart={() => moveLeftRef.current = true}
            onTouchEnd={() => moveLeftRef.current = false}
            onMouseDown={() => moveLeftRef.current = true}
            onMouseUp={() => moveLeftRef.current = false}
            onMouseLeave={() => moveLeftRef.current = false}
            className="flex-1 size-20 bg-red-600 hover:bg-red-700 active:bg-yellow-500 rounded-full"
          >
            
          </button>
          <button
            onTouchStart={() => moveRightRef.current = true}
            onTouchEnd={() => moveRightRef.current = false}
            onMouseDown={() => moveRightRef.current = true}
            onMouseUp={() => moveRightRef.current = false}
            onMouseLeave={() => moveRightRef.current = false}
            className="flex-1 size-20 bg-red-600 hover:bg-red-700 active:bg-yellow-500 rounded-full"
          >
            
          </button>
        </div>
      )}
    </div>
  );
}
