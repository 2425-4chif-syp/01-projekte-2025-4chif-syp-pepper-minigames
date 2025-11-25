const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');

const router = express.Router();

/**
 * GET /api/residents
 * Holt alle Einträge aus der Tabelle "People".
 * Optional können die Ergebnisse durch Query-Parameter gefiltert werden.
 * Unterstützte Parameter: FirstName, LastName, DOB
 */
router.get('/', async (req, res) => {
  try {
    logger.debug('Fetching residents from database');

    // Query-Parameter extrahieren
    const { FirstName, LastName, DOB } = req.query;

    // Logge alle gesetzten Query-Parameter
    if (FirstName) logger.debug(`Filtering by FirstName: ${FirstName}`);
    if (LastName) logger.debug(`Filtering by LastName: ${LastName}`);
    if (DOB) logger.debug(`Filtering by DOB: ${DOB}`);

    // Bedingungen und Parameter für die Abfrage erstellen
    let conditions = '';
    const params = [];
    if (FirstName) {
      conditions += (conditions ? ' AND ' : ' WHERE ') + 'FirstName = ?';
      params.push(FirstName);
    }
    if (LastName) {
      conditions += (conditions ? ' AND ' : ' WHERE ') + 'LastName = ?';
      params.push(LastName);
    }
    if (DOB) {
      conditions += (conditions ? ' AND ' : ' WHERE ') + 'DOB = ?';
      params.push(`${DOB}`);
    }

    // Datenbankabfrage ausführen
    const people = await DBHandler.read('People', conditions, params);

    const sanitizedPeople = people.map(person => ({
      ...person,
      DOB: person.DOB
        ? typeof person.DOB === 'string'
          ? person.DOB.split('T')[0]
          : new Date(person.DOB).toISOString().split('T')[0]
        : null,
    }));

    logger.info(`Fetched ${sanitizedPeople.length} residents from database`);
    // Ergebnis zurückgeben
    res.status(200).json(sanitizedPeople);
  } catch (err) {
    // Fehler loggen und 500 zurückgeben
    logger.error(`Error fetching residents: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch residents' });
  }
});

/**
 * DELETE /api/residents/:id
 * Löscht den Bewohner mit der angegebenen ID.
 */
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    // Person löschen
    const deletedPerson = await DBHandler.delete('People', ' WHERE ID = ?', [id]);

    if (deletedPerson) {
      logger.debug(`Deleted resident with ID ${id}`);
      res.status(204).send();
    } else {
      logger.debug(`Resident with ID ${id} not found`);
      res.status(404).json({ error: 'Resident not found' });
    }
  } catch (err) {
    logger.error(`Error deleting resident: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete resident' });
  }
});

/**
 * GET /api/residents/count
 * Gibt die Anzahl der Bewohner zurück.
 */
router.get('/count', async (req, res) => {
  try {
    // Anzahl der Bewohner abfragen
    const count = await DBHandler.count('People');

    logger.info(`Fetched count of residents: ${count}`);
    res.status(200).json({ count });
  } catch (err) {
    logger.error(`Error fetching count of residents: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch count of residents' });
  }
});

router.post('/', async (req, res) => {
  try {
    const { FirstName, LastName, DOB } = req.body;

    logger.info(`Adding resident: ${FirstName} ${LastName} ${DOB}`); // DEBUG: Log the attempt to add a resident

    // Check if required fields are provided
    if (!FirstName || !LastName) {
      logger.error('Missing required fields: FirstName or LastName');
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // Check if resident with same FirstName and LastName exists
    const existingPersons = await DBHandler.read('People', ' WHERE FirstName = ? AND LastName = ?', [FirstName, LastName]);

    // If resident already exists
    if (existingPersons.length > 0) {
      // If DOB is not provided, return error 400
      if (!DOB) {
        logger.error('Resident already exists and DOB is not provided');
        return res.status(400).json({ error: 'Resident already exists and DOB is required to distinguish' });
      }

      // If DOB is provided, check if resident with same FirstName, LastName, and DOB exists
      const personWithDOB = await DBHandler.read('People', ' WHERE FirstName = ? AND LastName = ? AND DOB = ?', [FirstName, LastName, DOB]);

      if (personWithDOB.length > 0) {
        logger.error('Resident with same DOB already exists');
        return res.status(400).json({ error: 'Resident already exists' });
      }
    }

    // Add new person with FirstName, LastName, and DOB (if provided)
    const newPerson = await DBHandler.create('People', { FirstName, LastName, DOB });

    logger.info(`Added resident: ${FirstName} ${LastName}`);
    return res.status(201).json(newPerson.insertId);
  } catch (err) {
    logger.error(`Error adding resident: ${err.message}`);
    return res.status(500).json({ error: 'Failed to add resident' });
  }
});

module.exports = router;

/**
 * @swagger
 * tags:
 *   - name: Residents
 *     description: Verwaltung der Bewohner (People)
 *
 * components:
 *   schemas:
 *     Resident:
 *       type: object
 *       properties:
 *         ID:
 *           type: integer
 *           example: 42
 *         FirstName:
 *           type: string
 *           example: Max
 *         LastName:
 *           type: string
 *           example: Mustermann
 *         DOB:
 *           type: string
 *           format: date
 *           nullable: true
 *           example: 2001-05-17
 *     ResidentCreate:
 *       type: object
 *       required: [FirstName, LastName]
 *       properties:
 *         FirstName:
 *           type: string
 *           example: Anna
 *         LastName:
 *           type: string
 *           example: Schmidt
 *         DOB:
 *           type: string
 *           format: date
 *           description: Geburtsdatum (optional, aber benötigt wenn es bereits gleichnamige Personen gibt)
 *           example: 1999-12-31
 *     ResidentIdResponse:
 *       type: integer
 *       example: 123
 *     CountResponse:
 *       type: object
 *       properties:
 *         count:
 *           type: integer
 *           example: 87
 *
 * /api/residents:
 *   get:
 *     tags: [Residents]
 *     summary: Holt alle Bewohner (optional filterbar über Query-Parameter)
 *     parameters:
 *       - in: query
 *         name: FirstName
 *         schema:
 *           type: string
 *         description: Filter nach Vorname (exakte Übereinstimmung)
 *         example: Max
 *       - in: query
 *         name: LastName
 *         schema:
 *           type: string
 *         description: Filter nach Nachname (exakte Übereinstimmung)
 *         example: Mustermann
 *       - in: query
 *         name: DOB
 *         schema:
 *           type: string
 *           format: date
 *         description: Filter nach Geburtsdatum (YYYY-MM-DD)
 *         example: 2001-05-17
 *     responses:
 *       200:
 *         description: Liste der gefundenen Bewohner
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Resident'
 *       500:
 *         description: Serverfehler
 *   post:
 *     tags: [Residents]
 *     summary: Legt einen neuen Bewohner an
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/ResidentCreate'
 *     responses:
 *       201:
 *         description: Bewohner erfolgreich angelegt (gibt insertId zurück)
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ResidentIdResponse'
 *       400:
 *         description: Ungültige Eingabe oder Bewohner existiert bereits (ggf. DOB erforderlich)
 *       500:
 *         description: Serverfehler
 *
 * /api/residents/{id}:
 *   delete:
 *     tags: [Residents]
 *     summary: Löscht den Bewohner mit der angegebenen ID
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *         description: Primärschlüssel der Person
 *         example: 42
 *     responses:
 *       204:
 *         description: Erfolgreich gelöscht (kein Inhalt)
 *       404:
 *         description: Bewohner nicht gefunden
 *       500:
 *         description: Serverfehler
 *
 * /api/residents/count:
 *   get:
 *     tags: [Residents]
 *     summary: Gibt die Anzahl aller Bewohner zurück
 *     responses:
 *       200:
 *         description: Anzahl der Bewohner
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/CountResponse'
 *       500:
 *         description: Serverfehler
 */
