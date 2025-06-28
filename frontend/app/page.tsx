"use client";

import ClipboardContainer from "@/components/clipboard/clipboard-container";

export default function Home() {
  return (
      <div className="container mx-auto p-4 max-w-6xl">
        <div className="mb-6">
          <h1 className="text-3xl font-bold mb-2">Clipboard Manager</h1>
          <p className="text-muted-foreground">Manage your clipboard history with ease</p>
        </div>

        <ClipboardContainer />
      </div>
  );
}