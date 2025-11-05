const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');
const { log } = require('winston');

const router = express.Router();

/**
 * GET /api/foods
 * Liefert alle Gerichte einschließlich Bildinformationen (Base64-kodiert).
 */
router.get('/', async (req, res) => {
  try {
    logger.debug('Fetching all foods from database');
    const foods = await DBHandler.executeQuery(`
      SELECT f.ID, f.Name, f.Type, 
             p.ID as PictureID, p.Name as PictureName, p.MediaType, p.Bytes 
      FROM Foods f 
      JOIN PictureFiles p ON f.PictureID = p.ID
    `);

    const foodsWithImages = foods.map((food) => {
      const base64Image = food.Bytes
        ? Buffer.from(food.Bytes, 'binary').toString('base64')
        : null;

      return {
        ID: food.ID,
        Name: food.Name,
        Type: food.Type,
        Picture: {
          ID: food.PictureID,
          Name: food.PictureName,
          MediaType: food.MediaType,
          Base64: base64Image,
        },
      };
    });

    logger.info(`Fetched ${foodsWithImages.length} foods from database`);
    res.status(200).json(foodsWithImages);
  } catch (err) {
    logger.error(`Error fetching foods: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch foods' });
  }
});

/**
 * GET /api/foods/id/:id
 * Liefert ein Gericht basierend auf der ID.
 */
router.get('/id/:id', async (req, res) => {
  try {
    const { id } = req.params;

    logger.debug(`Fetching food by ID: ${id}`);
    const foods = await DBHandler.executeQuery(
      `
      SELECT f.ID, f.Name, f.Type, 
             p.ID as PictureID, p.Name as PictureName, p.MediaType, p.Bytes 
      FROM Foods f 
      JOIN PictureFiles p ON f.PictureID = p.ID
      WHERE f.ID = ?
    `,
      [id]
    );

    if (foods.length === 0) {
      logger.warn(`No food found with ID: ${id}`);
      return res.status(404).json({ error: 'Food not found' });
    }

    const food = foods[0];
    res.status(200).json({
      ID: food.ID,
      Name: food.Name,
      Type: food.Type,
      Picture: {
        ID: food.PictureID,
        Name: food.PictureName,
        MediaType: food.MediaType,
        Base64: Buffer.from(food.Bytes, 'binary').toString('base64'),
      },
    });
  } catch (err) {
    logger.error(`Error fetching food by ID: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch food by ID' });
  }
});

/**
 * GET /api/foods/type/:type
 * Liefert alle Gerichte eines bestimmten Typs.
 */
router.get('/type/:type', async (req, res) => {
  try {
    const { type } = req.params;

    logger.debug(`Fetching foods by type: ${type}`);
    const foods = await DBHandler.executeQuery(
      `
      SELECT f.ID, f.Name, f.Type, 
             p.ID as PictureID, p.Name as PictureName, p.MediaType, p.Bytes 
      FROM Foods f 
      JOIN PictureFiles p ON f.PictureID = p.ID
      WHERE f.Type = ?
    `,
      [type]
    );

    const foodsWithImages = foods.map((food) => ({
      ID: food.ID,
      Name: food.Name,
      Type: food.Type,
      Picture: {
        ID: food.PictureID,
        Name: food.PictureName,
        MediaType: food.MediaType,
        Base64: Buffer.from(food.Bytes, 'binary').toString('base64'),
      },
    }));

    logger.info(`Fetched ${foodsWithImages.length} foods of type: ${type}`);
    res.status(200).json(foodsWithImages);
  } catch (err) {
    logger.error(`Error fetching foods by type: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch foods by type' });
  }
});

/**
 * POST /api/foods
 * Erstellt ein neues Gericht mit einem verknüpften Bild.
 */
router.post('/', async (req, res) => {
  try {
    const { Name, Picture, Type, Allergens } = req.body;

    // Validierung der erforderlichen Felder
    if (!Name || !Type || !Picture || !Picture.Base64 || !Picture.Name || !Picture.MediaType) {
      const fehlendeFelder = [];
      if (!Name) fehlendeFelder.push('Name');
      if (!Picture) {
        fehlendeFelder.push('Picture');
      } else {
        if (!Picture.Base64) fehlendeFelder.push('Picture Base64');
        if (!Picture.Name) fehlendeFelder.push('Picture Name');
        if (!Picture.MediaType) fehlendeFelder.push('Picture MediaType');
      }
      if (!Type) fehlendeFelder.push('Type');
      if (fehlendeFelder.length) logger.error(`Missing required fields for creating food: ${fehlendeFelder.join(', ')}`);

      return res.status(400).json({ error: 'Missing required fields', missing: fehlendeFelder });
    }

    // Bild in der Datenbank speichern
    const pictureResult = await DBHandler.create('PictureFiles', {
      Bytes: Picture.Base64,
      Name: Picture.Name,
      MediaType: Picture.MediaType,
    });

    // Bild-ID erhalten
    const pictureID = pictureResult.insertId;

    logger.info(`Stored picture with ID: ${pictureID}`);

    // Gericht in der Datenbank speichern
    const foodResult = await DBHandler.create('Foods', {
      Name,
      PictureID: pictureID,
      Type,
    });

    logger.info(`Added new food: ${Name} with ID: ${foodResult.insertId}`);
    res.status(201).json({ message: 'Food created successfully', id: foodResult.insertId });
  } catch (err) {
    logger.error(`Error creating food: ${err.message}`);
    res.status(500).json({ error: 'Failed to create food' });
  }
});

/**
 * DELETE /api/foods/:id
 * Löscht das Gericht mit der angegebenen ID.
 */
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    // Gericht löschen
    const deletedFood = await DBHandler.delete('Foods', ' WHERE ID = ?', [id]);

    if (deletedFood) {
      logger.debug(`Deleted food with ID ${id}`);
      res.status(204).send();
    } else {
      logger.debug(`Food with ID ${id} not found`);
      res.status(404).json({ error: 'Food not found' });
    }
  } catch (err) {
    logger.error(`Error deleting food: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete food' });
  }
});

/**
 * GET /api/foods/name/:name
 * Liefert ein oder mehrere Gerichte basierend auf dem Namen.
 * Unterstützt den Parameter ?strict=true für exakte Suche.
 */
router.get('/name/:name', async (req, res) => {
  try {
    const { name } = req.params;
    const { strict } = req.query;

    logger.debug(`Fetching food by name: ${name} (strict: ${strict})`);

    // SQL-Bedingung basierend auf dem "strict"-Parameter
    const condition = strict === 'true'
      ? 'WHERE f.Name = ?' // Exakte Übereinstimmung
      : 'WHERE f.Name LIKE ?'; // Teilweise Übereinstimmung
    const queryParam = strict === 'true' ? name : `%${name}%`;

    const foods = await DBHandler.executeQuery(
      `
      SELECT f.ID, f.Name, f.Type, 
             p.ID as PictureID, p.Name as PictureName, p.MediaType, p.Bytes 
      FROM Foods f 
      JOIN PictureFiles p ON f.PictureID = p.ID
      ${condition}
    `,
      [queryParam]
    );

    if (foods.length === 0) {
      logger.warn(`No food found with name: ${name}`);
      return res.status(404).json({ error: 'Food not found' });
    }

    const foodsWithImages = foods.map((food) => ({
      ID: food.ID,
      Name: food.Name,
      Type: food.Type,
      Picture: {
        ID: food.PictureID,
        Name: food.PictureName,
        MediaType: food.MediaType,
        Base64: food.Bytes || null,
      },
    }));

    logger.info(
      `Fetched ${foodsWithImages.length} food(s) by name: ${name} (strict: ${strict})`
    );
    res.status(200).json(foodsWithImages);
  } catch (err) {
    logger.error(`Error fetching food by name: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch food by name' });
  }
});

/**
 * PATCH /api/foods/:id
 * Aktualisiert das Gericht mit der angegebenen ID.
 */
router.patch('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const { Name, Type } = req.body;

    // Validierung der erforderlichen Felder
    if (!Name && !Type) {
      logger.error('Missing required fields for updating food');
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // Gericht in der Datenbank aktualisieren
    const updatedFood = await DBHandler.update('Foods', { Name, Type }, ' WHERE ID = ?', [id]);

    if (updatedFood) {
      logger.debug(`Updated food with ID ${id}`);
      res.status(200).json({ message: 'Food updated successfully' });
    } else {
      logger.debug(`Food with ID ${id} not found`);
      res.status(404).json({ error: 'Food not found' });
    }
  } catch (err) {
    logger.error(`Error updating food: ${err.message}`);
    res.status(500).json({ error: 'Failed to update food' });
  }
});

module.exports = router;




/**
 * @swagger
 * /api/foods:
 *   get:
 *     summary: Gibt alle gespeicherten Gerichte zurück
 *     responses:
 *       200:
 *         description: Erfolgreich – Liste der Gerichte
 *       500:
 *         description: Serverfehler
 *
 * /api/foods:
 *   post:
 *     summary: Erstellt ein neues Gericht
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               Name:
 *                 type: string
 *               Type:
 *                 type: string
 *                 example: main
 *     responses:
 *       201:
 *         description: Gericht erfolgreich erstellt
 *       400:
 *         description: Ungültige Eingabe
 *       500:
 *         description: Serverfehler
 */
