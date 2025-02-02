import type { PiscinaTask } from '../../task_queue';
import type { PiscinaWorker } from '..';
export type PiscinaLoadBalancerCommand = {
    candidate?: PiscinaWorker | null;
    command?: 0 | 1;
};
export type PisicnaLoadBalancer = (task: PiscinaTask, workers: PiscinaWorker[]) => PiscinaLoadBalancerCommand;
export type ResourceBasedBalancerOptions = {
    maximumUsage: number;
};
export declare function ResourceBasedBalancer(opts: ResourceBasedBalancerOptions): PisicnaLoadBalancer;
