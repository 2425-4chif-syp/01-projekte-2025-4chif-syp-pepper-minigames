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
 * tags:
 *   - name: Foods
 *     description: Verwaltung der Gerichte inkl. Bilder
 *
 * components:
 *   schemas:
 *     Picture:
 *       type: object
 *       properties:
 *         ID:
 *           type: integer
 *           example: 17
 *         Name:
 *           type: string
 *           example: "tomato_soup.jpg"
 *         MediaType:
 *           type: string
 *           example: "image/jpeg"
 *         Base64:
 *           type: string
 *           description: Base64-kodierte Bilddaten
 *           example: "/9j/4AAQSkZJRgABAQAAAQABAAD…"
 *
 *     Food:
 *       type: object
 *       properties:
 *         ID:
 *           type: integer
 *           example: 3
 *         Name:
 *           type: string
 *           example: "Tomatensuppe"
 *         Type:
 *           type: string
 *           description: Kategorie (z. B. soup, main, dessert, appetizer)
 *           example: "soup"
 *         Picture:
 *           $ref: '#/components/schemas/Picture'
 *
 *     FoodCreateRequest:
 *       type: object
 *       required: [Name, Type, Picture]
 *       properties:
 *         Name:
 *           type: string
 *           example: "Caesar Salad"
 *         Type:
 *           type: string
 *           example: "appetizer"
 *         Allergens:
 *           type: string
 *           nullable: true
 *           description: Optionale Allergen-Information (falls verwendet)
 *           example: "A,C,G"
 *         Picture:
 *           type: object
 *           required: [Base64, Name, MediaType]
 *           properties:
 *             Base64:
 *               type: string
 *               description: Base64-kodierte Bilddaten
 *             Name:
 *               type: string
 *               example: "caesar.jpg"
 *             MediaType:
 *               type: string
 *               example: "image/jpeg"
 *
 *     FoodUpdateRequest:
 *       type: object
 *       properties:
 *         Name:
 *           type: string
 *           example: "Spaghetti Bolognese"
 *         Type:
 *           type: string
 *           example: "main"
 *
 * /api/foods:
 *   get:
 *     tags: [Foods]
 *     summary: Liefert alle Gerichte einschließlich Bildinformationen (Base64)
 *     responses:
 *       200:
 *         description: Liste der Gerichte
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items: { $ref: '#/components/schemas/Food' }
 *       500:
 *         description: Serverfehler
 *   post:
 *     tags: [Foods]
 *     summary: Erstellt ein neues Gericht mit verknüpftem Bild
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema: { $ref: '#/components/schemas/FoodCreateRequest' }
 *     responses:
 *       201:
 *         description: Gericht erfolgreich erstellt
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message: { type: string, example: "Food created successfully" }
 *                 id: { type: integer, example: 42 }
 *       400:
 *         description: Fehlende/ungültige Felder
 *       500:
 *         description: Serverfehler
 *
 * /api/foods/id/{id}:
 *   get:
 *     tags: [Foods]
 *     summary: Liefert ein Gericht anhand der ID
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema: { type: integer }
 *         example: 5
 *     responses:
 *       200:
 *         description: Gericht gefunden
 *         content:
 *           application/json:
 *             schema: { $ref: '#/components/schemas/Food' }
 *       404:
 *         description: Nicht gefunden
 *       500:
 *         description: Serverfehler
 *
 * /api/foods/type/{type}:
 *   get:
 *     tags: [Foods]
 *     summary: Liefert alle Gerichte eines Typs
 *     parameters:
 *       - in: path
 *         name: type
 *         required: true
 *         schema: { type: string }
 *         example: "main"
 *     responses:
 *       200:
 *         description: Liste der Gerichte für den Typ
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items: { $ref: '#/components/schemas/Food' }
 *       500:
 *         description: Serverfehler
 *
 * /api/foods/name/{name}:
 *   get:
 *     tags: [Foods]
 *     summary: Sucht Gerichte nach Name (optional exakt)
 *     parameters:
 *       - in: path
 *         name: name
 *         required: true
 *         schema: { type: string }
 *         example: "Schnitzel"
 *       - in: query
 *         name: strict
 *         required: false
 *         schema: { type: boolean }
 *         description: true = exakte Übereinstimmung, sonst LIKE-Suche
 *         example: true
 *     responses:
 *       200:
 *         description: Trefferliste (mind. 1 Element)
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items: { $ref: '#/components/schemas/Food' }
 *       404:
 *         description: Keine Gerichte gefunden
 *       500:
 *         description: Serverfehler
 *
 * /api/foods/{id}:
 *   patch:
 *     tags: [Foods]
 *     summary: Aktualisiert ein Gericht
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema: { type: integer }
 *         example: 8
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema: { $ref: '#/components/schemas/FoodUpdateRequest' }
 *     responses:
 *       200:
 *         description: Aktualisierung erfolgreich
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message: { type: string, example: "Food updated successfully" }
 *       400:
 *         description: Keine gültigen Felder übergeben
 *       404:
 *         description: Nicht gefunden
 *       500:
 *         description: Serverfehler
 *   delete:
 *     tags: [Foods]
 *     summary: Löscht ein Gericht
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema: { type: integer }
 *         example: 8
 *     responses:
 *       204:
 *         description: Erfolgreich gelöscht (kein Inhalt)
 *       404:
 *         description: Nicht gefunden
 *       500:
 *         description: Serverfehler
 */

