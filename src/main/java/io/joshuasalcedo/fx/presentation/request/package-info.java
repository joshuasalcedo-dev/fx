/**
 * This package contains request objects for the presentation layer.
 *
 * <p>Request objects in this package represent client requests to the application, providing a
 * structured way to receive and validate input data from API clients. They encapsulate the data
 * required for specific operations.
 *
 * <p>Key components in this package include:
 *
 * <ul>
 *   <li>TogglePinRequest - A record representing a request to toggle the pin status of a clipboard
 *       entry, containing the entry's id
 * </ul>
 *
 * <p>These request objects provide several benefits:
 *
 * <ul>
 *   <li>Clear separation of input data from domain objects
 *   <li>Structured validation of client input
 *   <li>Documentation of required parameters for API operations
 *   <li>Simplified request handling in controllers
 * </ul>
 *
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.presentation.request;
