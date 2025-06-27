"use client"

import {Card} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {Copy, Pin, Trash2} from "lucide-react";
import {Button} from "@/components/ui/button";
import {toast} from "sonner";
import {clipboardOperations} from "@/lib/api-client";
import {copyToClipboard, formatDate, truncateContent} from "@/lib/utils";
export interface ClipboardEntry {
    id: number;
    content: string;
    localDateTime: string;
    isPinned: boolean;
}

interface ClipboardCardProps {
    entry: ClipboardEntry;
    onPinToggle: (id: number) => void;
    onDelete: (id: number) => void;
}
export default function  ClipboardCard(props : ClipboardCardProps){

    return (
        <Card key={props.entry.id} className="p-3">
    <div className="flex items-start justify-between gap-2">
    <div className="flex-1 min-w-0">
    <div className="flex items-center gap-2 mb-1">
    <span className="text-xs text-muted-foreground">
        {formatDate(props.entry.localDateTime)}
    </span>
    {props.entry.isPinned && (
        <Badge variant="secondary" className="text-xs">
    <Pin className="h-3 w-3 mr-1" />
        Pinned
        </Badge>
    )}
    </div>
    <p className="text-sm break-all">
        {truncateContent(props.entry.content)}
    </p>
    </div>
    <div className="flex gap-1">
    <Button
        size="icon"
    variant="ghost"
    onClick={() => copyToClipboard(props.entry.content)}
>
    <Copy className="h-4 w-4" />
        </Button>
        <Button
    size="icon"
    variant="ghost"
    onClick={() => props.onPinToggle(props.entry.id)}
>
    <Pin className={`h-4 w-4 ${props.entry.isPinned ? "fill-current" : ""}`} />
    </Button>
    <Button
    size="icon"
    variant="ghost"
    onClick={() => props.onDelete(props.entry.id)}
>
    <Trash2 className="h-4 w-4" />
        </Button>
        </div>
        </div>
        </Card>
    )
}




