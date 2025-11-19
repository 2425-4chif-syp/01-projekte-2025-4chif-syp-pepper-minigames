const express = require('express');
const logger = require('../../utils/logger');
const { generateMenuPlan } = require("../../utils/generateMenuPlan");
const router = express.Router();

/**
 * GET /api/orders
 * Exportiert den Aushang des Menüplans für eine bestimmte Woche (definiert durch das Datum)
 */
router.get("/:date", async (req, res) => {
  try {
    const date = req.params.date; // Datum aus der URL
    const filePath = await generateMenuPlan(date);
    logger.debug(`Menüplan für Datum ${date} generiert: ${filePath}`); //DEBUG
    const fileName = filePath.split("\\").pop();
    logger.debug(`Dateiname: ${fileName}`); //DEBUG
    res.setHeader('Cache-Control', 'no-store, no-cache, must-revalidate, proxy-revalidate');
    res.setHeader('Pragma', 'no-cache');
    res.setHeader('Expires', '0');
    res.setHeader('Surrogate-Control', 'no-store');
    res.download(filePath, fileName, (err) => {
      if (err) {
        console.error("Fehler beim Senden der Datei:", err);
        res.status(500).send("Fehler beim Generieren der Datei");
      }
    });

  } catch (error) {
    console.error("Fehler beim Abrufen des Menüplans:", error);
    res.status(500).send("Fehler beim Erstellen des Menüplans");
  }
});  

module.exports = router;