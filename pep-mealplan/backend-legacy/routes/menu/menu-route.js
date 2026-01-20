const express = require('express');
const DBHandler = require('../../DBHandler');
const logger = require('../../utils/logger');
const { getReferenceWeek, getReferenceWeekFromDate } = require('../../utils/referenceWeek');
const dayjs = require('dayjs');

const router = express.Router();

/**
 * POST /api/menu/week
 * Setzt das Menü für eine ganze Woche anhand der WeekNumber.
 * Erwarteter Request-Body:
 * {
 *   "WeekNumber": <number>,
 *   "menus": [
 *     {
 *       "WeekDay": <number>,
 *       "SoupID": <number>,
 *       "M1ID": <number>,
 *       "M2ID": <number>,
 *       "LunchDessertID": <number>,
 *       "A1ID": <number>,
 *       "A2ID": <number>
 *     },
 *     // ... weitere Tage
 *   ]
 * }
 */
router.post('/week', async (req, res) => {
  try {
    const { WeekNumber, menus } = req.body;

    // Validierung des WeekNumber und des Menüs-Arrays
    if (WeekNumber === undefined || !Array.isArray(menus) || menus.length === 0) {
      logger.error(`Missing required fields, got: ${JSON.stringify(req.body)}`);
      return res.status(400).json({ error: 'Missing required fields: WeekNumber und ein nicht-leeres menus-Array sind erforderlich.' });
    }

    // Überprüfe jeden Menü-Eintrag im Array
    for (const menu of menus) {
      const { WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID } = menu;
      if (
        WeekDay === undefined ||
        SoupID === undefined ||
        M1ID === undefined ||
        M2ID === undefined ||
        LunchDessertID === undefined ||
        A1ID === undefined ||
        A2ID === undefined
      ) {
        logger.error(`Missing required fields in menu entry: ${JSON.stringify(menu)}`);
        return res.status(400).json({ error: 'Ein oder mehrere Menü-Einträge fehlen erforderliche Felder.' });
      }
    }

    // Für jeden Tag der Woche: prüfen, ob bereits ein Menü existiert und dann updaten oder erstellen
    for (const menu of menus) {
      const { WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID } = menu;
      
      // Prüfen, ob bereits ein Menü für den Tag existiert
      const existingMenu = await DBHandler.read(
        'Menu',
        'WHERE WeekNumber = ? AND Weekday = ?',
        [WeekNumber, WeekDay]
      );
  
      if (existingMenu.length > 0) {
        // Falls vorhanden, Menü aktualisieren
        await DBHandler.update(
          'Menu',
          { SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID },
          'WHERE WeekNumber = ? AND Weekday = ?',
          [WeekNumber, WeekDay]
        );
        logger.info(`Updated menu for WeekNumber: ${WeekNumber}, WeekDay: ${WeekDay}`);
      } else {
        // Falls nicht vorhanden, neues Menü erstellen
        await DBHandler.create('Menu', {
          WeekNumber,
          Weekday: WeekDay,
          SoupID,
          M1ID,
          M2ID,
          LunchDessertID,
          A1ID,
          A2ID,
        });
        logger.debug(`Created menu for WeekNumber: ${WeekNumber}, WeekDay: ${WeekDay}`);
      }
    }

    res.status(200).json({ message: 'Week menu set successfully' });
  } catch (err) {
    logger.error(`Error setting week menu: ${err.message}`);
    res.status(500).json({ error: 'Failed to set week menu' });
  }
});

/**
 * POST /api/menu
 * Setzt das Menü für einen bestimmten Tag (WeekNumber und WeekDay).
 */
router.post('/', async (req, res) => {
    try {
      const { WeekNumber, WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID } = req.body;
  
      // Validierung der erforderlichen Felder (WeekNumber, WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID)
      if (
        WeekNumber === undefined ||
        WeekDay === undefined ||
        SoupID === undefined ||
        M1ID === undefined ||
        M2ID === undefined ||
        LunchDessertID === undefined ||
        A1ID === undefined ||
        A2ID === undefined
      ) {
        // Logge welche Parameter fehlen
        logger.error(`Missing required fields only got: ${JSON.stringify(req.body)}`);
        return res.status(400).json({ error: 'Missing required fields' });
      }
  
      // Prüfen, ob bereits ein Menü für den Tag existiert
      const existingMenu = await DBHandler.read(
        'Menu',
        'WHERE WeekNumber = ? AND Weekday = ?',
        [WeekNumber, WeekDay]
      );
  
      if (existingMenu.length > 0) {
        // Wenn ein Menü existiert, aktualisieren
        await DBHandler.update(
          'Menu',
          { SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID },
          'WHERE WeekNumber = ? AND Weekday = ?',
          [WeekNumber, WeekDay]
        );
        logger.info(`Updated menu for WeekNumber: ${WeekNumber}, WeekDay: ${WeekDay}`);
      } else {
        // Neues Menü erstellen
        await DBHandler.create('Menu', {
          WeekNumber,
          Weekday: WeekDay,
          SoupID,
          M1ID,
          M2ID,
          LunchDessertID,
          A1ID,
          A2ID,
        });
        logger.debug(`Created menu for WeekNumber: ${WeekNumber}, WeekDay: ${WeekDay}`);
      }
  
      res.status(200).json({ message: 'Menu set successfully' });
    } catch (err) {
      logger.error(`Error setting menu: ${err.message}`);
      res.status(500).json({ error: 'Failed to set menu' });
    }
  });  

/**
 * GET /api/menu/:id
 * Lädt das Menü mit einer bestimmten ID mit aufgelösten Gerichten.
 */
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;

    // Menü abrufen
    const menu = await DBHandler.read('Menu', 'WHERE ID = ?', [id]);

    if (menu.length === 0) {
      logger.debug(`Menu not found: ${id}`);
      return res.status(404).json({ error: 'Menu not found' });
    }

    const fullMenu = await enrichMenu(menu[0]);
    res.status(200).json(fullMenu);
  } catch (err) {
    logger.error(`Error fetching menu: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu' });
  }
});

/**
 * POST /api/menu/csv
 * Importiert Menüs aus einer mitgesendeten CSV-Datei korrekt in die Datenbank.
 * Erwartete CSV-Spalten: WeekNumber, WeekDay, Soup, LunchOne, LunchTwo, Dessert, DinnerOne, DinnerTwo
 */
router.post('/csv', async (req, res) => {
  try {
    const csv = req.body.csv;
    if (!csv) {
      logger.error('CSV-Inhalt fehlt im Request-Body.');
      return res.status(400).json({ error: 'CSV data is required' });
    }

    // CSV-Zeilen (nach Zeilenumbruch) aufteilen und leere Zeilen entfernen
    const lines = csv.split('\n').filter(line => line.trim() !== '');

    if (lines.length === 0) {
      logger.error('CSV enthält keine Zeilen.');
      return res.status(400).json({ error: 'CSV is empty' });
    }

    // CSV-Header extrahieren und trimmen
    const delimiter = csv.includes(';') ? ';' : ',';
    const header = lines.shift().split(delimiter).map(col => col.trim());
    const expectedHeader = ['WeekNumber', 'WeekDay', 'Soup', 'LunchOne', 'LunchTwo', 'Dessert', 'DinnerOne', 'DinnerTwo'];
    // Optional: Überprüfen, ob der Header exakt passt
    if (header.length !== expectedHeader.length || !header.every((col, idx) => col === expectedHeader[idx])) {
      logger.error(`Ungültiger CSV-Header. Erwartet: ${expectedHeader.join(', ')}, erhalten: ${header.join(', ')}`);
      return res.status(400).json({ error: 'Invalid CSV header' });
    }

    // CSV-Zeilen in Objekte umwandeln
    const menus = lines.map(line => {
      const values = line.split(delimiter).map(val => val.trim());
      const row = {};
      header.forEach((col, index) => {
      row[col] = values[index];
      });
      return row;
    });

    // Alle in der CSV auftretenden Gerichtsnamen sammeln (aus den Spalten, die die Namen enthalten)
    const dishColumns = ['Soup', 'LunchOne', 'LunchTwo', 'Dessert', 'DinnerOne', 'DinnerTwo'];
    const dishNamesSet = new Set();
    menus.forEach(menu => {
      dishColumns.forEach(col => {
        if (menu[col]) {
          dishNamesSet.add(menu[col]);
        }
      });
    });
    const dishNames = Array.from(dishNamesSet);

    // Alle benötigten Foods-Einträge in einem Query abfragen
    const placeholders = dishNames.map(() => '?').join(',');
    const foods = await DBHandler.executeQuery(
      `SELECT Name, ID FROM Foods WHERE Name IN (${placeholders})`,
      dishNames
    );

    // Mapping: Gerichtname -> Gericht-ID
    const dishMapping = {};
    foods.forEach(food => {
      dishMapping[food.Name] = food.ID;
    });

    // Überprüfen, ob alle in der CSV verwendeten Gerichte gefunden wurden
    for (const dishName of dishNames) {
      if (!dishMapping[dishName]) {
        const errMsg = `Gericht "${dishName}" wurde in der Datenbank nicht gefunden. Bitte prüfe die CSV-Daten.`;
        logger.error(errMsg);
        return res.status(400).json({ error: errMsg });
      }
    }

    // Mapping der CSV-Spalten zu den DB-Feldern; dabei werden die Gerichtsnamen in IDs umgewandelt.
    const mappedMenus = menus.map(menu => ({
      WeekNumber: menu.WeekNumber,
      WeekDay: menu.WeekDay,
      SoupID: dishMapping[menu.Soup],
      M1ID: dishMapping[menu.LunchOne],
      M2ID: dishMapping[menu.LunchTwo],
      LunchDessertID: dishMapping[menu.Dessert],
      A1ID: dishMapping[menu.DinnerOne],
      A2ID: dishMapping[menu.DinnerTwo],
    }));

    // Für jedes Menü: prüfen, ob es bereits existiert, und dann updaten oder neu anlegen.
    await Promise.all(
      mappedMenus.map(async (menu) => {
        const existingMenu = await DBHandler.read(
          'Menu',
          'WHERE WeekNumber = ? AND Weekday = ?',
          [menu.WeekNumber, menu.WeekDay]
        );

        if (existingMenu.length > 0) {
          // Menü aktualisieren
          await DBHandler.update(
            'Menu',
            {
              SoupID: menu.SoupID,
              M1ID: menu.M1ID,
              M2ID: menu.M2ID,
              LunchDessertID: menu.LunchDessertID,
              A1ID: menu.A1ID,
              A2ID: menu.A2ID,
            },
            'WHERE WeekNumber = ? AND Weekday = ?',
            [menu.WeekNumber, menu.WeekDay]
          );
          logger.info(`Aktualisiertes Menü für WeekNumber: ${menu.WeekNumber}, WeekDay: ${menu.WeekDay}`);
        } else {
          // Neues Menü erstellen
          await DBHandler.create('Menu', { ...menu, Weekday: menu.WeekDay });
          logger.debug(`Erstellt Menü für WeekNumber: ${menu.WeekNumber}, WeekDay: ${menu.WeekDay}`);
        }
      })
    );

    res.status(200).json({ message: 'Menüs erfolgreich importiert' });
  } catch (err) {
    logger.error(`Fehler beim Importieren der Menüs aus CSV: ${err.message}`);
    return res.status(500).json({ error: 'Failed to import menus from CSV' });
  }
});

/**
 * GET /api/menu/day/:weekNumber/:weekDay
 * Lädt das Menü eines bestimmten Tages mit aufgelösten Gerichten.
 */
router.get('/day/:weekNumber/:weekDay', async (req, res) => {
  try {
    const { weekNumber, weekDay } = req.params;

    // Menü für den Tag abrufen
    const menu = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? AND Weekday = ?',
      [weekNumber, weekDay]
    );

    if (menu.length === 0) {
      logger.debug(`Menu not found for day: ${weekDay} in week: ${weekNumber}`);
      return res.status(404).json({ error: 'Menu not found for the specified day' });
    }

    const fullMenu = await enrichMenu(menu[0]);
    res.status(200).json(fullMenu);
  } catch (err) {
    logger.error(`Error fetching menu for day: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu for the day' });
  }
});

// Function to get ID of a food item by its name
async function getFoodIdByName(foodName) {
  const cleanedName = foodName.normalize("NFKC").replace(/\s+/g, ' ').trim();

  const result = await DBHandler.executeQuery(
    `SELECT ID FROM Foods WHERE REPLACE(Name, CHAR(160), ' ') = ? OR Name = ?`,
    [cleanedName, cleanedName]
  );

  return result.length > 0 ? result[0].ID : null;
}

/**
 * POST /api/menu/csv
 * Importiert Menüs aus einer mitgesendeten CSV-Datei korrekt in die Datenbank.
 * Erwartete CSV-Spalten: WeekNumber, WeekDay, Soup, LunchOne, LunchTwo, Dessert, DinnerOne, DinnerTwo
 */
router.post('/csv', async (req, res) => {
  try {
    const csv = req.body.csv;
    if (!csv) {
      return res.status(400).json({ error: 'CSV data is required' });
    }

    // CSV-Zeilen in Objekte umwandeln
    const lines = csv.split('\n').filter(line => line.trim() !== '');
    const delimiter = csv.includes(';') ? ';' : ',';
    const header = lines.shift().split(delimiter).map(col => col.trim());
    const menus = lines.map(line => {
      const values = line.split(delimiter).map(val => val.trim());
      return header.reduce((acc, key, index) => {
      acc[key] = values[index];
      return acc;
      }, {});
    });

    // Gerichtsnamen sammeln
    const dishColumns = ['Soup', 'LunchOne', 'LunchTwo', 'Dessert', 'DinnerOne', 'DinnerTwo'];
    const dishMapping = {};

    for (const menu of menus) {
      for (const col of dishColumns) {
        const dishName = menu[col]?.trim();
        if (dishName && !dishMapping[dishName]) {
          const foodId = await getFoodIdByName(dishName);
          if (!foodId) {
            logger.error(`Gericht "${dishName}" wurde nicht gefunden.`);
            return res.status(400).json({ error: `Gericht "${dishName}" wurde nicht gefunden.` });
          }
          dishMapping[dishName] = foodId;
        }
      }
    }

    logger.debug(`Gerichts-Mapping: ${JSON.stringify(dishMapping)}`);

    // Mapping der Menü-Daten zu IDs (null-Handling für nicht gefundene Werte)
    const mappedMenus = menus.map(menu => ({
      WeekNumber: menu.WeekNumber,
      WeekDay: menu.WeekDay,
      SoupID: dishMapping[menu.Soup.trim()] || null,
      M1ID: dishMapping[menu.LunchOne.trim()] || null,
      M2ID: dishMapping[menu.LunchTwo.trim()] || null,
      LunchDessertID: dishMapping[menu.Dessert.trim()] || null,
      A1ID: dishMapping[menu.DinnerOne.trim()] || null, // Falls nicht gefunden → null
      A2ID: dishMapping[menu.DinnerTwo.trim()] || null, // Falls nicht gefunden → null
    }));

    // Prüfen, ob Menüs existieren, dann updaten oder neu erstellen
    await Promise.all(
      mappedMenus.map(async (menu) => {
        const existingMenu = await DBHandler.read(
          'Menu',
          'WHERE WeekNumber = ? AND Weekday = ?',
          [menu.WeekNumber, menu.WeekDay]
        );

        if (existingMenu.length > 0) {
          await DBHandler.update(
            'Menu',
            {
              SoupID: menu.SoupID,
              M1ID: menu.M1ID,
              M2ID: menu.M2ID,
              LunchDessertID: menu.LunchDessertID,
              A1ID: menu.A1ID,
              A2ID: menu.A2ID,
            },
            'WHERE WeekNumber = ? AND Weekday = ?',
            [menu.WeekNumber, menu.WeekDay]
          );
          logger.info(`Aktualisiertes Menü für WeekNumber: ${menu.WeekNumber}, WeekDay: ${menu.WeekDay}`);
        } else {
          await DBHandler.create('Menu', { ...menu, Weekday: menu.WeekDay });
          logger.info(`Erstellt Menü für WeekNumber: ${menu.WeekNumber}, WeekDay: ${menu.WeekDay}`);
        }
      })
    );

    res.status(200).json({ message: 'Menüs erfolgreich importiert' });

  } catch (err) {
    logger.error(`❌ Fehler beim Importieren der Menüs: ${err.message}`);
    return res.status(500).json({ error: 'Failed to import menus' });
  }
});

/**
 * GET /api/menu/weekStart/:date
 * Lädt das Menü für eine bestimmte Woche mit aufgelösten Gerichten (definiert durch das Datum des Montags).
 */
// Zuerst bestimmen welche Weeknumber dieses Datum ist und dann fetchen wie bei /week/:weekNumber
router.get('/weekStart/:date', async (req, res) => {
  try {
    const { date } = req.params;
    const startDate = new Date(date);
    const weekNumber = await getReferenceWeekFromDate(startDate);

    logger.debug(`WeekNumber for date ${date}: ${weekNumber}`);

    const menus = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? ORDER BY CAST(Weekday AS UNSIGNED)',
      [weekNumber]
    );

    if (menus.length === 0) {
      logger.debug(`Menuplan not found for week: ${weekNumber}`);
      return res.status(404).json({ error: 'Menuplan not found for the specified week' });
    }

    const fullMenus = await Promise.all(menus.map(enrichMenu));
    res.status(200).json(fullMenus);
  } catch (err) {
    logger.error(`Error fetching menu for week: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu for the week' });
  }
});

/**
 * GET /api/menu/weekWithSpecials/:date
 * Liefert den Menüplan einer Woche inklusive möglicher Spezialmenüs.
 */
router.get('/weekWithSpecials/:date', async (req, res) => {
  try {
    const { date } = req.params;
    const startDate = dayjs(date).startOf('day');
    const weekNumber = await getReferenceWeekFromDate(startDate.toDate());

    const menus = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? ORDER BY CAST(Weekday AS UNSIGNED)',
      [weekNumber]
    );

    const weekDates = Array.from({ length: 7 }).map((_, i) =>
      startDate.add(i, 'day').format('YYYY-MM-DD')
    );

    let fullMenus = [];
    if (menus.length > 0) {
      fullMenus = await Promise.all(menus.map(enrichMenu));
      fullMenus.sort((a, b) => Number(a.WeekDay) - Number(b.WeekDay));
    }

    const placeholders = weekDates.map(() => '?').join(',');
    const specialMeals = await DBHandler.executeQuery(
      `SELECT * FROM SpecialMeals WHERE Date IN (${placeholders})`,
      weekDates
    );

    const specialsByDate = {};
    for (const meal of specialMeals) {
      specialsByDate[dayjs(meal.Date).format('YYYY-MM-DD')] = await enrichSpecialMeal(meal);
    }

    let combined;
    if (fullMenus.length > 0) {
      combined = fullMenus.map((menu, idx) => {
        const dateKey = weekDates[idx];
        if (specialsByDate[dateKey]) {
          return { ...specialsByDate[dateKey], WeekDay: menu.WeekDay, WeekNumber: menu.WeekNumber };
        }
        return { ...menu, Date: dateKey };
      });
    } else if (specialMeals.length > 0) {
      combined = weekDates.map((d, idx) => {
        if (specialsByDate[d]) {
          return { ...specialsByDate[d], WeekDay: idx + 1, WeekNumber: weekNumber };
        }
        return { WeekDay: idx + 1, WeekNumber: weekNumber, Date: d, Soup: null, Lunch1: null, Lunch2: null, LunchDessert: null, Dinner1: null, Dinner2: null };
      });
    } else {
      logger.debug(`Menuplan not found for week: ${weekNumber}`);
      return res.status(404).json({ error: 'Menuplan not found for the specified week' });
    }

    res.status(200).json(combined);
  } catch (err) {
    logger.error(`Error fetching menu with specials: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu with specials' });
  }
});

/**
 * GET /api/menu/date/:date
 * Liefert das Menü für ein bestimmtes Datum inklusive IDs.
 */
router.get('/date/:date', async (req, res) => {
  try {
    const { date } = req.params;
    const weekNumber = await getReferenceWeekFromDate(new Date(date));

    const jsDay = new Date(date).getDay();
    const weekDay = jsDay === 0 ? 6 : jsDay - 1;

    const menus = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? AND Weekday = ?',
      [weekNumber, weekDay]
    );

    if (menus.length === 0) {
      logger.debug(`Menu not found for date: ${date}`);
      return res.status(404).json({ error: 'Menu not found for the specified date' });
    }

    let fullMenu = await enrichMenu(menus[0]);
    fullMenu = { ...fullMenu, ID: menus[0].ID,
      SoupID: menus[0].SoupID,
      Lunch1ID: menus[0].M1ID,
      Lunch2ID: menus[0].M2ID,
      LunchDessertID: menus[0].LunchDessertID,
      Dinner1ID: menus[0].A1ID,
      Dinner2ID: menus[0].A2ID,
      Date: date };

    const [special] = await DBHandler.executeQuery('SELECT * FROM SpecialMeals WHERE Date = ?', [date]);
    if (special) {
      const spec = await enrichSpecialMeal(special);
      fullMenu = {
        ...spec,
        ID: menus[0].ID,
        WeekNumber: weekNumber,
        WeekDay: weekDay,
        SoupID: menus[0].SoupID,
        Lunch1ID: menus[0].M1ID,
        Lunch2ID: menus[0].M2ID,
        LunchDessertID: menus[0].LunchDessertID,
        Dinner1ID: menus[0].A1ID,
        Dinner2ID: menus[0].A2ID,
        Date: date
      };
    }

    res.status(200).json(fullMenu);
  } catch (err) {
    logger.error(`Error fetching menu for date: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu for the date' });
  }
});


/**
 * GET /api/menu/week/:weekNumber
 * Lädt das Menü für eine bestimmte Woche mit aufgelösten Gerichten.
 */
router.get('/week/:weekNumber', async (req, res) => {
  try {
    const { weekNumber } = req.params;

    // Menü für die Woche abrufen
    const menus = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? ORDER BY CAST(Weekday AS UNSIGNED)',
      [weekNumber]
    );

    if (menus.length === 0) {
      logger.debug(`Menuplan not found for week: ${weekNumber}`);
      return res.status(404).json({ error: 'Menuplan not found for the specified week' });
    }

    const fullMenus = await Promise.all(menus.map(enrichMenu));
    res.status(200).json(fullMenus);
  } catch (err) {
    logger.error(`Error fetching menu for week: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu for the week' });
  }
});

/**
 * DELETE /api/menu/:id
 * Delete a menu by id.
 */
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    await DBHandler.delete('Menu', 'WHERE ID = ?', [id]);
    logger.debug(`Menu deleted successfully: ${id}`);
    res.status(200).json({ message: 'Menu deleted successfully' });
  } catch (err) {
    logger.error(`Error deleting menu: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete menu' });
  }
});

/**
 * GET /api/menu/next-week
 * Get Menu for next week. based on current reference week and the menu plan.
 */
router.get('/next-week', async (req, res) => {
  try {
    const referenceWeek = await getReferenceWeek();
    const nextWeek = parseInt(referenceWeek, 10) + 1;
    let MealplanWeek;
    if(nextWeek > 4) {
      MealplanWeek = nextWeek / 4;
    } else {
      MealplanWeek = nextWeek;
    }
    logger.debug(`Next week (ReferenceWeek): ${referenceWeek + 1}`);
    logger.debug(`Next week (in Mealplan): ${MealplanWeek}`);

    const menus = await DBHandler.executeQuery(
      'SELECT * FROM Menu WHERE WeekNumber = ? ORDER BY CAST(Weekday AS UNSIGNED)',
      [nextWeek]
    );

    // Get Date of next Monday(startDate) and next Sunday(endDate)
    const startDate = new Date();
    startDate.setDate(startDate.getDate() + (1 + 7 - startDate.getDay()) % 7);
    const endDate = new Date(startDate);
    endDate.setDate(endDate.getDate() + 6);

    logger.debug(`Next week starts on: ${startDate.toISOString().split('T')[0]}`);
    logger.debug(`Next week ends on: ${endDate.toISOString().split('T')[0]}`);

    if (menus.length === 0) {
      logger.debug(`Menuplan not found for next week: ${nextWeek}`);
      return res.status(404).json({ error: 'Menuplan not found for the next week' });
    }

    const fullMenus = await Promise.all(menus.map(enrichMenu));
    const response = {
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      menus: fullMenus,
    };

    res.status(200).json(response);
  } catch (err) {
    logger.error(`Error fetching menu for next week: ${err.message}`);
    res.status(500).json({ error: 'Failed to fetch menu for the next week' });
  }
});

/**
 * DELETE /api/menu/wipe
 * Clear all menus.
 */
router.delete('/wipe', async (req, res) => {
  try {
    await DBHandler.executeQuery('DELETE FROM Menu');
    logger.info('All menus deleted successfully');
    res.status(200).json({ message: 'All menus deleted successfully' });
  } catch (err) {
    logger.error(`Error deleting all menus: ${err.message}`);
    res.status(500).json({ error: 'Failed to delete all menus' });
  }
});

/**
 * Hilfsfunktion: Löst die Fremdschlüssel in der Menu-Tabelle auf.
 */
async function enrichMenu(menu) {
  const foodIds = [
    menu.SoupID,
    menu.M1ID,
    menu.M2ID,
    menu.LunchDessertID,
    menu.A1ID,
    menu.A2ID,
  ].filter(id => id !== null && id !== undefined); // nur gültige IDs

  // 🔧 Dynamisches Query mit korrekt aufgelösten Platzhaltern
  const placeholders = foodIds.map(() => '?').join(',');
  const sql = `
    SELECT f.ID, f.Name, f.Type, p.Name as PictureName, p.MediaType, p.Bytes 
    FROM Foods f
    JOIN PictureFiles p ON f.PictureID = p.ID
    WHERE f.ID IN (${placeholders})
  `;

  const foodDetails = await DBHandler.executeQuery(sql, foodIds);

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

  const days = ['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag', 'Sonntag'];
  const WeekDayString = days[menu.Weekday];

  logger.debug(`Enriched menu for ${WeekDayString} / ${menu.Weekday}, WeekNumber: ${menu.WeekNumber}`);
  logger.debug(`Menu: ${JSON.stringify(menu)}`);

  return {
    WeekNumber: menu.WeekNumber,
    WeekDay: menu.Weekday,
    WeekDayString: WeekDayString,
    Soup: foodsById[menu.SoupID] || null,
    Lunch1: foodsById[menu.M1ID] || null,
    Lunch2: foodsById[menu.M2ID] || null,
    LunchDessert: foodsById[menu.LunchDessertID] || null,
    Dinner1: foodsById[menu.A1ID] || null,
    Dinner2: foodsById[menu.A2ID] || null,
  };
}

async function enrichSpecialMeal(meal) {
  const foodIds = [
    meal.SoupID,
    meal.M1ID,
    meal.M2ID,
    meal.LunchDessertID,
    meal.A1ID,
    meal.A2ID,
  ];

  const foodDetails = await DBHandler.executeQuery(
    `SELECT f.ID, f.Name, f.Type, p.Name as PictureName, p.MediaType, p.Bytes
     FROM Foods f
     JOIN PictureFiles p ON f.PictureID = p.ID
     WHERE f.ID IN (?)`,
    [foodIds]
  );

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
 *   - name: Menu
 *     description: Verwaltung des Menüplans (tägliche und wöchentliche Menüs, CSV-Import, Specials)
 *
 * components:
 *   schemas:
 *     Picture:
 *       type: object
 *       properties:
 *         Name: { type: string, example: "tomato_soup.jpg" }
 *         MediaType: { type: string, example: "image/jpeg" }
 *         Base64: { type: string, description: "Base64-kodierte Bilddaten" }
 *
 *     FoodWithPicture:
 *       type: object
 *       properties:
 *         Name: { type: string, example: "Tomatensuppe" }
 *         Type: { type: string, example: "soup" }
 *         Picture:
 *           $ref: '#/components/schemas/Picture'
 *
 *     MenuSetDayRequest:
 *       type: object
 *       required: [WeekNumber, WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID]
 *       properties:
 *         WeekNumber: { type: integer, example: 43 }
 *         WeekDay: { type: integer, description: "0=Montag, …, 6=Sonntag (gemäß Code-Logik)", example: 0 }
 *         SoupID: { type: integer, example: 2 }
 *         M1ID: { type: integer, example: 11 }
 *         M2ID: { type: integer, example: 12 }
 *         LunchDessertID: { type: integer, example: 21 }
 *         A1ID: { type: integer, example: 31 }
 *         A2ID: { type: integer, example: 32 }
 *
 *     MenuSetWeekRequest:
 *       type: object
 *       required: [WeekNumber, menus]
 *       properties:
 *         WeekNumber: { type: integer, example: 43 }
 *         menus:
 *           type: array
 *           items:
 *             type: object
 *             required: [WeekDay, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID]
 *             properties:
 *               WeekDay: { type: integer, example: 0 }
 *               SoupID: { type: integer, example: 2 }
 *               M1ID: { type: integer, example: 11 }
 *               M2ID: { type: integer, example: 12 }
 *               LunchDessertID: { type: integer, example: 21 }
 *               A1ID: { type: integer, example: 31 }
 *               A2ID: { type: integer, example: 32 }
 *
 *     CSVImportRequest:
 *       type: object
 *       required: [csv]
 *       properties:
 *         csv:
 *           type: string
 *           description: >
 *             CSV-Text mit Header: WeekNumber,WeekDay,Soup,LunchOne,LunchTwo,Dessert,DinnerOne,DinnerTwo
 *           example: |
 *             WeekNumber,WeekDay,Soup,LunchOne,LunchTwo,Dessert,DinnerOne,DinnerTwo
 *             43,0,Tomatensuppe,Lasagne,Carbonara,Apfelstrudel,Schnitzel,Backhendl
 *
 *     EnrichedMenu:
 *       type: object
 *       properties:
 *         WeekNumber: { type: integer, example: 43 }
 *         WeekDay: { type: integer, example: 0 }
 *         WeekDayString: { type: string, example: "Montag" }
 *         Soup: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Lunch1: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Lunch2: { $ref: '#/components/schemas/FoodWithPicture' }
 *         LunchDessert: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Dinner1: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Dinner2: { $ref: '#/components/schemas/FoodWithPicture' }
 *
 *     SpecialMealEnriched:
 *       type: object
 *       properties:
 *         Date: { type: string, format: date, example: "2025-11-05" }
 *         Soup: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Lunch1: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Lunch2: { $ref: '#/components/schemas/FoodWithPicture' }
 *         LunchDessert: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Dinner1: { $ref: '#/components/schemas/FoodWithPicture' }
 *         Dinner2: { $ref: '#/components/schemas/FoodWithPicture' }
 *
 *     MenuWithIdsForDate:
 *       allOf:
 *         - $ref: '#/components/schemas/EnrichedMenu'
 *         - type: object
 *           properties:
 *             ID: { type: integer, example: 123 }
 *             SoupID: { type: integer, example: 2 }
 *             Lunch1ID: { type: integer, example: 11 }
 *             Lunch2ID: { type: integer, example: 12 }
 *             LunchDessertID: { type: integer, example: 21 }
 *             Dinner1ID: { type: integer, example: 31 }
 *             Dinner2ID: { type: integer, example: 32 }
 *             Date: { type: string, format: date, example: "2025-11-05" }
 *
 *     WeekWithSpecialsResponse:
 *       type: array
 *       description: Liste von 7 Tagen. Für Tage mit Specials überschreiben die Specials das Tagesmenü.
 *       items:
 *         anyOf:
 *           - $ref: '#/components/schemas/EnrichedMenu'
 *           - $ref: '#/components/schemas/SpecialMealEnriched'
 *
 *     NextWeekResponse:
 *       type: object
 *       properties:
 *         startDate: { type: string, format: date, example: "2025-11-10" }
 *         endDate: { type: string, format: date, example: "2025-11-16" }
 *         menus:
 *           type: array
 *           items: { $ref: '#/components/schemas/EnrichedMenu' }
 *
 *     BasicMessage:
 *       type: object
 *       properties:
 *         message: { type: string, example: "Menu set successfully" }
 *
 * /api/menu/week:
 *   post:
 *     tags: [Menu]
 *     summary: Setzt oder aktualisiert das Menü für eine ganze Woche (Upsert je Tag)
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema: { $ref: '#/components/schemas/MenuSetWeekRequest' }
 *     responses:
 *       200: { description: Week menu set successfully, content: { application/json: { schema: { $ref: '#/components/schemas/BasicMessage' } } } }
 *       400: { description: Ungültige oder fehlende Felder }
 *       500: { description: Serverfehler }
 *
 * /api/menu:
 *   post:
 *     tags: [Menu]
 *     summary: Setzt oder aktualisiert das Menü für einen bestimmten Tag (Upsert)
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema: { $ref: '#/components/schemas/MenuSetDayRequest' }
 *     responses:
 *       200: { description: Menu set successfully, content: { application/json: { schema: { $ref: '#/components/schemas/BasicMessage' } } } }
 *       400: { description: Ungültige oder fehlende Felder }
 *       500: { description: Serverfehler }
 *
 * /api/menu/{id}:
 *   get:
 *     tags: [Menu]
 *     summary: Lädt ein Menü per ID (inkl. aufgelöster Gerichte)
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema: { type: integer }
 *         example: 123
 *     responses:
 *       200:
 *         description: Menü gefunden
 *         content:
 *           application/json:
 *             schema: { $ref: '#/components/schemas/EnrichedMenu' }
 *       404: { description: Menü nicht gefunden }
 *       500: { description: Serverfehler }
 *   delete:
 *     tags: [Menu]
 *     summary: Löscht ein Menü per ID
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema: { type: integer }
 *         example: 123
 *     responses:
 *       200: { description: Menu deleted successfully, content: { application/json: { schema: { $ref: '#/components/schemas/BasicMessage' } } } }
 *       500: { description: Serverfehler }
 *
 * /api/menu/csv:
 *   post:
 *     tags: [Menu]
 *     summary: Importiert Menüs aus CSV (Upsert je Tag)
 *     description: >
 *       Erwartete Spalten: WeekNumber, WeekDay, Soup, LunchOne, LunchTwo, Dessert, DinnerOne, DinnerTwo.
 *       Hinweis: In der Codebasis existieren zwei Implementierungen von /csv – dokumentiere nur einmal.
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema: { $ref: '#/components/schemas/CSVImportRequest' }
 *     responses:
 *       200: { description: Menüs erfolgreich importiert }
 *       400: { description: CSV fehlerhaft oder Gericht nicht gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/day/{weekNumber}/{weekDay}:
 *   get:
 *     tags: [Menu]
 *     summary: Lädt das Menü eines bestimmten Tages (inkl. aufgelöster Gerichte)
 *     parameters:
 *       - in: path
 *         name: weekNumber
 *         required: true
 *         schema: { type: integer }
 *         example: 43
 *       - in: path
 *         name: weekDay
 *         required: true
 *         schema: { type: integer }
 *         description: "0=Montag … 6=Sonntag (gemäß Code-Logik)"
 *         example: 0
 *     responses:
 *       200: { description: Menü des Tages, content: { application/json: { schema: { $ref: '#/components/schemas/EnrichedMenu' } } } }
 *       404: { description: Kein Menü für diesen Tag gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/weekStart/{date}:
 *   get:
 *     tags: [Menu]
 *     summary: Lädt den Menüplan der Woche, die das angegebene Datum enthält
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema: { type: string, format: date }
 *         example: "2025-11-03"
 *     responses:
 *       200:
 *         description: Menüplan der Woche
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items: { $ref: '#/components/schemas/EnrichedMenu' }
 *       404: { description: Kein Menüplan für die Woche gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/weekWithSpecials/{date}:
 *   get:
 *     tags: [Menu]
 *     summary: Gibt den Menüplan der Woche inkl. Spezialmenüs zurück
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema: { type: string, format: date }
 *         example: "2025-11-03"
 *     responses:
 *       200:
 *         description: Menüplan (7 Tage, Specials überschreiben ggf. Menü)
 *         content:
 *           application/json:
 *             schema: { $ref: '#/components/schemas/WeekWithSpecialsResponse' }
 *       404: { description: Kein Menüplan gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/date/{date}:
 *   get:
 *     tags: [Menu]
 *     summary: Liefert das Menü für ein Datum (inkl. Food-IDs)
 *     parameters:
 *       - in: path
 *         name: date
 *         required: true
 *         schema: { type: string, format: date }
 *         example: "2025-11-05"
 *     responses:
 *       200:
 *         description: Menü für das Datum
 *         content:
 *           application/json:
 *             schema: { $ref: '#/components/schemas/MenuWithIdsForDate' }
 *       404: { description: Kein Menü gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/week/{weekNumber}:
 *   get:
 *     tags: [Menu]
 *     summary: Lädt den Menüplan einer Woche
 *     parameters:
 *       - in: path
 *         name: weekNumber
 *         required: true
 *         schema: { type: integer }
 *         example: 43
 *     responses:
 *       200:
 *         description: Menüplan der Woche
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items: { $ref: '#/components/schemas/EnrichedMenu' }
 *       404: { description: Kein Menüplan für die Woche gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/next-week:
 *   get:
 *     tags: [Menu]
 *     summary: Liefert den Menüplan für die nächste Referenz-Woche inkl. Start-/Enddatum
 *     responses:
 *       200:
 *         description: Menüplan nächste Woche
 *         content:
 *           application/json:
 *             schema: { $ref: '#/components/schemas/NextWeekResponse' }
 *       404: { description: Kein Menüplan für nächste Woche gefunden }
 *       500: { description: Serverfehler }
 *
 * /api/menu/wipe:
 *   delete:
 *     tags: [Menu]
 *     summary: Löscht alle Menüs
 *     responses:
 *       200: { description: All menus deleted successfully, content: { application/json: { schema: { $ref: '#/components/schemas/BasicMessage' } } } }
 *       500: { description: Serverfehler }
 */
