/**
 * This package contains event-related infrastructure components for the application.
 *
 * <p>The classes in this package are responsible for handling application events, particularly
 * those related to clipboard monitoring and event publishing. They serve as a bridge between the
 * system clipboard and the application's domain events.
 *
 * <p>Key components in this package include:
 *
 * <ul>
 *   <li>ClipboardRunner - An ApplicationRunner implementation that monitors clipboard changes and
 *       publishes domain events when changes are detected
 * </ul>
 *
 * <p>The event infrastructure provides mechanisms for:
 *
 * <ul>
 *   <li>Starting and stopping clipboard monitoring
 *   <li>Converting system clipboard changes to domain events
 *   <li>Publishing events to be consumed by other components
 *   <li>Logging and error handling for clipboard monitoring
 * </ul>
 *
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.infrastructure.events;
