import capacity from "./capacity.js";

function status(stopped, running, full, buffered) {
    return "ServerManager:<br>\n"
        + "stoppedServers: " + stopped + "<br>\n"
        + "bufferedServer: " + buffered + "<br>\n"
        + "runningServers: " + running + "<br>\n"
        + "fullServers: " + full + "<br>\n"
        + "http://localhost:8101 -> private-instance";
}

const fixtures = [
    [1, 1, 0, 1, false],
    [0, 2, 1, 1, false],
    [0, 2, 2, 0, true],
    [0, 3, 3, 0, true],
    [0, 3, 2, 1, false]
];
fixtures.forEach(fixture => {
    const result = capacity.parseStatus(status(fixture[0], fixture[1], fixture[2], fixture[3]));
    if (JSON.stringify(result) !== JSON.stringify({ full: fixture[4] })) {
        throw new Error("Incorrect capacity response");
    }
});
const invalid = [
    "", "<html>upstream failed</html>",
    status(0, 0, 0, 0),
    status(0, 2, 3, 0),
    status(0, 2, 2, 1),
    status(-1, 2, 1, 0),
    status(0, 2, 2, 0) + "\nfullServers: 0<br>",
    status(9007199254740992, 1, 0, 1)
];
invalid.forEach(body => {
    let failed = false;
    try {
        capacity.parseStatus(body);
    } catch (error) {
        failed = true;
    }
    if (!failed) throw new Error("Invalid telemetry must not produce a capacity answer");
});
console.log("Pinned capacity parser tests passed");

async function checkHandlers() {
    const cases = [
        { status: 200, responseText: status(0, 2, 2, 0), expected: 200, body: '{"full":true}' },
        { status: 200, responseText: status(1, 1, 0, 1), expected: 200, body: '{"full":false}' },
        { status: 503, responseText: "unavailable", expected: 503 },
        { status: 200, responseText: "not pool telemetry", expected: 503 },
        { rejected: true, expected: 503 }
    ];
    for (let i = 0; i < cases.length; i++) {
        const fixture = cases[i];
        let responseCode = null;
        let responseBody = null;
        let logged = false;
        const request = {
            headersOut: {},
            subrequest: path => {
                if (path !== "/_internal/pool-status") throw new Error("Unexpected upstream path");
                return fixture.rejected ? Promise.reject(new Error("network failure")) : Promise.resolve(fixture);
            },
            error: () => { logged = true; },
            return: (code, body) => { responseCode = code; responseBody = body; }
        };
        await capacity.availability(request);
        const expectedBody = fixture.body || '{"error":"capacity_unavailable"}';
        if (responseCode !== fixture.expected || responseBody !== expectedBody
            || logged !== (fixture.expected === 503)
            || request.headersOut["Cache-Control"] !== "no-store"
            || request.headersOut["Content-Type"] !== "application/json") {
            throw new Error("Unexpected capacity handler response");
        }
    }
    console.log("Capacity handler error propagation and response-shape tests passed");
}
checkHandlers();
