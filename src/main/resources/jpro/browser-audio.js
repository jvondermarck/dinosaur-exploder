(() => {
    const active = new Set();
    const pending = new Set();
    let music = null;

    function release(audio) {
        pending.delete(audio);
        active.delete(audio);
        audio.pause();
        audio.removeAttribute("src");
        audio.load();
        audio.remove();
        if (music === audio) music = null;
    }

    function start(audio) {
        audio.play().then(() => {
            pending.delete(audio);
        }).catch(error => {
            if (!active.has(audio)) return;
            if (error.name === "NotAllowedError") {
                // Browsers require a real user gesture before audible playback.
                pending.add(audio);
                console.info("Dinosaur Exploder audio is waiting for a user gesture.");
            } else {
                console.error("Dinosaur Exploder audio playback failed", error);
                release(audio);
            }
        });
    }

    function unlock() {
        for (const audio of pending) start(audio);
    }
    document.addEventListener("pointerdown", unlock, true);
    document.addEventListener("keydown", unlock, true);

    const controller = {
        play(url, isMusic, volume, muted) {
            if (isMusic) this.stopMusic();
            const audio = new Audio(url);
            audio.dataset.dinosaurAudio = isMusic ? "music" : "sfx";
            audio.loop = isMusic;
            audio.volume = volume;
            audio.muted = muted;
            audio.hidden = true;
            audio.addEventListener("ended", () => release(audio), { once: true });
            audio.addEventListener("error", () => {
                if (!active.has(audio)) return;
                console.error("Dinosaur Exploder audio resource failed", audio.error, url);
                release(audio);
            }, { once: true });
            document.body.append(audio);
            active.add(audio);
            if (isMusic) music = audio;
            start(audio);
        },
        setMuted(isMusic, muted) {
            for (const audio of active) {
                if ((audio === music) === isMusic) audio.muted = muted;
            }
        },
        setVolume(isMusic, volume) {
            for (const audio of active) {
                if ((audio === music) === isMusic) audio.volume = volume;
            }
        },
        stopMusic() {
            if (music) release(music);
        },
        stopAll() {
            for (const audio of [...active]) release(audio);
        }
    };
    window.addEventListener("pagehide", () => controller.stopAll());
    return controller;
})()
