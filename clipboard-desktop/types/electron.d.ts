// types/electron.d.ts
export interface IElectronAPI {
    close: () => void
    setAlwaysOnTop: (flag: boolean) => void
}

declare global {
    interface Window {
        electron?: IElectronAPI
    }
}