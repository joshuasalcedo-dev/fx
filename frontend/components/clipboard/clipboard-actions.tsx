"use client"

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, RefreshCw, Trash2, Download } from "lucide-react";

interface ClipboardActionsProps {
    searchTerm: string;
    onSearchChange: (value: string) => void;
    onRefresh: () => void;
    onDeleteAll: () => void;
    onExport: (format: "json" | "csv" | "txt") => void;
}

export default function ClipboardActions({
                                             searchTerm,
                                             onSearchChange,
                                             onRefresh,
                                             onDeleteAll,
                                             onExport
                                         }: ClipboardActionsProps) {
    return (
        <div className="flex gap-2 mb-6 flex-wrap">
            <div className="flex-1 min-w-[200px]">
                <div className="relative">
                    <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Search clipboard entries..."
                        value={searchTerm}
                        onChange={(e) => onSearchChange(e.target.value)}
                        className="pl-8"
                    />
                </div>
            </div>
            <Button onClick={onRefresh} variant="outline" size="icon">
                <RefreshCw className="h-4 w-4" />
            </Button>
            <Button onClick={onDeleteAll} variant="destructive" size="sm">
                <Trash2 className="h-4 w-4 mr-2" />
                Clear Unpinned
            </Button>
            <div className="flex gap-1">
                <Button onClick={() => onExport("json")} variant="outline" size="sm">
                    <Download className="h-4 w-4 mr-2" />
                    JSON
                </Button>
                <Button onClick={() => onExport("csv")} variant="outline" size="sm">
                    <Download className="h-4 w-4 mr-2" />
                    CSV
                </Button>
                <Button onClick={() => onExport("txt")} variant="outline" size="sm">
                    <Download className="h-4 w-4 mr-2" />
                    TXT
                </Button>
            </div>
        </div>
    );
}