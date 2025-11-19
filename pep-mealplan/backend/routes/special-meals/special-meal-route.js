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