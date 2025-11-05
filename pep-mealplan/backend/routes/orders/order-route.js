const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');
const dayjs = require('dayjs');
const utc = require('dayjs/plugin/utc');
const timezone = require('dayjs/plugin/timezone');
const { getReferenceWeekFromDate } = require('../../utils/referenceWeek');

dayjs.extend(utc);
dayjs.extend(timezone);

const router = express.Router();

/**
 * GET /api/orders
 * Holt alle Bestellungen inklusive der zugehörigen Details.
 */
router.get('/', async (req, res) => {
  try {
    logger.debug('Fetching all orders from database');

    const orders = await DBHandler.executeQuery('SELECT * FROM Orders');
    const enrichedOrders = await Promise.all(orders.map(enrichOrder));

    logger.debug(`Fetched ${enrichedOrders.length} orders`);
    res.status(200).json(enrichedOrders);
  } catch (err) {
    logger.error(`Error fetching orders: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch orders' });
  }
});

/**
 * GET /api/orders/:id
 * Holt eine Bestellung nach ihrer ID inklusive der zugehörigen Details.
 */
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    logger.debug(`Fetching order with ID: ${id}`);
    const order = await DBHandler.executeQuery('SELECT * FROM Orders WHERE ID = ?', [id]);

    if (order.length === 0) {
      logger.debug(`Order with ID ${id} not found`);
      return res.status(404).json({ error: 'Order not found' });
    }

    const enrichedOrder = await enrichOrder(order[0]);
    logger.debug(`Fetched order with ID: ${id}`);
    res.status(200).json(enrichedOrder);
  } catch (err) {
    logger.error(`Error fetching order: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch order' });
  }
});

/**
 * POST /api/orders
 * Erstellt eine neue Bestellung.
 */
router.post('/', async (req, res) => {
  try {
    const { Date, UserID, MenuID, DessertSelected, SelectedLunchID, SelectedDinnerID } = req.body;

    // Validierung der Felder
    if (!Date || !UserID || !MenuID) {
      logger.error(`Missing required fields only got: ${JSON.stringify(req.body)}`);
      return res.status(400).json({ error: 'Missing required fields' });
    }

    // if DessertSelected is not provided, set it to false
    if (DessertSelected === undefined || DessertSelected === null) {
      DessertSelected = false;
    }

    const viennaTime = dayjs().tz('Europe/Vienna').format('YYYY-MM-DD HH:mm:ss');

    // Neue Bestellung einfügen
    const result = await DBHandler.create('Orders', {
      Date,
      UserID,
      MenuID,
      DessertSelected,
      SelectedLunchID,
      SelectedDinnerID,
      ordered_at: viennaTime,
    });

    logger.info(`Created new order with ID: ${result.insertId}`);
    res.status(201).json({ message: 'Order created successfully', orderId: result.insertId, user: UserID, orderedAt: viennaTime });
  } catch (err) {
    logger.error(`Error creating order: ${err.message}`);
    res.status(500).json({ error: 'Failed to create order' });
  }
});

/**
 * DELETE /api/orders/:id
 * Löscht eine Bestellung nach ihrer ID.
 */
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    logger.debug(`Deleting order with ID: ${id}`);
    const result = await DBHandler.executeQuery('DELETE FROM Orders WHERE ID = ?', [id]);

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Order not found' });
    }

    logger.info(`Deleted order with ID: ${id}`);
    res.status(200).json({ message: 'Order deleted successfully' });
  } catch (err) {
    logger.error(`Error deleting order: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete order' });
  }
});

/**
 * GET /api/orders/date/:date
 * Holt alle Bestellungen für ein bestimmtes Datum inklusive Details.
 */
router.get('/date/:date', async (req, res) => {
    try {
      const { date } = req.params;
  
      logger.debug(`Fetching all orders for date: ${date}`);
  
      // Alle Bestellungen für das angegebene Datum abrufen
      const orders = await DBHandler.executeQuery('SELECT * FROM Orders WHERE Date = ?', [date]);
  
      // Auch wenn keine Bestellungen gefunden wurden, eine leere Liste zurückgeben
      if (orders.length === 0) {
        logger.debug(`No orders found for date: ${date}`);
        return res.status(200).json([]);
      }

      // Alle Bestellungen anreichern
      const enrichedOrders = await Promise.all(orders.map(enrichOrder));

      logger.debug(`Fetched ${enrichedOrders.length} orders for date: ${date}`);
      res.status(200).json(enrichedOrders);
    } catch (err) {
      logger.error(`Error fetching orders for date: ${err.message}`);
      res.status(500).json({ error: 'Failed to fetch orders for the specified date' });
    }
  });

/**
 * PUT /api/orders/by-user-date
 * Erstellt oder aktualisiert eine Bestellung für einen Benutzer an einem Datum.
 */
router.put('/by-user-date', async (req, res) => {
  try {
    const { Date: orderDate, UserID, DessertSelected, SelectedLunchID, SelectedDinnerID } = req.body;

    if (!orderDate || !UserID) {
      logger.error(`Missing required fields only got: ${JSON.stringify(req.body)}`);
      return res.status(400).json({ error: 'Missing required fields' });
    }

    const weekNumber = await getReferenceWeekFromDate(new Date(orderDate));
    const jsDay = new Date(orderDate).getDay();
    const weekDay = jsDay === 0 ? 6 : jsDay - 1;

    const [menu] = await DBHandler.executeQuery(
      'SELECT ID FROM Menu WHERE WeekNumber = ? AND Weekday = ?',
      [weekNumber, weekDay]
    );

    if (!menu) {
      logger.error(`Menu not found for date ${orderDate}`);
      return res.status(404).json({ error: 'Menu not found for date' });
    }

    const [existing] = await DBHandler.executeQuery(
      'SELECT ID FROM Orders WHERE UserID = ? AND Date = ?',
      [UserID, orderDate]
    );

    const dessert = DessertSelected ? 1 : 0;

    if (existing) {
      await DBHandler.update(
        'Orders',
        {
          MenuID: menu.ID,
          DessertSelected: dessert,
          SelectedLunchID,
          SelectedDinnerID,
        },
        'WHERE ID = ?',
        [existing.ID]
      );
      logger.info(`Updated order ${existing.ID} for user ${UserID} on ${orderDate}`);
      return res.status(200).json({ message: 'Order updated', orderId: existing.ID });
    } else {
      const viennaTime = dayjs().tz('Europe/Vienna').format('YYYY-MM-DD HH:mm:ss');
      const result = await DBHandler.create('Orders', {
        Date: orderDate,
        UserID,
        MenuID: menu.ID,
        DessertSelected: dessert,
        SelectedLunchID,
        SelectedDinnerID,
        ordered_at: viennaTime,
      });
      logger.info(`Created new order with ID: ${result.insertId}`);
      return res.status(201).json({ message: 'Order created', orderId: result.insertId, orderedAt: viennaTime });
    }
  } catch (err) {
    logger.error(`Error upserting order: ${err.message}`);
    res.status(500).json({ error: 'Failed to save order' });
  }
});

/**
 * Hilfsfunktion: Löst die Fremdschlüssel auf und fügt Inhalte (inkl. Details) hinzu.
 */
async function enrichOrder(order) {
    // Details zum Benutzer abrufen
    const [userDetails] = await DBHandler.executeQuery(
      'SELECT * FROM People WHERE ID = ?',
      [order.UserID]
    );
  
    // Details zum Menü abrufen
    const [menuDetails] = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE ID = ?',
      [order.MenuID]
    );

    // IDs der Gerichte (Lunch, Dinner, Soup, LunchDessert) sammeln
    const foodIds = [
      order.SelectedLunchID,
      order.SelectedDinnerID,
      menuDetails?.SoupID,        // SoupID aus dem Menü
      menuDetails?.LunchDessertID // LunchDessertID aus dem Menü
    ].filter((id) => id); // IDs, die nicht null oder undefined sind
  
    // Details zu den Gerichten abrufen
    let foodDetails = [];
    if (foodIds.length > 0) {
      const placeholders = foodIds.map(() => '?').join(',');
      const sql = `SELECT f.ID, f.Name, f.Type, p.Name as PictureName, p.MediaType, p.Bytes
       FROM Foods f
       JOIN PictureFiles p ON f.PictureID = p.ID
       WHERE f.ID IN (${placeholders})`;
      foodDetails = await DBHandler.executeQuery(sql, foodIds);
    }
  
    // Foods-Details nach ID indizieren
    const foodsById = foodDetails.reduce((acc, food) => {
      const base64 = Buffer.isBuffer(food.Bytes)
        ? food.Bytes.toString('base64')
        : food.Bytes;
      acc[food.ID] = {
        Name: food.Name,
        Type: food.Type,
        Picture: {
          Name: food.PictureName,
          MediaType: food.MediaType,
          Base64: base64,
        },
      };
      return acc;
    }, {});
  
    return {
      ID: order.ID,
      Date: dayjs(order.Date).format('YYYY-MM-DD'),
      User: userDetails || null,
      Menu: menuDetails || null,
      DessertSelected: Boolean(order.DessertSelected),
      SelectedLunchID: order.SelectedLunchID ?? null,
      SelectedDinnerID: order.SelectedDinnerID ?? null,
      SelectedLunch: foodsById[order.SelectedLunchID] || null,
      SelectedDinner: foodsById[order.SelectedDinnerID] || null,
      Soup: foodsById[menuDetails?.SoupID] || null,
      LunchDessert: foodsById[menuDetails?.LunchDessertID] || null,
      OrderedAt: dayjs(order.ordered_at).tz('Europe/Vienna').format('YYYY-MM-DD_HH:mm:ss'),
    };
  }

module.exports = router;