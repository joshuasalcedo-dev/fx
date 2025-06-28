// components/clipboard/clipboard-list.tsx
"use client";

import { ScrollArea } from "@/components/ui/scroll-area";
import ClipboardCard from "./clipboard-card";
import type { ClipboardDto } from "@/lib/api-client";

interface ClipboardListProps {
    entries: ClipboardDto[];
    loading?: boolean;
    onPinToggle: (id: number) => void;
    onDelete: (id: number) => void;
}

export default function ClipboardList({
                                          entries,
                                          loading = false,
                                          onPinToggle,
                                          onDelete
                                      }: ClipboardListProps) {
    if (loading) {
        return (
            <div className="text-center py-8 text-muted-foreground text-sm">
                Loading entries...
            </div>
        );
    }

    if (entries.length === 0) {
        return (
            <div className="text-center py-8 text-muted-foreground text-sm">
                No entries found
            </div>
        );
    }

    return (
        <ScrollArea className="h-full">
            <div className="space-y-2 pr-4">
                {entries.map((entry) => (
                    <ClipboardCard
                        key={entry.id}
                        entry={entry}
                        onPinToggle={onPinToggle}
                        onDelete={onDelete}
                    />
                ))}
            </div>
        </ScrollArea>
    );
}