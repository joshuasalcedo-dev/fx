// components/clipboard/clipboard-tabs.tsx
"use client";

import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, RefreshCw, MoreHorizontal, Trash2 } from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import ClipboardList from "./clipboard-list";
import type { ClipboardDto } from "@/lib/api-client";

interface ClipboardTabsProps {
    allEntries: ClipboardDto[];
    pinnedEntries: ClipboardDto[];
    loading: boolean;
    onPinToggle: (id: number) => void;
    onDelete: (id: number) => void;
    onDeleteAll: () => void;
    onExport: (format: "json" | "csv" | "txt") => void;
    onRefresh: () => void;
}

export default function ClipboardTabs({
                                          allEntries,
                                          pinnedEntries,
                                          loading,
                                          onPinToggle,
                                          onDelete,
                                          onDeleteAll,
                                          onExport,
                                          onRefresh
                                      }: ClipboardTabsProps) {
    const [searchTerm, setSearchTerm] = useState("");
    const [showPinnedOnly, setShowPinnedOnly] = useState(false);

    // Listen for filter changes from title bar
    useEffect(() => {
        const handleFilterChange = (event: any) => {
            setShowPinnedOnly(event.detail.showPinnedOnly);
        };

        window.addEventListener('clipboard-filter-change', handleFilterChange);
        return () => {
            window.removeEventListener('clipboard-filter-change', handleFilterChange);
        };
    }, []);

    // Filter entries based on search
    const filterEntries = (entries: ClipboardDto[]) => {
        return entries.filter(entry =>
            entry.content.toLowerCase().includes(searchTerm.toLowerCase())
        );
    };

    // Sort entries: pinned first, then by date
    const sortEntries = (entries: ClipboardDto[]) => {
        return [...entries].sort((a, b) => {
            if (a.isPinned && !b.isPinned) return -1;
            if (!a.isPinned && b.isPinned) return 1;
            return new Date(b.localDateTime).getTime() - new Date(a.localDateTime).getTime();
        });
    };

    // Get the entries to display based on current filter
    const entriesToDisplay = showPinnedOnly
        ? filterEntries(pinnedEntries)
        : sortEntries(filterEntries(allEntries));

    return (
        <div className="h-full flex flex-col p-3">
            {/* Search and Actions Bar */}
            <div className="flex gap-2 mb-3">
                <div className="relative flex-1">
                    <Search className="absolute left-2 top-2 h-3 w-3 text-muted-foreground" />
                    <Input
                        placeholder="Search..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-7 h-8 text-sm"
                    />
                </div>
                <Button
                    onClick={onRefresh}
                    variant="ghost"
                    size="sm"
                    className="h-8 w-8"
                    disabled={loading}
                >
                    <RefreshCw className={`h-3 w-3 ${loading ? 'animate-spin' : ''}`} />
                </Button>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm" className="h-8 w-8">
                            <MoreHorizontal className="h-3 w-3" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem onClick={onDeleteAll} className="text-destructive">
                            <Trash2 className="h-3 w-3 mr-2" />
                            Clear Unpinned
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem onClick={() => onExport("json")}>
                            Export as JSON
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => onExport("csv")}>
                            Export as CSV
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => onExport("txt")}>
                            Export as TXT
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>

            {/* Clipboard List */}
            <div className="flex-1 overflow-hidden">
                <ClipboardList
                    entries={entriesToDisplay}
                    loading={loading}
                    onPinToggle={onPinToggle}
                    onDelete={onDelete}
                />
            </div>
        </div>
    );
}