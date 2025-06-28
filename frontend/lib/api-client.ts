// API Client wrapper for easy usage in Next.js components
// This file provides a convenient way to use the generated OpenAPI client

import { Configuration } from './client/configuration';
import { ClipboardApi, ClipboardExportApi, HealthApi } from './client/api';
import type { Page, ClipboardDto as GeneratedClipboardDto } from './client/models';

// Configuration for the API client
const config = new Configuration({
    basePath: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:5000',
});

// Export pre-configured API instances
export const clipboardApi = new ClipboardApi(config);
export const clipboardExportApi = new ClipboardExportApi(config);
export const healthApi = new HealthApi(config);

// Import types from generated models

// Re-export types
export type { TogglePinRequest, Page } from './client/models';

// Create a proper ClipboardDto interface that matches what components expect
export interface ClipboardDto {
    id: number;
    content: string;
    localDateTime: string;
    isPinned: boolean;
}

// Create a proper ClipboardStats interface with required properties
export interface ClipboardStats {
    totalEntries: number;
    pinnedEntries: number;
    unpinnedEntries: number;
    oldestEntry: string;
    newestEntry: string;
}

// Helper to convert generated DTO to our interface
function toClipboardDto(dto: GeneratedClipboardDto): ClipboardDto {
    return {
        id: dto.id || 0,
        content: dto.content || '',
        localDateTime: dto.localDateTime || new Date().toISOString(),
        isPinned: dto.isPinned || false
    };
}

// Utility functions for common operations
export const clipboardOperations = {
    // Get all clipboard entries with pagination
    async getEntries(page = 0, size = 50): Promise<Page> {
        try {
            const response = await clipboardApi.clipboards(page, size);
            const data = response.data;

            // Ensure content array contains properly typed items
            if (data.content) {
                data.content = data.content.map((item: any) => toClipboardDto(item));
            }

            return data;
        } catch (error) {
            console.error('Error fetching clipboard entries:', error);
            throw error;
        }
    },

    // Get pinned entries
    async getPinnedEntries(): Promise<ClipboardDto[]> {
        try {
            const response = await clipboardApi.pinnedClipboards();
            const data: any = response.data;
            
            // If data is already an array, use it directly
            if (Array.isArray(data)) {
                return data.map((item: any) => toClipboardDto(item));
            }
            
            // If data is an object but not an array, return empty array
            if (typeof data === 'object' && data !== null) {
                return [];
            }
            
            // If data is a string, try to parse it as JSON
            if (typeof data === 'string') {
                try {
                    const parsed = JSON.parse(data);
                    return Array.isArray(parsed)
                        ? parsed.map((item: any) => toClipboardDto(item))
                        : [];
                } catch (parseError) {
                    console.error('Error parsing pinned entries JSON:', parseError);
                    return [];
                }
            }
            
            // Fallback: return empty array
            return [];
        } catch (error) {
            console.error('Error fetching pinned entries:', error);
            throw error;
        }
    },

    // Search entries
    async searchEntries(query: string, page = 0, size = 50): Promise<Page> {
        try {
            const response = await clipboardApi.searchClipboards(query, page, size);
            const data = response.data;

            // Ensure content array contains properly typed items
            if (data.content) {
                data.content = data.content.map((item: any) => toClipboardDto(item));
            }

            return data;
        } catch (error) {
            console.error('Error searching clipboard entries:', error);
            throw error;
        }
    },

    // Toggle pin status
    async togglePin(id: number): Promise<ClipboardDto> {
        try {
            const response = await clipboardApi.pinClipboards({ id });
            return toClipboardDto(response.data);
        } catch (error) {
            console.error('Error toggling pin status:', error);
            throw error;
        }
    },

    // Delete entry
    async deleteEntry(id: number): Promise<void> {
        try {
            await clipboardApi.deleteClipboard(id);
        } catch (error) {
            console.error('Error deleting entry:', error);
            throw error;
        }
    },

    // Delete all unpinned entries
    async deleteAllUnpinned(): Promise<void> {
        try {
            await clipboardApi.deleteAllClipboards(false);
        } catch (error) {
            console.error('Error deleting all entries:', error);
            throw error;
        }
    },

    // Get statistics
    async getStats(): Promise<ClipboardStats> {
        try {
            const response = await clipboardApi.getClipboardStats();
            const data = response.data;

            // Convert optional properties to required with defaults
            return {
                totalEntries: data.totalEntries || 0,
                pinnedEntries: data.pinnedEntries || 0,
                unpinnedEntries: data.unpinnedEntries || 0,
                oldestEntry: data.oldestEntry || '',
                newestEntry: data.newestEntry || ''
            };
        } catch (error) {
            console.error('Error fetching stats:', error);
            throw error;
        }
    },

    // Export entries
    async exportAsJson(includePinned = true): Promise<string> {
        try {
            const response = await clipboardExportApi.exportAsJson(includePinned);
            // Handle response data which might be void or any format
            return typeof response.data === 'string'
                ? response.data
                : JSON.stringify(response.data);
        } catch (error) {
            console.error('Error exporting as JSON:', error);
            throw error;
        }
    },

    async exportAsCsv(includePinned = true): Promise<string> {
        try {
            const response = await clipboardExportApi.exportAsCsv(includePinned);
            // Handle response data which might be void or any format
            return typeof response.data === 'string'
                ? response.data
                : JSON.stringify(response.data);
        } catch (error) {
            console.error('Error exporting as CSV:', error);
            throw error;
        }
    },

    async exportAsTxt(includePinned = true): Promise<string> {
        try {
            const response = await clipboardExportApi.exportAsText(includePinned);
            // Handle response data which might be void or any format
            return typeof response.data === 'string'
                ? response.data
                : JSON.stringify(response.data);
        } catch (error) {
            console.error('Error exporting as TXT:', error);
            throw error;
        }
    }
};

// Health check utility
export const healthCheck = {
    async check(): Promise<any> {
        try {
            const response = await healthApi.health();
            return response.data;
        } catch (error) {
            console.error('Error checking health:', error);
            throw error;
        }
    }
};
