"use client"

import { ScrollArea } from "@/components/ui/scroll-area";
import ClipboardCard, { type ClipboardEntry } from "./clipboard-card";

interface ClipboardListProps {
    entries: ClipboardEntry[];
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
            <div className="text-center py-8 text-muted-foreground">
                Loading entries...
            </div>
        );
    }

    if (entries.length === 0) {
        return (
            <div className="text-center py-8 text-muted-foreground">
                No entries found
            </div>
        );
    }

    return (
        <ScrollArea className="h-[500px] pr-4">
            <div className="space-y-2">
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