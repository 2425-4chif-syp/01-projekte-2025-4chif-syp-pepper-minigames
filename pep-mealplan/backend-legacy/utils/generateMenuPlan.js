const fs = require("fs");
const path = require("path");
const Docxtemplater = require("docxtemplater");
const Pizzip = require("pizzip");
const DBHandler = require("../DBHandler");
const logger = require("./logger");
const { getReferenceWeekFromDate } = require("../utils/referenceWeek");

const TEMPLATE_PATH = path.join(__dirname, "Vorlage_Aushang Seniorenheim Landl.docx");

// Hilfsfunktion zum Formatieren eines Datums (YYYY-MM-DD)
function formatDate(date) {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
}

/**
 * Erstellt einen Word-Menüplan basierend auf dem gegebenen Datum.
 * @param {string} dateStr - Datum im Format YYYY-MM-DD
 * @returns {Promise<string>} Pfad zur generierten Word-Datei
 */
async function generateMenuPlan(dateStr) {
    try {
        // Datum parsen und ReferenceWeek ermitteln
        const inputDate = new Date(dateStr);
        const referenceWeek = await getReferenceWeekFromDate(dateStr);
        if (!referenceWeek || isNaN(referenceWeek)) {
            throw new Error(`Keine gültige ReferenceWeek für das Datum ${dateStr} gefunden`);
        }
        logger.debug(`ReferenceWeek für Datum ${dateStr}: ${referenceWeek}`); //DEBUG

        // Menüzeilen für die entsprechende Woche abrufen
        const menuRows = await DBHandler.read("Menu", "WHERE WeekNumber = ?", [referenceWeek]);
        if (!menuRows || menuRows.length === 0) {
            throw new Error(`Kein Menü für die gegebene Woche (${referenceWeek}) gefunden`);
        }

        // Alle benötigten Food-IDs aus den Menüzeilen extrahieren
        const foodIds = [...new Set(menuRows.flatMap(row => [
            row.SoupID, row.M1ID, row.M2ID, row.LunchDessertID, row.A1ID, row.A2ID
        ]))].filter(Boolean);
        if (foodIds.length === 0) {
            throw new Error(`Keine gültigen Food-IDs für ReferenceWeek ${referenceWeek} gefunden`);
        }

        // Gerichtsnamen aus der Foods-Tabelle abrufen
        const placeholders = foodIds.map(() => "?").join(",");
        const foodRows = await DBHandler.executeQuery(
            `SELECT id, Name FROM Foods WHERE id IN (${placeholders})`,
            foodIds
        );
        if (!foodRows || foodRows.length === 0) {
            throw new Error(`Keine passenden Gerichte in der Foods-Tabelle gefunden für IDs: ${foodIds.join(", ")}`);
        }
        const foodMap = Object.fromEntries(foodRows.map(food => [food.id, food.Name]));

        // Word-Vorlage laden
        const templateContent = fs.readFileSync(TEMPLATE_PATH, "binary");
        const zip = new Pizzip(templateContent);
        const doc = new Docxtemplater(zip, {
            paragraphLoop: true,
            linebreaks: true,
        });

        // Mapping von numerischen Wochentagen (wie in der DB gespeichert) auf deutsche Bezeichnungen,
        const dayMapping = {
            "0": "Montag",
            "1": "Dienstag",
            "2": "Mittwoch",
            "3": "Donnerstag",
            "4": "Freitag",
            "5": "Samstag",
            "6": "Sonntag"
        };

        // Platzhalter-Daten vorbereiten:
        const menuData = {};
        menuRows.forEach(row => {
            // Umwandlung der in der DB gespeicherten numerischen Weekday in den entsprechenden Tagnamen
            const day = dayMapping[row.Weekday] || row.Weekday;
            menuData[`${day}Suppe`]   = foodMap[row.SoupID]         || "-";
            menuData[`${day}M1`]      = foodMap[row.M1ID]           || "-";
            menuData[`${day}M2`]      = foodMap[row.M2ID]           || "-";
            menuData[`${day}Dessert`] = foodMap[row.LunchDessertID] || "-";
            menuData[`${day}A1`]      = foodMap[row.A1ID]           || "-";
            menuData[`${day}A2`]      = foodMap[row.A2ID]           || "-";
        });
        logger.debug("Menu Data: " + JSON.stringify(menuData)); //DEBUG

        // Daten in die Vorlage einfügen
        doc.setData(menuData);
        try {
            doc.render();
        } catch (error) {
            const e = {
                message: error.message,
                name: error.name,
                stack: error.stack,
                properties: error.properties,
            };
            logger.error("Docxtemplater Fehler: " + JSON.stringify(e));
            throw error;
        }

        const dayOfWeek = inputDate.getDay(); // Sonntag = 0, Montag = 1, ...
        const diffToMonday = (dayOfWeek + 6) % 7;
        const monday = new Date(inputDate);
        monday.setDate(inputDate.getDate() - diffToMonday);
        const sunday = new Date(monday);
        sunday.setDate(monday.getDate() + 6);

        // Dynamischen Dateinamen erstellen: Speiseplan_YYYY-MM-DD_YYYY-MM-DD.docx
        const outputFileName = `Speiseplan_${formatDate(monday)}_${formatDate(sunday)}.docx`;
        const outputPath = path.join(__dirname, "../public/", outputFileName);

        const buffer = doc.getZip().generate({ type: "nodebuffer" });
        fs.writeFileSync(outputPath, buffer);
        logger.debug("Generierte Datei: " + outputPath);

        return outputPath;
    } catch (error) {
        logger.error("Fehler beim Generieren des Menüplans: " + error.toString());
        throw error;
    }
}

module.exports = { generateMenuPlan };