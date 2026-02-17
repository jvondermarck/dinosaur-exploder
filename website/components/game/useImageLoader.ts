'use client';

import { useEffect, useState } from 'react';

export function useImageLoader() {
  const [images, setImages] = useState<{
    spaceship: HTMLImageElement | null;
    dino: HTMLImageElement | null;
    bullet: HTMLImageElement | null;
    explosion: HTMLImageElement | null;
    loaded: boolean;
  }>({
    spaceship: null,
    dino: null,
    bullet: null,
    explosion: null,
    loaded: false,
  });

  useEffect(() => {
    const loadImage = (src: string): Promise<HTMLImageElement> => {
      return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = reject;
        img.src = src;
      });
    };

    Promise.all([
      loadImage('/spaceship1.png'),
      loadImage('/greenDino.png'),
      loadImage('/basicProjectile.png'),
      loadImage('/explosion.png'),
    ])
      .then(([ship, dino, bullet, explosion]) => {
        setImages({
          spaceship: ship,
          dino: dino,
          bullet: bullet,
          explosion: explosion,
          loaded: true,
        });
      })
      .catch((err) => {
        console.error('Failed to load images:', err);
      });
  }, []);

  return images;
}
