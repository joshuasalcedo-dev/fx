/**
 * This package contains exception handling infrastructure for the application.
 *
 * <p>The classes in this package are responsible for global exception handling, providing a
 * consistent approach to error responses across the application. They translate various exceptions
 * into structured HTTP responses with appropriate status codes and error details.
 *
 * <p>Key components in this package include:
 *
 * <ul>
 *   <li>ClipboardExceptionHandler - A global exception handler using Spring's @RestControllerAdvice
 *       to intercept and process exceptions thrown during request handling
 *   <li>ErrorResponse - A structured response format for error information including status, error
 *       message, path, timestamp, and validation details
 * </ul>
 *
 * <p>The exception handling infrastructure provides:
 *
 * <ul>
 *   <li>Consistent error response format across the application
 *   <li>Appropriate HTTP status codes based on exception type
 *   <li>Detailed validation error information for invalid requests
 *   <li>Logging of exceptions with appropriate severity levels
 *   <li>Sanitized error messages for security and usability
 * </ul>
 *
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.infrastructure.exception;
