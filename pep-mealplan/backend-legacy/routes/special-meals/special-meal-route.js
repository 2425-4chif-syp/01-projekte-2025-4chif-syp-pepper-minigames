const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');
const dayjs = require('dayjs');

const router = express.Router();

/**
 * GET /api/special-meals
 * Holt alle SpecialMeals inklusive der Inhalte (mit Bildern).
 */
router.get('/', async (req, res) => {
  try {
    logger.debug('Fetching all special meals from database');

    const specialMeals = await DBHandler.executeQuery('SELECT * FROM SpecialMeals');
    const enrichedMeals = await Promise.all(specialMeals.map(enrichSpecialMeal));

    logger.debug(`Fetched ${enrichedMeals.length} special meals`);

    res.status(200).json(enrichedMeals);
  } catch (err) {
    logger.error(`Error fetching special meals: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch special meals' });
  }
});

/**
 * GET /api/special-meals/:date
 * Holt ein SpecialMeal für ein bestimmtes Datum inklusive der Inhalte (mit Bildern).
 */
router.get('/:date', async (req, res) => {
  try {
    const { date } = req.params;

    logger.debug(`Fetching special meal for date: ${date}`);

    const [meal] = await DBHandler.executeQuery(
      `SELECT 
         Date, 
         SoupID, 
         M1ID, 
         M2ID, 
         LunchDessertID, 
         A1ID, 
         A2ID
       FROM SpecialMeals
       WHERE DATE(Date) = ?`,
      [date]
    );

    if (!meal) {
      logger.debug(`Special meal not found for date: ${date}`);
      return res
        .status(404)
        .json({ error: 'Special meal not found for the specified date' });
    }

    logger.debug(`Fetched special meal for date: ${date}`);
    logger.debug(`Meal details: ${JSON.stringify(meal)}`);

    const originalExec = DBHandler.executeQuery.bind(DBHandler);
    DBHandler.executeQuery = async (sql, params) => {
      if (
        typeof sql === 'string' &&
        sql.includes('FROM Foods f') &&
        Array.isArray(params) &&
        params.length === 1 &&
        Array.isArray(params[0])
      ) {
        const ids = params[0];
        const placeholders = ids.map(() => '?').join(', ');
        const newSql = sql.replace('IN (?)', `IN (${placeholders})`);
        return originalExec(newSql, ids);
      }
      return originalExec(sql, params);
    };

    const enrichedMeal = await enrichSpecialMeal(meal);
    DBHandler.executeQuery = originalExec;

    logger.debug(`Enriched meal details: ${JSON.stringify(enrichedMeal)}`);
    res.status(200).json(enrichedMeal);
  } catch (err) {
    logger.error(`Error fetching special meal for date: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch special meal' });
  }
});

/**
 * POST /api/special-meals
 * Erstellt ein neues SpecialMeal für ein bestimmtes Datum.
 */
router.post('/', async (req, res) => {
  try {
    const { Date, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID } = req.body;

    // Validierung der Felder
    if (!Date || !SoupID || !M1ID || !M2ID || !LunchDessertID || !A1ID || !A2ID) {
      logger.error(`Missing required fields only got: ${JSON.stringify(req.body)}`);
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // Prüfen, ob bereits ein Eintrag für das Datum existiert
    const existingMeal = await DBHandler.executeQuery(
      'SELECT * FROM SpecialMeals WHERE Date = ?',
      [Date]
    );

    if (existingMeal.length > 0) {
      logger.debug(`Special meal already exists for date: ${Date}`);
      return res.status(400).json({ error: 'Special meal already exists for the specified date' });
    }

    // Neues SpecialMeal einfügen
    await DBHandler.create('SpecialMeals', {
      Date,
      SoupID,
      M1ID,
      M2ID,
      LunchDessertID,
      A1ID,
      A2ID,
    });

    logger.info(`Created new special meal for date: ${Date}`);
    res.status(201).json({ message: 'Special meal created successfully' });
  } catch (err) {
    logger.error(`Error creating special meal: ${err.message}`);
    res.status(500).json({ error: 'Failed to create special meal' });
  }
});

/**
 * DELETE /api/special-meals/:id
 * Löscht ein SpecialMeal nach ID
 */

router.delete('/:date', async (req, res) => {
  try {
    const { date } = req.params;

    const existingMeal = await DBHandler.executeQuery(
      'SELECT * FROM SpecialMeals WHERE Date = ?',
      [date]
    );

    if (existingMeal.length === 0) {
      logger.debug(`Special meal not found for date: ${date}`);
      return res.status(404).json({ error: 'Special meal not found for the specified date' });
    }

    await DBHandler.delete('SpecialMeals', 'WHERE Date = ?', [date]);

    logger.info(`Deleted special meal for date: ${date}`);
    res.status(200).json({ message: 'Special meal deleted successfully' });
  } catch (err) {
    logger.error(`Error deleting special meal: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete special meal' });
  }
});

/**
 * PUT /api/special-meals/:date
 * Erstellt oder aktualisiert ein SpecialMeal für ein bestimmtes Datum.
 */
router.put('/:date', async (req, res) => {
  try {
    const { date } = req.params;
    const { SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID } = req.body;

    if (!SoupID || !M1ID || !M2ID || !LunchDessertID || !A1ID || !A2ID) {
      logger.error(`Missing required fields for date ${date}`);
      return res.status(400).json({ error: 'Missing required fields' });
    }

    const existingMeal = await DBHandler.executeQuery(
      'SELECT * FROM SpecialMeals WHERE Date = ?',
      [date]
    );

    if (existingMeal.length > 0) {
      await DBHandler.update(
        'SpecialMeals',
        { SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID },
        'WHERE Date = ?',
        [date]
      );
      logger.info(`Updated special meal for date: ${date}`);
      return res.status(200).json({ message: 'Special meal updated successfully' });
    }

    await DBHandler.create('SpecialMeals', {
      Date: date,
      SoupID,
      M1ID,
      M2ID,
      LunchDessertID,
      A1ID,
      A2ID,
    });
    logger.info(`Created new special meal for date: ${date}`);
    res.status(201).json({ message: 'Special meal created successfully' });
  } catch (err) {
    logger.error(`Error upserting special meal: ${err.message}`);
    res.status(500).json({ error: 'Failed to save special meal' });
  }
});

/**
 * Hilfsfunktion: Löst die Fremdschlüssel auf und fügt Inhalte (inkl. Bilder) hinzu.
 */
async function enrichSpecialMeal(meal) {
  const foodIds = [
    meal.SoupID,
    meal.M1ID,
    meal.M2ID,
    meal.LunchDessertID,
    meal.A1ID,
    meal.A2ID,
  ];

  logger.debug(`Fetching food details for IDs: ${foodIds.join(', ')}`);

  const foodDetails = await DBHandler.executeQuery(
    `SELECT f.ID, f.Name, f.Type, p.Name as PictureName, p.MediaType, p.Bytes
     FROM Foods f
     JOIN PictureFiles p ON f.PictureID = p.ID
     WHERE f.ID IN (?)`,
    [foodIds]
  );

  const foodsById = foodDetails.reduce((acc, food) => {
    acc[food.ID] = {
      Name: food.Name,
      Type: food.Type,
      Picture: {
        Name: food.PictureName,
        MediaType: food.MediaType,
        Base64: Buffer.from(food.Bytes, 'binary').toString('base64'),
      },
    };
    return acc;
  }, {});

  return {
    Date: dayjs(meal.Date).format('YYYY-MM-DD'),
    Soup: foodsById[meal.SoupID] || null,
    Lunch1: foodsById[meal.M1ID] || null,
    Lunch2: foodsById[meal.M2ID] || null,
    LunchDessert: foodsById[meal.LunchDessertID] || null,
    Dinner1: foodsById[meal.A1ID] || null,
    Dinner2: foodsById[meal.A2ID] || null,
  };
}

module.exports = router;

/**
 * @swagger
 * tags:
 *   - name: Special Meals
 *     description: Verwaltung der SpecialMeals
 *
 * components:
 *   schemas:
 *     Picture:
 *       type: object
 *       properties:
 *         Name:
 *           type: string
 *           example: tomato_soup.jpg
 *         MediaType:
 *           type: string
 *           example: image/jpeg
 *         Base64:
 *           type: string
 *           description: Base64-kodierte Bilddaten
 *     FoodWithPicture:
 *       type: object
 *       properties:
 *         Name:
 *           type: string
 *           example: Tomatensuppe
 *         Type:
 *           type: string
 *           description: Kategorie des Gerichts (z.B. soup, main, dessert, appetizer)
 *           example: soup
 *         Picture:
 *           $ref: '#/components/schemas/Picture'
 *     SpecialMealCreate:
 *       type: object
 *       required: [Date, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID]
 *       properties:
 *         Date:
 *           type: string
 *           format: date
 *           example: 2025-11-05
 *         SoupID:
 *           type: integer
 *           example: 1
 *         M1ID:
 *           type: integer
 *           example: 10
 *         M2ID:
 *           type: integer
 *           example: 11
 *         LunchDessertID:
 *           type: integer
 *           example: 20
 *         A1ID:
 *           type: integer
 *           example: 30
 *         A2ID:
 *           type: integer
 *           example: 31
 *     SpecialMealUpdate:
 *       type: object
 *       required: [SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID]
 *       properties:
 *         SoupID:
 *           type: integer
 *         M1ID:
 *           type: integer
 *         M2ID:
 *           type: integer
 *         LunchDessertID:
 *           type: integer
 *         A1ID:
 *           type: integer
 *         A2ID:
 *           type: integer
 *     SpecialMealEnriched:
 *       type: object
 *       properties:
 *         Date:
 *           type: string
 *           format: date
 *           example: 2025-11-05
 *         Soup:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *         Lunch1:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *         Lunch2:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *         LunchDessert:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *         Dinner1:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *         Dinner2:
 *           $ref: '#/components/schemas/FoodWithPicture'
 *
 * /api/special-meals:
 *   get:
 *     tags: [Special Meals]
 *     summary: Holt alle SpecialMeals inkl. Inhalte (mit Bildern)
 *     responses:
 *       200:
 *         description: Erfolgreich – Liste der SpecialMeals
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/SpecialMealEnriched'
 *       500:
 *         description: Serverfehler
 *   post:
 *     tags: [Special Meals]
 *     summary: Erstellt ein neues SpecialMeal für ein Datum
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/SpecialMealCreate'
 *     responses:
 *       201:
 *         description: SpecialMeal erfolgreich erstellt
 *       400:
 *         description: Ungültige oder fehlende Felder / Eintrag für Datum existiert bereits
 *       500:
 *         description: Serverfehler
 *
 * /api/special-meals/{date}:
 *   get:
 *     tags: [Special Meals]
 *     summary: Holt ein SpecialMeal für ein bestimmtes Datum inkl. Inhalte (mit Bildern)
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema:
 *           type: string
 *           format: date
 *         description: Datum im Format YYYY-MM-DD
 *     responses:
 *       200:
 *         description: Erfolgreich – SpecialMeal für das Datum
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/SpecialMealEnriched'
 *       404:
 *         description: Kein SpecialMeal für das Datum gefunden
 *       500:
 *         description: Serverfehler
 *   put:
 *     tags: [Special Meals]
 *     summary: Erstellt oder aktualisiert ein SpecialMeal für ein bestimmtes Datum (Upsert)
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema:
 *           type: string
 *           format: date
 *         description: Datum im Format YYYY-MM-DD
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/SpecialMealUpdate'
 *     responses:
 *       200:
 *         description: SpecialMeal aktualisiert
 *       201:
 *         description: SpecialMeal neu erstellt
 *       400:
 *         description: Ungültige oder fehlende Felder
 *       500:
 *         description: Serverfehler
 *   delete:
 *     tags: [Special Meals]
 *     summary: Löscht ein SpecialMeal für ein bestimmtes Datum
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema:
 *           type: string
 *           format: date
 *         description: Datum im Format YYYY-MM-DD
 *     responses:
 *       200:
 *         description: SpecialMeal erfolgreich gelöscht
 *       404:
 *         description: Kein SpecialMeal für das Datum gefunden
 *       500:
 *         description: Serverfehler
 */
