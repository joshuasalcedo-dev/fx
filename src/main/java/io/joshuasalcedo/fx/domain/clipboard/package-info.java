/**
 * This package contains the domain model classes and services for managing clipboard functionality.
 * 
 * <p>The clipboard domain provides a comprehensive solution for storing, retrieving, and managing
 * clipboard entries. It supports features like pinning important entries, searching through content,
 * and automatic cleanup of old entries.</p>
 * 
 * <p>Key components in this package include:
 * <ul>
 *   <li>ClipboardEntry - Entity representing a clipboard item with content and metadata</li>
 *   <li>ClipboardRepository - Repository interface for persisting and querying clipboard entries</li>
 *   <li>ClipboardService - Domain service providing business logic for clipboard operations</li>
 * </ul>
 * </p>
 * 
 * <p>The clipboard functionality allows users to:
 * <ul>
 *   <li>Save clipboard content with duplicate detection</li>
 *   <li>Pin important entries to prevent automatic cleanup</li>
 *   <li>Search through clipboard history</li>
 *   <li>View statistics about clipboard usage</li>
 *   <li>Manage cleanup policies for old entries</li>
 * </ul>
 * </p>
 * 
 * @since 1.0.0-SNAPSHOT
 */
package io.joshuasalcedo.fx.domain.clipboard;