// server.js

const express = require('express');
const helmet = require('helmet');
const path = require('path');
const fs = require('fs');
const dotenv = require('dotenv');
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const swaggerJsdoc = require('swagger-jsdoc');

const logger = require('./utils/logger');
const updateChecker = require('./utils/updateChecker');
const { notFoundHandler, errorHandler } = require('./middleware/errorHandler');
const residentsRoute = require('./routes/residents/residents-route');
const allergensRoute = require('./routes/allergens/allergens-route');
const foodsRoute = require('./routes/foods/foods-route');
const menuRoute = require('./routes/menu/menu-route');
const specialMealRoute = require('./routes/special-meals/special-meal-route');
const ordersRoute = require('./routes/orders/order-route');
const menuExport = require('./routes/menu-export/menu-export-route');
const { getReferenceWeek } = require('./utils/referenceWeek');

// --- Load .env early and from absolute path ---
const envPath = path.join(__dirname, '.env');
if (!fs.existsSync(envPath)) {
  logger.error(`No .env file found at ${envPath}. Exiting...`);
  process.exit(1);
}
dotenv.config({ path: envPath });

// --- Validate required env vars ---
const requiredEnv = ['PORT', 'DB_HOST', 'DB_USER', 'DB_PASSWORD', 'DB_NAME'];
for (const k of requiredEnv) {
  if (!process.env[k]) {
    logger.error(`Missing environment variable: ${k}. Exiting...`);
    process.exit(1);
  }
}

logger.info('Starting server...');
logger.info('LeoCloud V' + updateChecker.getVersion() + ' starting...');

// Debug log mode info
const DEBUG_LOGS = process.env.DEBUG_LOGS || 'OFF';
if (DEBUG_LOGS === 'FILE') {
  logger.info('Debug logging is enabled: logging to file only');
} else if (DEBUG_LOGS === 'BOTH') {
  logger.info('Debug logging is enabled: logging to both console and file');
} else if (DEBUG_LOGS === 'OFF') {
  logger.info('Debug logging is disabled. Only logging INFO and higher');
} else {
  logger.warn(`Unknown DEBUG_LOGS mode: "${DEBUG_LOGS}". Defaulting to OFF`);
}

// Try to read current reference week (non-fatal)
(async () => {
  try {
    const referenceWeek = await getReferenceWeek();
    logger.debug('Current ReferenceWeek: ' + JSON.stringify(referenceWeek));
  } catch (err) {
    logger.error(`Error initializing ReferenceWeek: ${err.message}`);
  }
})();

const app = express();



// --- Swagger Setup ---
const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Menu Assistent API',
      version: '1.0.0',
      description: 'Automatisch generierte API-Dokumentation für das Menu Assistent Backend',
    },
    servers: [{ url: 'http://localhost:3000' }],
  },
  apis: ['./routes/**/*.js'], // durchsucht alle Route-Dateien
};

const specs = swaggerJsdoc(options);
app.use('/api/docs', swaggerUi.serve, swaggerUi.setup(specs));

logger.info('Swagger UI verfügbar unter: http://localhost:3000/api/docs');


const PORT = process.env.PORT || 3000;

// CORS
app.use(
    cors({
      origin: '*',
      exposedHeaders: ['Content-Disposition'],
    })
);

// Body parsing (once)
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Security headers
app.use(
    helmet({
      contentSecurityPolicy: {
        directives: {
          "default-src": ["'self'"],
          "script-src": ["'self'", "http://localhost:3000", "https://cdnjs.cloudflare.com", "https://fonts.googleapis.com"],
          "script-src-elem": ["'self'", "http://localhost:3000"],
          "script-src-attr": ["'unsafe-inline'"],
          "style-src": ["'self'", "'unsafe-inline'", "https://fonts.googleapis.com"],
          "font-src": ["'self'", "https://fonts.gstatic.com"],
          "img-src": ["'self'", "data:"],
          "connect-src": ["'self'", "wss:"],
          "frame-src": ["'none'"],
        },
      },
    })
);

// Request logging (debug)
app.use((req, _res, next) => {
  const clientIP = req.headers['x-forwarded-for'] || req.socket.remoteAddress;
  const contentLength = req.headers['content-length'] || '0';
  logger.debug(`Incoming request: ${req.method} ${req.url} | IP: ${clientIP} | Size: ${contentLength} bytes`);
  next();
});

// --- API routes ---
app.use('/api/residents', residentsRoute);
app.use('/api/allergens', allergensRoute);
app.use('/api/foods', foodsRoute);
app.use('/api/menu', menuRoute);
app.use('/api/special-meals', specialMealRoute);
app.use('/api/orders', ordersRoute);
app.use('/api/menu-export', menuExport);

// Meta endpoints
app.get('/api/cycle-length', (_req, res) => {
  const cycleLength = require('./utils/referenceWeek').getCycleLength();
  res.json({ cycleLength });
});

app.get('/api/version-check', (_req, res) => {
  updateChecker.checkForNewVersion();
  res.json(updateChecker.getVersionStatus());
});

// --- Static frontend ---
const frontendPath = path.join(__dirname, 'public');
app.use(express.static(frontendPath));

// Let Angular handle non-API routes
app.use((req, res, next) => {
  if (!req.path.startsWith('/api')) {
    return res.sendFile(path.join(frontendPath, 'index.html'));
  }
  next();
});

// 404 + error handling
app.use(notFoundHandler);
app.use(errorHandler);

// --- Start server ---
app.listen(PORT, () => {
  logger.info(`Server started on http://localhost:${PORT}`);
});
