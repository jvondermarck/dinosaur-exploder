(() => {
    const message = document.getElementById("pool-message");
    let app = null;
    let joined = false;
    let checking = false;
    let observer = null;

    async function isFull() {
        const response = await fetch("/_pool/availability", {
            cache: "no-store",
            signal: AbortSignal.timeout(5000)
        });
        if (!response.ok) throw new Error("Capacity check HTTP " + response.status);
        const result = await response.json();
        if (typeof result.full !== "boolean") throw new Error("Invalid capacity response");
        return result.full;
    }

    function leavePendingApp(path) {
        observer?.disconnect();
        // Removing the custom element does not stop this pinned client's retry timers.
        location.replace(path);
    }

    async function loadClient() {
        for (const href of ["/jpro/css/jpro-fullscreen.css", "/jpro/css/jpro.css"]) {
            const link = document.createElement("link");
            link.rel = "stylesheet";
            link.href = href;
            document.head.append(link);
        }
        await new Promise((resolve, reject) => {
            const script = document.createElement("script");
            const timer = setTimeout(() => reject(new Error("JPro client load timed out")), 10000);
            script.src = "/jpro/js/jpro.js";
            script.onload = () => { clearTimeout(timer); resolve(); };
            script.onerror = () => { clearTimeout(timer); reject(new Error("Cannot load JPro client")); };
            document.head.append(script);
        });
        if (!customElements.get("jpro-app")) throw new Error("JPro client did not initialize");
    }

    // Pinned JPro 2025.3.3 publishes loading/running/reconnecting in data-status.
    async function connectionChanged() {
        const status = app?.querySelector("[data-status]")?.dataset.status;
        if (status === "running") {
            joined = true;
            observer.disconnect();
        }
        if (joined || status !== "reconnecting" || checking) return;
        checking = true;
        try {
            if (await isFull() && !joined) {
                leavePendingApp("/busy.html");
            }
        } catch (error) {
            console.error("Dinosaur Exploder capacity check failed", error);
            if (!joined) {
                leavePendingApp("/unavailable.html");
            }
        } finally {
            checking = false;
        }
    }

    async function start() {
        try {
            if (await isFull()) {
                leavePendingApp("/busy.html");
                return;
            }
            await loadClient();
            message.hidden = true;
            app = document.createElement("jpro-app");
            app.setAttribute("href", "/app/DinosaurExploderJProWebApp");
            app.setAttribute("fullscreen", "true");
            observer = new MutationObserver(connectionChanged);
            observer.observe(app, { subtree: true, childList: true, attributes: true, attributeFilter: ["data-status"] });
            document.body.append(app);
        } catch (error) {
            console.error("Dinosaur Exploder startup failed", error);
            leavePendingApp("/unavailable.html");
        }
    }
    start();
})();
