const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');

const router = express.Router();

/**
 * GET /api/allergens
 * Liefert alle Allergene (Shortname und Fullname).
 */
router.get('/', async (req, res) => {
  try {
    logger.debug('Fetching all allergens from database');
    const allergens = await DBHandler.read('Allergens'); // Liest alle Einträge aus der Tabelle

    logger.info(`Fetched ${allergens.length} allergens from database`);
    res.status(200).json(allergens);
  } catch (err) {
    logger.error(`Error fetching allergens: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch allergens' });
  }
});

/**
 * GET /api/allergens/:shortname
 * Liefert ein einzelnes Allergen basierend auf dem Shortname.
 */
router.get('/:shortname', async (req, res) => {
  try {
    const { shortname } = req.params;

    // Überprüfen, ob der Shortname bereitgestellt wurde
    if (!shortname) {
      logger.error('Shortname is required');
      return res.status(400).json({ error: 'Shortname is required' });
    }

    logger.debug(`Fetching allergen with Shortname: ${shortname}`);
    const allergen = await DBHandler.read(
      'Allergens',
      ' WHERE Shortname = ?',
      [shortname]
    );

    if (allergen.length === 0) {
      logger.warn(`Allergen with Shortname "${shortname}" not found`);
      return res.status(404).json({ error: 'Allergen not found' });
    }

    logger.info(`Fetched allergen with Shortname: ${shortname}`);
    res.status(200).json(allergen[0]);
  } catch (err) {
    logger.error(`Error fetching allergen: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch allergen' });
  }
});

module.exports = router;



/**
 * @swagger
 * /api/allergens:
 *   get:
 *     summary: Gibt alle Allergene zurück
 *     responses:
 *       200:
 *         description: Erfolgreich – Liste der Allergene
 *       500:
 *         description: Serverfehler
 */
