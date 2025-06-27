"use client";

import { useState, useEffect } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { toast } from "sonner";
import { AlertCircle } from "lucide-react";
import { clipboardOperations, type ClipboardStats, type ClipboardDto } from "@/lib/api-client";
import ClipboardStatsCards from "./clipboard-stats";
import ClipboardActions from "./clipboard-actions";
import ClipboardTabs from "./clipboard-tabs";

export default function ClipboardContainer() {
    const [entries, setEntries] = useState<ClipboardDto[]>([]);
    const [pinnedEntries, setPinnedEntries] = useState<ClipboardDto[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [stats, setStats] = useState<ClipboardStats>({
        totalEntries: 0,
        pinnedEntries: 0,
        unpinnedEntries: 0,
        oldestEntry: '',
        newestEntry: ''
    });

    // Fetch clipboard entries
    const fetchEntries = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await clipboardOperations.getEntries(0, 50);
            setEntries(data.content || []);
        } catch (err) {
            setError(err instanceof Error ? err.message : "Failed to fetch entries");
            toast.error("Failed to fetch clipboard entries");
        } finally {
            setLoading(false);
        }
    };

    // Fetch pinned entries
    const fetchPinnedEntries = async () => {
        try {
            const data = await clipboardOperations.getPinnedEntries();
            setPinnedEntries(data || []);
        } catch (err) {
            console.error("Failed to fetch pinned entries:", err);
        }
    };

    // Fetch stats
    const fetchStats = async () => {
        try {
            const data = await clipboardOperations.getStats();
            setStats(data);
        } catch (err) {
            console.error("Failed to fetch stats:", err);
        }
    };

    // Initial load
    useEffect(() => {
        fetchEntries();
        fetchPinnedEntries();
        fetchStats();

        // Set up WebSocket connection for real-time updates
        const ws = new WebSocket("ws://localhost:5000/ws-clipboard");

        ws.onmessage = (event) => {
            const message = JSON.parse(event.data);
            if (message.type === "NEW_ENTRY" || message.type === "UPDATE" || message.type === "DELETE") {
                fetchEntries();
                fetchPinnedEntries();
                fetchStats();
            }
        };

        return () => {
            ws.close();
        };
    }, []);

    // Toggle pin status
    const togglePin = async (id: number) => {
        try {
            await clipboardOperations.togglePin(id);
            toast.success("Pin status updated");
            fetchEntries();
            fetchPinnedEntries();
            fetchStats();
        } catch (err) {
            toast.error("Failed to toggle pin status");
        }
    };

    // Delete entry
    const deleteEntry = async (id: number) => {
        try {
            await clipboardOperations.deleteEntry(id);
            toast.success("Entry deleted");
            fetchEntries();
            fetchPinnedEntries();
            fetchStats();
        } catch (err) {
            toast.error("Failed to delete entry");
        }
    };

    // Delete all entries
    const deleteAll = async () => {
        if (!confirm("Are you sure you want to delete all unpinned entries?")) return;

        try {
            await clipboardOperations.deleteAllUnpinned();
            toast.success("All unpinned entries deleted");
            fetchEntries();
            fetchStats();
        } catch (err) {
            toast.error("Failed to delete entries");
        }
    };

    // Export entries
    const exportEntries = async (format: "json" | "csv" | "txt") => {
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

            const blob = new Blob([data], { type: format === "json" ? "application/json" : "text/plain" });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `clipboard_export.${format}`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);

            toast.success(`Exported as ${format.toUpperCase()}`);
        } catch (err) {
            toast.error("Failed to export entries");
        }
    };

    // Filter entries based on search
    const filteredEntries = entries.filter(entry =>
        entry.content.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <>
            <ClipboardStatsCards stats={stats} />

            <ClipboardActions
                searchTerm={searchTerm}
                onSearchChange={setSearchTerm}
                onRefresh={fetchEntries}
                onDeleteAll={deleteAll}
                onExport={exportEntries}
            />

            {error && (
                <Alert variant="destructive" className="mb-6">
                    <AlertCircle className="h-4 w-4" />
                    <AlertTitle>Error</AlertTitle>
                    <AlertDescription>{error}</AlertDescription>
                </Alert>
            )}

            <ClipboardTabs
                allEntries={filteredEntries}
                pinnedEntries={pinnedEntries}
                loading={loading}
                onPinToggle={togglePin}
                onDelete={deleteEntry}
            />
        </>
    );
}