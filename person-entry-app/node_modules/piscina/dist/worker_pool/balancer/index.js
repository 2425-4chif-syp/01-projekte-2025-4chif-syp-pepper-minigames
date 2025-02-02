"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ResourceBasedBalancer = void 0;
function ResourceBasedBalancer(opts) {
    const { maximumUsage } = opts;
    return (task, workers) => {
        const command = { candidate: null, command: 1 };
        let checkpoint = maximumUsage;
        for (const worker of workers) {
            if (worker.currentUsage === 0) {
                command.candidate = worker;
                break;
            }
            if (worker.isRunningAbortableTask)
                continue;
            if (!task.isAbortable &&
                (worker.currentUsage < checkpoint)) {
                command.candidate = worker;
                checkpoint = worker.currentUsage;
            }
        }
        return command;
    };
}
exports.ResourceBasedBalancer = ResourceBasedBalancer;
//# sourceMappingURL=index.js.map