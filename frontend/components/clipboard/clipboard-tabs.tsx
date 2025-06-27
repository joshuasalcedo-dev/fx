"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import ClipboardList from "./clipboard-list";
import { type ClipboardEntry } from "./clipboard-card";

interface ClipboardTabsProps {
    allEntries: ClipboardEntry[];
    pinnedEntries: ClipboardEntry[];
    loading: boolean;
    onPinToggle: (id: number) => void;
    onDelete: (id: number) => void;
}

export default function ClipboardTabs({
                                          allEntries,
                                          pinnedEntries,
                                          loading,
                                          onPinToggle,
                                          onDelete
                                      }: ClipboardTabsProps) {
    return (
        <Tabs defaultValue="all" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="all">All Entries</TabsTrigger>
                <TabsTrigger value="pinned">Pinned Only</TabsTrigger>
            </TabsList>

            <TabsContent value="all">
                <Card>
                    <CardHeader>
                        <CardTitle>Clipboard History</CardTitle>
                        <CardDescription>
                            {allEntries.length} entries found
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ClipboardList
                            entries={allEntries}
                            loading={loading}
                            onPinToggle={onPinToggle}
                            onDelete={onDelete}
                        />
                    </CardContent>
                </Card>
            </TabsContent>

            <TabsContent value="pinned">
                <Card>
                    <CardHeader>
                        <CardTitle>Pinned Entries</CardTitle>
                        <CardDescription>
                            {pinnedEntries.length} pinned entries
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <ClipboardList
                            entries={pinnedEntries}
                            loading={false}
                            onPinToggle={onPinToggle}
                            onDelete={onDelete}
                        />
                    </CardContent>
                </Card>
            </TabsContent>
        </Tabs>
    );
}