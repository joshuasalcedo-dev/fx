// pages/_app.tsx
import type { AppProps } from 'next/app'
import { ThemeProvider } from "@/components/theme/theme-provider"
import TitleBar from "@/components/window/title-bar"
import { Toaster } from 'sonner'
import '@/styles/globals.css'

export default function App({ Component, pageProps }: AppProps) {
    return (
        <ThemeProvider
            attribute="class"
            defaultTheme="dark"
            enableSystem
            disableTransitionOnChange
        >
            <div className="h-screen flex flex-col">
                <TitleBar />
                <main className="flex-1 overflow-hidden">
                    <Component {...pageProps} />
                </main>
            </div>
            <Toaster
                position="bottom-center"
                toastOptions={{
                    style: {
                        background: 'hsl(var(--background))',
                        color: 'hsl(var(--foreground))',
                        border: '1px solid hsl(var(--border))',
                    },
                }}
            />
        </ThemeProvider>
    )
}