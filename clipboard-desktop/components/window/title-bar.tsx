// components/window/title-bar.tsx
import { Button } from "@/components/ui/button";
import { X, Pin, Inbox, Settings } from "lucide-react";
import { useState, useEffect } from "react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export default function TitleBar() {
    const [showPinnedOnly, setShowPinnedOnly] = useState(false);

    // Emit event when filter changes
    useEffect(() => {
        window.dispatchEvent(new CustomEvent('clipboard-filter-change', {
            detail: { showPinnedOnly }
        }));
    }, [showPinnedOnly]);

    return (
        <div className="h-8 bg-background/95 backdrop-blur border-b flex items-center justify-between select-none drag-region">
            <div className="flex items-center gap-2 px-3 no-drag">
                <Button
                    variant={showPinnedOnly ? "ghost" : "secondary"}
                    size="icon"
                    className="h-6 w-6"
                    onClick={() => setShowPinnedOnly(false)}
                    title="All entries"
                >
                    <Inbox className="h-3 w-3" />
                </Button>
                <Button
                    variant={showPinnedOnly ? "secondary" : "ghost"}
                    size="icon"
                    className="h-6 w-6"
                    onClick={() => setShowPinnedOnly(true)}
                    title="Pinned only"
                >
                    <Pin className="h-3 w-3" />
                </Button>
                <div className="w-px h-4 bg-border mx-1" />
                <span className="text-xs text-muted-foreground">Ctrl+Shift+V</span>
            </div>

            <div className="flex no-drag">
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            size="icon"
                            className="h-8 w-8 rounded-none hover:bg-secondary"
                        >
                            <Settings className="h-3 w-3" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Settings</DropdownMenuLabel>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem>Preferences</DropdownMenuItem>
                        <DropdownMenuItem>Keyboard Shortcuts</DropdownMenuItem>
                        <DropdownMenuItem>Theme</DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem>About</DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
                <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 rounded-none hover:bg-secondary"
                    onClick={() => window.electron?.close()}
                >
                    <X className="h-3 w-3" />
                </Button>
            </div>
        </div>
    );
}