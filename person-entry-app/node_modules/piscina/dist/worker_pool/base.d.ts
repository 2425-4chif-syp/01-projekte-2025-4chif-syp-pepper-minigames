export declare abstract class AsynchronouslyCreatedResource {
    onreadyListeners: (() => void)[] | null;
    ondestroyListeners: (() => void)[] | null;
    markAsReady(): void;
    isReady(): boolean;
    onReady(fn: () => void): void;
    onDestroy(fn: () => void): void;
    markAsDestroyed(): void;
    abstract currentUsage(): number;
}
export declare class AsynchronouslyCreatedResourcePool<T extends AsynchronouslyCreatedResource> {
    pendingItems: Set<T>;
    readyItems: Set<T>;
    maximumUsage: number;
    onAvailableListeners: ((item: T) => void)[];
    onTaskDoneListeners: ((item: T) => void)[];
    constructor(maximumUsage: number);
    add(item: T): void;
    delete(item: T): void;
    findAvailable(): T | null;
    [Symbol.iterator](): Generator<T, void, undefined>;
    get size(): number;
    maybeAvailable(item: T): void;
    onAvailable(fn: (item: T) => void): void;
    taskDone(item: T): void;
    onTaskDone(fn: (item: T) => void): void;
    getCurrentUsage(): number;
}
