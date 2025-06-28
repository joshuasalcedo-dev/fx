/**
 * This package contains request objects for the presentation layer.
 * 
 * <p>Request objects in this package represent client requests to the application,
 * providing a structured way to receive and validate input data from API clients.
 * They encapsulate the data required for specific operations.</p>
 * 
 * <p>Key components in this package include:
 * <ul>
 *   <li>TogglePinRequest - A record representing a request to toggle the pin status
 *       of a clipboard entry, containing the entry's id</li>
 * </ul>
 * </p>
 * 
 * <p>These request objects provide several benefits:
 * <ul>
 *   <li>Clear separation of input data from domain objects</li>
 *   <li>Structured validation of client input</li>
 *   <li>Documentation of required parameters for API operations</li>
 *   <li>Simplified request handling in controllers</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.presentation.request;