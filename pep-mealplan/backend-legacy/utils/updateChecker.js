const fetch = require("node-fetch");
const fs = require("fs");
const path = require("path");
const logger = require('./logger');
const config = require('./config');

let updateAvailable = false;
let latestVersion = "";
let currentVersion = null;

async function checkForNewVersion() {
    try {
        logger.debug(`Update check sensitivity: '${config.updateCheckSensitivity}'`);
        logger.info("Checking for new version...");

        if (fs.existsSync(".app_version")) {
            currentVersion = fs.readFileSync(".app_version", "utf8").trim();
            logger.debug("Version read from .app_version file.");
        } else {
            const packageJson = require(path.join(__dirname, "..", "package.json"));
            currentVersion = packageJson.version;
            logger.debug("No .app_version found – using version from package.json instead.");
        }

        const res = await fetch("https://hub.docker.com/v2/repositories/tricktrack2500/meal-planner-app/tags?page_size=100&ordering=last_updated");
        const data = await res.json();

        const tags = data.results.map(r => r.name).filter(name => /^\d+\.\d+\.\d+$/.test(name));
        latestVersion = tags.sort(compareSemver).reverse()[0];

        updateAvailable = compareVersionsRespectingSensitivity(currentVersion, latestVersion, config.updateCheckSensitivity);

        if (updateAvailable) {
            logger.warn(`New version available: ${latestVersion} (current: ${currentVersion})`);
        } else {
            logger.info(`You are using the latest version: ${currentVersion}`);
        }
    } catch (error) {
        logger.error("Error during version check", error);
    }
}

function compareSemver(a, b) {
    const pa = a.split('.').map(Number);
    const pb = b.split('.').map(Number);
    return pa[0] - pb[0] || pa[1] - pb[1] || pa[2] - pb[2];
}

function compareVersionsRespectingSensitivity(current, latest, sensitivity) {
    if (sensitivity === "none") return false;

    const [currMaj, currMin, currPatch] = current.split('.').map(Number);
    const [latestMaj, latestMin, latestPatch] = latest.split('.').map(Number);

    if (sensitivity === "major") return latestMaj > currMaj;
    if (sensitivity === "minor") return latestMaj > currMaj || (latestMaj === currMaj && latestMin > currMin);
    if (sensitivity === "patch") return (
        latestMaj > currMaj ||
        (latestMaj === currMaj && latestMin > currMin) ||
        (latestMaj === currMaj && latestMin === currMin && latestPatch > currPatch)
    );

    return false;
}

function getVersionStatus() {
    return {
        current: currentVersion,
        latest: latestVersion,
        updateAvailable
    };
}

// Konfiguration überprüfen
const validLevels = ["none", "patch", "minor", "major"];
if (!validLevels.includes(config.updateCheckSensitivity)) {
    logger.warn(`Invalid updateCheckSensitivity '${config.updateCheckSensitivity}', defaulting to 'minor'`);
    config.updateCheckSensitivity = "minor";
}

// Direkt beim Start prüfen
if(config.updateCheckSensitivity !== "none") {
    checkForNewVersion();

    // Alle 24h erneut
    setInterval(checkForNewVersion, 24 * 60 * 60 * 1000);
}else {
    logger.info("Update check is disabled. Skipping version check...");
}

// Function Get Version
function getVersion() {
    return currentVersion;
}

module.exports = { checkForNewVersion, getVersionStatus, getVersion };