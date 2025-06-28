/**
 * This package contains Data Transfer Objects (DTOs) for the presentation layer.
 * 
 * <p>DTOs in this package are used to transfer data between the application and client,
 * providing a clean separation between the domain model and the presentation layer.
 * They are specifically designed for data exchange in API responses and WebSocket messages.</p>
 * 
 * <p>Key components in this package include:
 * <ul>
 *   <li>ClipboardDto - A record representing clipboard data for presentation purposes,
 *       including id, timestamp, content, and pin status</li>
 * </ul>
 * </p>
 * 
 * <p>These DTOs provide several benefits:
 * <ul>
 *   <li>Decoupling the domain model from client-facing interfaces</li>
 *   <li>Controlling which data is exposed to clients</li>
 *   <li>Optimizing data transfer by including only necessary fields</li>
 *   <li>Providing a stable contract for API consumers</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.presentation.dto;