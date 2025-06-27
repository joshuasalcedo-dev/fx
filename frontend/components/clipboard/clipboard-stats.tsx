import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { FileText, Pin, Clock } from "lucide-react";
import { type ClipboardStats } from "@/lib/api-client";

interface ClipboardStatsProps {
    stats: ClipboardStats;
}

export default function ClipboardStatsCards({ stats }: ClipboardStatsProps) {
    return (
        <div className="grid gap-4 md:grid-cols-3 mb-6">
            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Total Entries</CardTitle>
                    <FileText className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{stats.totalEntries}</div>
                </CardContent>
            </Card>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Pinned</CardTitle>
                    <Pin className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{stats.pinnedEntries}</div>
                </CardContent>
            </Card>
            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Unpinned</CardTitle>
                    <Clock className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{stats.unpinnedEntries}</div>
                </CardContent>
            </Card>
        </div>
    );
}