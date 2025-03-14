'use strict';
/**
 * @license Angular v19.2.0
 * (c) 2010-2024 Google LLC. https://angular.io/
 * License: MIT
 */
'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var schematics = require('@angular-devkit/schematics');
var signalQueriesMigration = require('./signal-queries-migration.js');
var signalInputMigration = require('./signal-input-migration.js');
var outputMigration = require('./output-migration.js');
require('./project_tsconfig_paths-6c9cde78.js');
require('@angular-devkit/core');
require('./project_paths-64bc3947.js');
require('node:path/posix');
require('os');
require('typescript');
require('./checker-2eecc677.js');
require('fs');
require('module');
require('path');
require('url');
require('./program-24da9092.js');
require('./apply_import_manager-a4e62ded.js');
require('./migrate_ts_type_references-1abf1f5f.js');
require('assert');
require('./index-afc3f749.js');
require('./leading_space-6e7a8ec6.js');

function migrate(options) {
    // The migrations are independent so we can run them in any order, but we sort them here
    // alphabetically so we get a consistent execution order in case of issue reports.
    const migrations = options.migrations.slice().sort();
    const rules = [];
    for (const migration of migrations) {
        switch (migration) {
            case "inputs" /* SupportedMigrations.inputs */:
                rules.push(signalInputMigration.migrate(options));
                break;
            case "outputs" /* SupportedMigrations.outputs */:
                rules.push(outputMigration.migrate(options));
                break;
            case "queries" /* SupportedMigrations.queries */:
                rules.push(signalQueriesMigration.migrate(options));
                break;
            default:
                throw new schematics.SchematicsException(`Unsupported migration "${migration}"`);
        }
    }
    return schematics.chain(rules);
}

exports.migrate = migrate;
