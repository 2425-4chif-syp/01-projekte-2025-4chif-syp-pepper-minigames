const logger = require('../utils/logger');

// 404-Handler: Wird ausgelöst, wenn keine Route gefunden wird
function notFoundHandler(req, res, next) {
  logger.warn(`404 Not Found - ${req.method} ${req.url}`);
  res.status(404).json({
    error: 'Not Found',
    message: `The requested resource ${req.originalUrl} was not found on this server.`,
  });
}

// Allgemeiner Fehler-Handler
function errorHandler(err, req, res, next) {
  logger.error(
    `${req.method} ${req.url} - Status: ${err.status || 500} - Message: ${err.message}`
  );

  // Spezifische HTTP-Fehlerbehandlung
  if (err.status === 403) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'You do not have permission to access this resource.',
    });
  }

  if (err.status === 401) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Authentication is required or failed.',
    });
  }

  // ValidationError (z. B. durch express-validator)
  if (err.name === 'ValidationError') {
    return res.status(400).json({
      error: 'Validation Error',
      message: "There was a validation error. Please check your input data and try again.",
    });
  }

  // SyntaxError (z. B. fehlerhafter JSON-Body)
  if (err.name === 'SyntaxError' && err.body) {
    return res.status(400).json({
      error: 'Malformed JSON',
      message: 'The request body contains invalid JSON.',
    });
  }

  // Default-Fehlerbehandlung für unbekannte Fehler
  return res.status(err.status || 500).json({
    error: 'Internal Server Error',
    message: 'Something went wrong. Please try again later.',
  });
}

module.exports = { notFoundHandler, errorHandler };