// This contract is specific to the pinned JPro 2025.3.3 status response.
function parseStatus(body) {
    function count(label) {
        const matches = body.match(new RegExp("(?:^|\\n)" + label + ": ([0-9]+)<br>", "g"));
        if (!matches || matches.length !== 1) throw new Error("Missing or repeated " + label);
        const value = Number(matches[0].match(/: ([0-9]+)<br>/)[1]);
        if (!Number.isSafeInteger(value)) throw new Error("Invalid " + label);
        return value;
    }
    const stopped = count("stoppedServers");
    const running = count("runningServers");
    const full = count("fullServers");
    const buffered = count("bufferedServer");
    const total = stopped + running;
    if (!Number.isSafeInteger(total) || total < 1 || full + buffered > running) {
        throw new Error("Inconsistent pool status");
    }
    return { full: full === total };
}

async function availability(r) {
    r.headersOut["Content-Type"] = "application/json";
    r.headersOut["Cache-Control"] = "no-store";
    try {
        const response = await r.subrequest("/_internal/pool-status");
        if (response.status !== 200) throw new Error("Pool status HTTP " + response.status);
        r.return(200, JSON.stringify(parseStatus(response.responseText)));
    } catch (error) {
        r.error("Cannot determine pool capacity: " + error.message);
        r.return(503, '{"error":"capacity_unavailable"}');
    }
}

export default { parseStatus, availability };
