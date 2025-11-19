const logger = require('./logger');
const config = require('./config');
const dayjs = require('dayjs');

/**
 * Berechnet die aktuelle ReferenceWeek (1–n), basierend auf dem ersten Montag im Jahr.
 * @returns {Promise<{StartOfFirstWeek: string, CurrentWeek: number}>}
 */
async function getReferenceWeek() {
  const today = dayjs().startOf('day');
  const startOfCycle = dayjs(config.START_DATE).startOf('day');

  const diffWeeks = Math.floor(today.diff(startOfCycle, 'day') / 7);
  const currentWeek = ((diffWeeks % config.CYCLE_LENGTH + config.CYCLE_LENGTH) % config.CYCLE_LENGTH) + 1;

  const weekCount = diffWeeks + 1;

  logger.debug(`Calculated ReferenceWeek: ${currentWeek} (start: ${startOfCycle.format('YYYY-MM-DD')}, weekCount: ${weekCount}, cycleLength: ${config.CYCLE_LENGTH} weeks)`);

  return {
    StartOfFirstWeek: startOfCycle.format('YYYY-MM-DD'),
    CurrentWeek: currentWeek
  };
}

/**
 * Berechnet die ReferenceWeek für ein beliebiges Datum
 * @param {string} date Datum im Format 'YYYY-MM-DD'
 * @returns {Promise<number>}
 */
async function getReferenceWeekFromDate(date) {
  const targetDate = dayjs(date).startOf('day');
  const startOfCycle = dayjs(config.START_DATE).startOf('day');

  const diffWeeks = Math.floor(targetDate.diff(startOfCycle, 'day') / 7);
  const referenceWeek = ((diffWeeks % config.CYCLE_LENGTH + config.CYCLE_LENGTH) % config.CYCLE_LENGTH) + 1;

  logger.debug(`ReferenceWeek for ${date} is ${referenceWeek} (start: ${startOfCycle.format('YYYY-MM-DD')})`);
  return referenceWeek;
}

/**
 * Gibt die konfigurierte Zykluslänge zurück
 * @returns {number}
 */
function getCycleLength() {
  return config.CYCLE_LENGTH;
}

logger.info(`Menuplan cyclelength is set to: ${config.CYCLE_LENGTH} Weeks`);

module.exports = {
  getReferenceWeek,
  getReferenceWeekFromDate,
  getCycleLength
};