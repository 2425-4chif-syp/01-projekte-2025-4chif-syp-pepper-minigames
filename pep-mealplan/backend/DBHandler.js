const mysql = require('mysql2');
const dotenv = require('dotenv');
const logger = require('./utils/logger');
const fs = require('fs');
const path = require('path');

dotenv.config();

// ==== Custom Errors (Express-compatible) ====

class DBError extends Error {
  constructor(message, statusCode = 500, code = 'DB_ERROR') {
    super(message);
    this.name = 'DBError';
    this.statusCode = statusCode;
    this.code = code;
  }
}

class TableValidationError extends DBError {
  constructor(tableName) {
    super(`Invalid table name: "${tableName}"`, 400, 'INVALID_TABLE');
  }
}

class InvalidConditionError extends DBError {
  constructor(condition) {
    super(`Unsafe SQL condition: "${condition}"`, 400, 'INVALID_CONDITION');
  }
}

class NotFoundError extends DBError {
  constructor(entity = 'Entry') {
    super(`${entity} not found`, 404, 'NOT_FOUND');
  }
}

// ==== Helper Validation ====
const allowedTables = ['Allergens', 'FoodAllergens', 'Foods', 'Menu', 'Orders', 'People', 'PictureFiles', 'SpecialMeals'];

function validateTable(table) {
  if (!allowedTables.includes(table)) {
    throw new TableValidationError(table);
  }
}

function validateConditions(conditions) {
  if (!conditions) return;
  const allowedPattern = /^[\w\s=<>.'"%()?,+-]*$/;

  if (!allowedPattern.test(conditions)) {
    throw new InvalidConditionError(conditions);
  }
}

// ==== DB Connection ====

const poolConfig = {
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
  charset: 'utf8mb4'
};

let promisePool = null;

async function createPoolWithRetry(retries = 5, delay = 5000) {
  for (let i = 0; i < retries; i++) {
    try {
      const pool = mysql.createPool(poolConfig);

      pool.on('connection', (connection) => {
        connection.query(
          "SET character_set_client = utf8mb4, character_set_connection = utf8mb4, character_set_results = utf8mb4",
          (err) => {
            if (err) logger.error("Charset error: " + err.message);
          }
        );
      });

      promisePool = pool.promise();

      const connection = await promisePool.getConnection();
      await connection.query("SET NAMES utf8mb4");
      logger.info('Database connected successfully');
      connection.release();

      return promisePool;
    } catch (err) {
      logger.error(`Database connection failed (${i + 1}): ${err.message}`);

      if (['ER_ACCESS_DENIED_ERROR', 'ENOTFOUND'].includes(err.code)) {
        process.exit(1);
      }

      if (i < retries - 1) {
        logger.info(`Retrying in ${delay / 1000} seconds...`);
        await new Promise((res) => setTimeout(res, delay));
      } else {
        process.exit(1);
      }
    }
  }
}

async function ensureDatabaseInitialized() {
  try {
    const tables = await DBHandler.executeQuery("SHOW TABLES");

    if (tables.length === 0) {
      logger.warn("No tables found, running startup.sql...");
      const sqlFilePath = path.join(__dirname, 'startup.sql');
      const sqlContent = fs.readFileSync(sqlFilePath, 'utf8');
      await DBHandler.executeQuery(sqlContent);
      logger.info("Database initialized from startup.sql");
    } else {
      logger.info("Database already initialized");
    }
  } catch (error) {
    logger.error(`Initialization error: ${error.message}`);
    process.exit(1);
  }
}

(async () => {
  await createPoolWithRetry();
  await ensureDatabaseInitialized();
})();

// ==== DBHandler Class ====

class DBHandler {
  static async executeQuery(query, params = []) {
    if (!promisePool) {
      await new Promise((resolve) => setTimeout(resolve, 2000));
    }

    // Log the query with inserted parameters for debugging
    logger.debug(`Executing SQL: ${query} | Params: ${JSON.stringify(params)}`);

    try {
      const [results] = await promisePool.execute(query, params);
      return results;
    } catch (err) {
      logger.error(`SQL execution error: ${err.message}`);
      throw new DBError(err.message);
    }
  }

  static async read(table, conditions = '', params = []) {
    validateTable(table);
    validateConditions(conditions);
    const query = `SELECT * FROM ${table} ${conditions}`;
    const results = await this.executeQuery(query, params);
    return results;
  }

  static async create(table, data) {
    validateTable(table);
    const keys = Object.keys(data).join(', ');
    const placeholders = Object.keys(data).map(() => '?').join(', ');
    const values = Object.values(data);
    const query = `INSERT INTO ${table} (${keys}) VALUES (${placeholders})`;
    return this.executeQuery(query, values);
  }

  static async update(table, data, conditions, params = []) {
    validateTable(table);
    validateConditions(conditions);
    const setClause = Object.keys(data).map((key) => `${key} = ?`).join(', ');
    const values = [...Object.values(data), ...params];
    const query = `UPDATE ${table} SET ${setClause} ${conditions}`;
    const result = await this.executeQuery(query, values);
    if (result.affectedRows === 0) {
      throw new NotFoundError(`Entry not found in table "${table}"`);
    }
    return result;
  }

  static async delete(table, conditions, params = []) {
    validateTable(table);
    validateConditions(conditions);
    const query = `DELETE FROM ${table} ${conditions}`;
    const result = await this.executeQuery(query, params);
    if (result.affectedRows === 0) {
      throw new NotFoundError(`Entry not found in table "${table}"`);
    }
    return result;
  }

  static async count(table, conditions = '', params = []) {
    validateTable(table);
    validateConditions(conditions);
    const query = `SELECT COUNT(*) AS count FROM ${table} ${conditions}`;
    const result = await this.executeQuery(query, params);
    return result[0].count;
  }
}

module.exports = DBHandler;