"use client"

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, RefreshCw, Trash2, MoreHorizontal } from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

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
        <div className="flex gap-2 mb-4">
            <div className="flex-1">
                <div className="relative">
                    <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Search..."
                        value={searchTerm}
                        onChange={(e) => onSearchChange(e.target.value)}
                        className="pl-8 h-9"
                    />
                </div>
            </div>
            <Button onClick={onRefresh} variant="ghost" size="icon" className="h-9 w-9">
                <RefreshCw className="h-4 w-4" />
            </Button>
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant="ghost" size="icon" className="h-9 w-9">
                        <MoreHorizontal className="h-4 w-4" />
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuItem onClick={onDeleteAll} className="text-destructive">
                        <Trash2 className="h-4 w-4 mr-2" />
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
    );
}