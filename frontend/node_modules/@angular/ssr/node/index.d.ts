import { ApplicationRef } from '@angular/core';
import type { Http2ServerRequest } from 'node:http2';
import type { Http2ServerResponse } from 'node:http2';
import type { IncomingMessage } from 'node:http';
import type { ServerResponse } from 'node:http';
import { StaticProvider } from '@angular/core';
import { Type } from '@angular/core';

/**
 * Angular server application engine.
 * Manages Angular server applications (including localized ones), handles rendering requests,
 * and optionally transforms index HTML before rendering.
 *
 * @remarks This class should be instantiated once and used as a singleton across the server-side
 * application to ensure consistent handling of rendering requests and resource management.
 *
 * @developerPreview
 */
export declare class AngularNodeAppEngine {
    private readonly angularAppEngine;
    /**
     * Handles an incoming HTTP request by serving prerendered content, performing server-side rendering,
     * or delivering a static file for client-side rendered routes based on the `RenderMode` setting.
     *
     * This method adapts Node.js's `IncomingMessage` or `Http2ServerRequest`
     * to a format compatible with the `AngularAppEngine` and delegates the handling logic to it.
     *
     * @param request - The incoming HTTP request (`IncomingMessage` or `Http2ServerRequest`).
     * @param requestContext - Optional context for rendering, such as metadata associated with the request.
     * @returns A promise that resolves to the resulting HTTP response object, or `null` if no matching Angular route is found.
     *
     * @remarks A request to `https://www.example.com/page/index.html` will serve or render the Angular route
     * corresponding to `https://www.example.com/page`.
     */
    handle(request: IncomingMessage | Http2ServerRequest, requestContext?: unknown): Promise<Response | null>;
}

/**
 * A common engine to use to server render an application.
 */
export declare class CommonEngine {
    private options?;
    private readonly templateCache;
    private readonly inlineCriticalCssProcessor;
    private readonly pageIsSSG;
    constructor(options?: CommonEngineOptions | undefined);
    /**
     * Render an HTML document for a specific URL with specified
     * render options
     */
    render(opts: CommonEngineRenderOptions): Promise<string>;
    private inlineCriticalCss;
    private retrieveSSGPage;
    private renderApplication;
    /** Retrieve the document from the cache or the filesystem */
    private getDocument;
}

export declare interface CommonEngineOptions {
    /** A method that when invoked returns a promise that returns an `ApplicationRef` instance once resolved or an NgModule. */
    bootstrap?: Type<{}> | (() => Promise<ApplicationRef>);
    /** A set of platform level providers for all requests. */
    providers?: StaticProvider[];
    /** Enable request performance profiling data collection and printing the results in the server console. */
    enablePerformanceProfiler?: boolean;
}

export declare interface CommonEngineRenderOptions {
    /** A method that when invoked returns a promise that returns an `ApplicationRef` instance once resolved or an NgModule. */
    bootstrap?: Type<{}> | (() => Promise<ApplicationRef>);
    /** A set of platform level providers for the current request. */
    providers?: StaticProvider[];
    url?: string;
    document?: string;
    documentFilePath?: string;
    /**
     * Reduce render blocking requests by inlining critical CSS.
     * Defaults to true.
     */
    inlineCriticalCss?: boolean;
    /**
     * Base path location of index file.
     * Defaults to the 'documentFilePath' dirname when not provided.
     */
    publicPath?: string;
}

/**
 * Attaches metadata to the handler function to mark it as a special handler for Node.js environments.
 *
 * @typeParam T - The type of the handler function.
 * @param handler - The handler function to be defined and annotated.
 * @returns The same handler function passed as an argument, with metadata attached.
 *
 * @example
 * Usage in an Express application:
 * ```ts
 * const app = express();
 * export default createNodeRequestHandler(app);
 * ```
 *
 * @example
 * Usage in a Hono application:
 * ```ts
 * const app = new Hono();
 * export default createNodeRequestHandler(async (req, res, next) => {
 *   try {
 *     const webRes = await app.fetch(createWebRequestFromNodeRequest(req));
 *     if (webRes) {
 *       await writeResponseToNodeResponse(webRes, res);
 *     } else {
 *       next();
 *     }
 *   } catch (error) {
 *     next(error);
 *   }
 * }));
 * ```
 *
 * @example
 * Usage in a Fastify application:
 * ```ts
 * const app = Fastify();
 * export default createNodeRequestHandler(async (req, res) => {
 *   await app.ready();
 *   app.server.emit('request', req, res);
 *   res.send('Hello from Fastify with Node Next Handler!');
 * }));
 * ```
 * @developerPreview
 */
export declare function createNodeRequestHandler<T extends NodeRequestHandlerFunction>(handler: T): T;

/**
 * Converts a Node.js `IncomingMessage` or `Http2ServerRequest` into a
 * Web Standard `Request` object.
 *
 * This function adapts the Node.js request objects to a format that can
 * be used by web platform APIs.
 *
 * @param nodeRequest - The Node.js request object (`IncomingMessage` or `Http2ServerRequest`) to convert.
 * @returns A Web Standard `Request` object.
 * @developerPreview
 */
export declare function createWebRequestFromNodeRequest(nodeRequest: IncomingMessage | Http2ServerRequest): Request;


/**
 * Determines whether the provided URL represents the main entry point module.
 *
 * This function checks if the provided URL corresponds to the main ESM module being executed directly.
 * It's useful for conditionally executing code that should only run when a module is the entry point,
 * such as starting a server or initializing an application.
 *
 * It performs two key checks:
 * 1. Verifies if the URL starts with 'file:', ensuring it is a local file.
 * 2. Compares the URL's resolved file path with the first command-line argument (`process.argv[1]`),
 *    which points to the file being executed.
 *
 * @param url The URL of the module to check. This should typically be `import.meta.url`.
 * @returns `true` if the provided URL represents the main entry point, otherwise `false`.
 * @developerPreview
 */
export declare function isMainModule(url: string): boolean;

/**
 * Represents a middleware function for handling HTTP requests in a Node.js environment.
 *
 * @param req - The incoming HTTP request object.
 * @param res - The outgoing HTTP response object.
 * @param next - A callback function that signals the completion of the middleware or forwards the error if provided.
 *
 * @returns A Promise that resolves to void or simply void. The handler can be asynchronous.
 * @developerPreview
 */
export declare type NodeRequestHandlerFunction = (req: IncomingMessage, res: ServerResponse, next: (err?: unknown) => void) => Promise<void> | void;

/**
 * Streams a web-standard `Response` into a Node.js `ServerResponse`
 * or `Http2ServerResponse`.
 *
 * This function adapts the web `Response` object to write its content
 * to a Node.js response object, handling both HTTP/1.1 and HTTP/2.
 *
 * @param source - The web-standard `Response` object to stream from.
 * @param destination - The Node.js response object (`ServerResponse` or `Http2ServerResponse`) to stream into.
 * @returns A promise that resolves once the streaming operation is complete.
 * @developerPreview
 */
export declare function writeResponseToNodeResponse(source: Response, destination: ServerResponse | Http2ServerResponse): Promise<void>;

export { }
