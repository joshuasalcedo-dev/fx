/**
 * This package contains the domain model classes and services for managing clipboard functionality.
 *
 * <p>The clipboard domain provides a comprehensive solution for storing, retrieving, and managing
 * clipboard entries. It supports features like pinning important entries, searching through
 * content, and automatic cleanup of old entries.
 *
 * <p>Key components in this package include:
 *
 * <ul>
 *   <li>ClipboardEntry - Entity representing a clipboard item with content and metadata
 *   <li>ClipboardRepository - Repository interface for persisting and querying clipboard entries
 *   <li>ClipboardService - Domain service providing business logic for clipboard operations
 * </ul>
 *
 * <p>The clipboard functionality allows users to:
 *
 * <ul>
 *   <li>Save clipboard content with duplicate detection
 *   <li>Pin important entries to prevent automatic cleanup
 *   <li>Search through clipboard history
 *   <li>View statistics about clipboard usage
 *   <li>Manage cleanup policies for old entries
 * </ul>
 *
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.domain.clipboard;
