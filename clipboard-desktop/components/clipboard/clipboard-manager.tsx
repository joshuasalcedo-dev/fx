"use client";

import { useState, useEffect, useCallback } from "react";
import { toast } from "sonner";
import { clipboardOperations, type ClipboardDto } from "@/lib/api-client";
import ClipboardTabs from "./clipboard-tabs";
import { useWebSocketClipboard } from "@/hooks/use-websocket-clipboard";

export default function ClipboardManager() {
    const [entries, setEntries] = useState<ClipboardDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [mounted, setMounted] = useState(false);

    // Prevent hydration mismatch
    useEffect(() => {
        setMounted(true);
    }, []);

    // WebSocket handlers
    const handleNewEntry = useCallback((entry: ClipboardDto) => {
        setEntries(prev => {
            // Check if entry already exists
            const exists = prev.some(e => e.id === entry.id);
            if (exists) return prev;

            // Add new entry at the beginning
            return [entry, ...prev];
        });
        toast.success("New clipboard entry received");
    }, []);

    const handleUpdateEntry = useCallback((entry: ClipboardDto) => {
        setEntries(prev => prev.map(e =>
            e.id === entry.id ? entry : e
        ));
    }, []);

    const handleDeleteEntry = useCallback((entryId: number) => {
        setEntries(prev => prev.filter(e => e.id !== entryId));
    }, []);

    const handleClear = useCallback((includePinned: boolean) => {
        if (includePinned) {
            setEntries([]);
        } else {
            setEntries(prev => prev.filter(e => e.isPinned));
        }
        toast.info(includePinned ? "All entries cleared" : "Unpinned entries cleared");
    }, []);

    // Setup WebSocket connection
    const { isConnected } = useWebSocketClipboard({
        onNewEntry: handleNewEntry,
        onUpdateEntry: handleUpdateEntry,
        onDeleteEntry: handleDeleteEntry,
        onClear: handleClear,
        enabled: mounted
    });

    // Fetch entries
    const fetchEntries = async () => {
        setLoading(true);
        try {
            const data = await clipboardOperations.getEntries(0, 100);
            console.log('API Response:', data);
            setEntries(data.content || []);
        } catch (err) {
            console.error('Fetch error:', err);
            toast.error("Failed to fetch entries");
        } finally {
            setLoading(false);
        }
    };

    // Initial load
    useEffect(() => {
        if (!mounted) return;
        fetchEntries();
    }, [mounted]);

    // Toggle pin
    const togglePin = async (id: number) => {
        try {
            await clipboardOperations.togglePin(id);
            // The update will come through WebSocket
        } catch (err) {
            toast.error("Failed to update pin");
            // Fallback: refetch if WebSocket update doesn't arrive
            setTimeout(fetchEntries, 1000);
        }
    };

    // Delete entry
    const deleteEntry = async (id: number) => {
        try {
            await clipboardOperations.deleteEntry(id);
            // The deletion will come through WebSocket
        } catch (err) {
            toast.error("Failed to delete");
            // Fallback: refetch if WebSocket update doesn't arrive
            setTimeout(fetchEntries, 1000);
        }
    };

    // Delete all unpinned
    const deleteAllUnpinned = async () => {
        if (!confirm("Delete all unpinned entries?")) return;
        try {
            await clipboardOperations.deleteAllUnpinned();
            // The clear event will come through WebSocket
        } catch (err) {
            toast.error("Failed to clear");
            // Fallback: refetch if WebSocket update doesn't arrive
            setTimeout(fetchEntries, 1000);
        }
    };

    // Export
    const handleExport = async (format: "json" | "csv" | "txt") => {
        try {
            let data: any;
            switch (format) {
                case "json":
                    data = await clipboardOperations.exportAsJson();
                    break;
                case "csv":
                    data = await clipboardOperations.exportAsCsv();
                    break;
                case "txt":
                    data = await clipboardOperations.exportAsTxt();
                    break;
            }

            const blob = new Blob([data], {
                type: format === "json" ? "application/json" : "text/plain"
            });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `clipboard_${new Date().toISOString().split('T')[0]}.${format}`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            toast.success("Exported successfully");
        } catch (err) {
            toast.error("Export failed");
        }
    };

    if (!mounted) {
        return null;
    }

    // Get pinned entries
    const pinnedEntries = entries.filter(entry => entry.isPinned);

    return (
        <div className="h-full flex flex-col">
            {/* WebSocket Connection Status */}
            {mounted && (
                <div className="flex items-center justify-end px-3 py-1 bg-muted/30">
                    <div className="flex items-center gap-2 text-xs">
                        <div className={`w-2 h-2 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`} />
                        <span className="text-muted-foreground">
                            {isConnected ? 'Live' : 'Offline'}
                        </span>
                    </div>
                </div>
            )}

            <ClipboardTabs
                allEntries={entries}
                pinnedEntries={pinnedEntries}
                loading={loading}
                onPinToggle={togglePin}
                onDelete={deleteEntry}
                onDeleteAll={deleteAllUnpinned}
                onExport={handleExport}
                onRefresh={fetchEntries}
            />
        </div>
    );
}