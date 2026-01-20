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

/**
 * @swagger
 * tags:
 *   - name: Menu Plan
 *     description: Export/Aushang des Menüplans
 *
 * components:
 *   schemas:
 *     ErrorResponse:
 *       type: object
 *       properties:
 *         error:
 *           type: string
 *           example: Fehler beim Erstellen des Menüplans
 *
 * /api/orders/export/{date}:
 *   get:
 *     tags: [Menu Plan]
 *     summary: Exportiert den Aushang des Menüplans für die Kalenderwoche des angegebenen Datums
 *     description: |
 *       Liefert eine generierte Datei (z. B. PDF/DOCX/PNG) als Download zurück.
 *       **Hinweis:** Die Route ist als `export`-Subpfad dokumentiert, um Kollisionen mit `/api/orders/{id}` zu vermeiden.
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema:
 *           type: string
 *           format: date
 *         description: Stichtagsdatum (YYYY-MM-DD), dessen Kalenderwoche exportiert wird
 *         example: 2025-11-05
 *     responses:
 *       200:
 *         description: Datei erfolgreich generiert und als Download bereitgestellt
 *         headers:
 *           Cache-Control:
 *             schema:
 *               type: string
 *             description: no-store, no-cache, must-revalidate, proxy-revalidate
 *           Pragma:
 *             schema:
 *               type: string
 *             description: no-cache
 *           Expires:
 *             schema:
 *               type: string
 *             description: 0
 *           Surrogate-Control:
 *             schema:
 *               type: string
 *             description: no-store
 *           Content-Disposition:
 *             schema:
 *               type: string
 *             description: attachment; filename="<generierter-Dateiname>"
 *         content:
 *           application/octet-stream:
 *             schema:
 *               type: string
 *               format: binary
 *       400:
 *         description: Ungültiges Datum oder fehlender Parameter
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 *       500:
 *         description: Fehler beim Generieren oder Senden der Datei
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ErrorResponse'
 */
