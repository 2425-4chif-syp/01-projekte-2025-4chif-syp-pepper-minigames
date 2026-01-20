const { createLogger, format, transports } = require('winston');
const path = require('path');
const fs = require('fs');
const crypto = require('crypto');
const dotenv = require('dotenv');

// Umgebungsvariablen laden
dotenv.config();

// Funktion zum Erstellen eines eindeutigen Log-Ordners
function createLogFolder() {
  const timestamp = new Date().toISOString().replace(/[-T:.Z]/g, '_');
  const randomId = crypto.randomBytes(3).toString('hex');
  const logFolder = path.join(__dirname, '../logs', `Logs_${timestamp}_${randomId}`);
  fs.mkdirSync(logFolder, { recursive: true });
  return logFolder;
}

// Erstelle den Logs-Ordner für die aktuelle Session
const logFolder = createLogFolder();

// Custom-Log-Format für Dateien
const fileLogFormat = format.printf(({ timestamp, level, message }) => {
  return `${timestamp.replace('T', '_').replace('Z', '').split('.')[0]} ${level.toUpperCase()} ${message}`;
});

// Custom-Log-Format für die Konsole
const consoleLogFormat = format.printf(({ level, message }) => {
  return `${level.toUpperCase()}: ${message}`;
});

// Konfiguration für Debug-Logs aus der .env-Datei
const DEBUG_LOGS = process.env.DEBUG_LOGS || 'OFF';

// Logger-Definition
const logger = createLogger({
  level: 'debug', // Mindest-Loglevel
  format: format.combine(
    format.timestamp({ format: 'YYYY-MM-DD_HH:mm:ss' }),
    fileLogFormat // Standardformat für Dateien
  ),
  transports: [
    // Debug-Logs in Datei, wenn DEBUG_LOGS "FILE" oder "BOTH" ist
    ...(DEBUG_LOGS === 'BOTH' || DEBUG_LOGS === 'FILE'
      ? [
          new transports.File({
            filename: path.join(logFolder, 'debug.log'),
            level: 'debug',
          }),
        ]
      : []),
    // Info-Logs in Datei
    new transports.File({
      filename: path.join(logFolder, 'info.log'),
      level: 'info',
    }),
    // Warn-Logs in Datei
    new transports.File({
      filename: path.join(logFolder, 'warning.log'),
      level: 'warn',
    }),
    // Error-Logs in Datei
    new transports.File({
      filename: path.join(logFolder, 'error.log'),
      level: 'error',
    }),
    // Combined-Logs in Datei (alles ab "info")
    new transports.File({
      filename: path.join(logFolder, 'combined.log'),
      level: 'info',
    }),
    // Konsolenausgabe abhängig von DEBUG_LOGS
    ...(DEBUG_LOGS === 'BOTH'
      ? [
          new transports.Console({
            level: 'debug', // Alle Logs ab Debug-Level
            format: consoleLogFormat,
          }),
        ]
      : DEBUG_LOGS !== 'OFF'
      ? [
          new transports.Console({
            level: 'info', // Alle Logs ab Info-Level
            format: consoleLogFormat,
          }),
        ]
      : []),
  ],
});

// Debug-Level komplett deaktivieren, wenn DEBUG_LOGS "OFF" ist
if (DEBUG_LOGS === 'OFF') {
  logger.transports.forEach((transport) => {
    if (transport.level === 'debug') {
      transport.silent = true; // Debug-Logs werden nicht ausgegeben
    }
  });
}

// Logger exportieren
module.exports = logger;