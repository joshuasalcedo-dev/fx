// components/clipboard/clipboard-card.tsx
"use client";

import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Copy, Pin, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { copyToClipboard, formatDate, truncateContent } from "@/lib/utils";
import type { ClipboardDto } from "@/lib/api-client";
import { cn } from "@/lib/utils";
import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip";

interface ClipboardCardProps {
    entry: ClipboardDto;
    onPinToggle: (id: number) => void;
    onDelete: (id: number) => void;
}

export default function ClipboardCard({ entry, onPinToggle, onDelete }: ClipboardCardProps) {
    return (
        <Card
            className={cn(
                "p-2 group transition-all duration-200 hover:shadow-sm",
                entry.isPinned && "border-primary/20 bg-primary/5"
            )}
        >
            <div className="flex items-start gap-2">
                <div className="flex-1 min-w-0">
                    <p className="text-sm leading-tight break-all">
                        {truncateContent(entry.content, 150)}
                    </p>
                    <div className="flex items-center gap-2 mt-1">
                        <span className="text-xs text-muted-foreground/70">
                            {formatDate(entry.localDateTime)}
                        </span>
                        {entry.isPinned && (
                            <Pin className="h-3 w-3 text-primary/60" />
                        )}
                    </div>
                </div>
                <TooltipProvider delayDuration={300}>
                    <div className="flex flex-col gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity">
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <Button
                                    size="icon"
                                    variant="ghost"
                                    className="h-6 w-6"
                                    onClick={() => copyToClipboard(entry.content)}
                                >
                                    <Copy className="h-3 w-3" />
                                </Button>
                            </TooltipTrigger>
                            <TooltipContent side="left" className="text-xs">
                                Copy
                            </TooltipContent>
                        </Tooltip>

                        <Tooltip>
                            <TooltipTrigger asChild>
                                <Button
                                    size="icon"
                                    variant="ghost"
                                    className="h-6 w-6"
                                    onClick={() => onPinToggle(entry.id)}
                                >
                                    <Pin className={cn("h-3 w-3", entry.isPinned && "fill-current")} />
                                </Button>
                            </TooltipTrigger>
                            <TooltipContent side="left" className="text-xs">
                                {entry.isPinned ? "Unpin" : "Pin"}
                            </TooltipContent>
                        </Tooltip>

                        <Tooltip>
                            <TooltipTrigger asChild>
                                <Button
                                    size="icon"
                                    variant="ghost"
                                    className="h-6 w-6 text-destructive/60 hover:text-destructive"
                                    onClick={() => onDelete(entry.id)}
                                >
                                    <Trash2 className="h-3 w-3" />
                                </Button>
                            </TooltipTrigger>
                            <TooltipContent side="left" className="text-xs">
                                Delete
                            </TooltipContent>
                        </Tooltip>
                    </div>
                </TooltipProvider>
            </div>
        </Card>
    );
}