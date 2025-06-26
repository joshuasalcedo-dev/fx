# Electron + Next.js Setup Script
# This script automates the setup process from the tutorial

param(
    [string]$ProjectName = "clipboard-electron"
)

Write-Host "Starting Electron + Next.js project setup..." -ForegroundColor Green
Write-Host "Project name: $ProjectName" -ForegroundColor Cyan

# Step 1: Create Next.js app
Write-Host "`nStep 1: Creating Next.js app..." -ForegroundColor Yellow
npx create-next-app@latest $ProjectName --no-app --use-npm --no-tailwind --no-src-dir --import-alias "@/*"

# Check if the project was created successfully
if (-not (Test-Path $ProjectName)) {
    Write-Host "Error: Failed to create Next.js project" -ForegroundColor Red
    exit 1
}

# Navigate to project directory
Set-Location .\$ProjectName
Write-Host "Navigated to project directory: $ProjectName" -ForegroundColor Green

# Step 2: Install Electron dependencies
Write-Host "`nStep 2: Installing Electron dependencies..." -ForegroundColor Yellow
Write-Host "Installing dev dependencies..." -ForegroundColor Cyan
npm install --save-dev electron electron-builder concurrently

Write-Host "Installing project dependencies..." -ForegroundColor Cyan
npm install electron-serve

# Step 3: Update package.json
Write-Host "`nStep 3: Updating package.json..." -ForegroundColor Yellow
$packageJson = Get-Content "package.json" -Raw | ConvertFrom-Json

# Add main entry point
$packageJson | Add-Member -Name "main" -Value "main/main.js" -MemberType NoteProperty -Force
$packageJson | Add-Member -Name "author" -Value "Your Name" -MemberType NoteProperty -Force
$packageJson | Add-Member -Name "description" -Value "Electron + NextJS clipboard application" -MemberType NoteProperty -Force

# Update scripts
$packageJson.scripts.dev = 'concurrently -n "NEXT,ELECTRON" -c "yellow,blue" --kill-others "next dev" "electron ."'
$packageJson.scripts.build = "next build && electron-builder"

# Save updated package.json
$packageJson | ConvertTo-Json -Depth 10 | Set-Content "package.json" -Encoding UTF8
Write-Host "package.json updated successfully" -ForegroundColor Green

# Step 4: Configure Next.js
Write-Host "`nStep 4: Configuring Next.js..." -ForegroundColor Yellow
$nextConfig = @'
/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  output: "export",
  images: {
    unoptimized: true
  }
}

module.exports = nextConfig
'@

$nextConfig | Set-Content "next.config.js" -Encoding UTF8
Write-Host "next.config.js updated successfully" -ForegroundColor Green

# Step 5: Create Electron files
Write-Host "`nStep 5: Creating Electron files..." -ForegroundColor Yellow

# Create main directory
New-Item -ItemType Directory -Path "main" -Force | Out-Null

# Create main.js
$mainJs = @'
const { app, BrowserWindow } = require("electron");
const serve = require("electron-serve");
const path = require("path");

const appServe = app.isPackaged ? serve({
  directory: path.join(__dirname, "../out")
}) : null;

const createWindow = () => {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, "preload.js")
    }
  });

  if (app.isPackaged) {
    appServe(win).then(() => {
      win.loadURL("app://-");
    });
  } else {
    win.loadURL("http://localhost:3000");
    win.webContents.openDevTools();
    win.webContents.on("did-fail-load", (e, code, desc) => {
      win.webContents.reloadIgnoringCache();
    });
  }
}

app.on("ready", () => {
    createWindow();
});

app.on("window-all-closed", () => {
    if(process.platform !== "darwin"){
        app.quit();
    }
});
'@

$mainJs | Set-Content "main/main.js" -Encoding UTF8
Write-Host "main/main.js created successfully" -ForegroundColor Green

# Step 6: Create preload.js
$preloadJs = @'
const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("electronAPI", {
    on: (channel, callback) => {
        ipcRenderer.on(channel, callback);
    },
    send: (channel, args) => {
        ipcRenderer.send(channel, args);
    }
});
'@

$preloadJs | Set-Content "main/preload.js" -Encoding UTF8
Write-Host "main/preload.js created successfully" -ForegroundColor Green

# Step 7: Create electron-builder.yaml
Write-Host "`nStep 7: Creating electron-builder configuration..." -ForegroundColor Yellow
$electronBuilderYaml = @'
appId: "com.example.clipboard-electron"
productName: "Clipboard Electron"
copyright: "Copyright (c) 2024 Your Name"
win:
  target: ["dir", "portable", "zip"]
  icon: "resources/icon.ico"
linux:
  target: ["dir", "appimage", "zip"]
  icon: "resources/icon.png"
mac:
  target: ["dir", "dmg", "zip"]
  icon: "resources/icon.icns"
'@

$electronBuilderYaml | Set-Content "electron-builder.yaml" -Encoding UTF8
Write-Host "electron-builder.yaml created successfully" -ForegroundColor Green

# Create resources directory for icons
New-Item -ItemType Directory -Path "resources" -Force | Out-Null
Write-Host "Created resources directory for icons" -ForegroundColor Green

# Final message
Write-Host "`n✅ Setup completed successfully!" -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Add your application icons to the 'resources' folder" -ForegroundColor White
Write-Host "2. Update author and description in package.json" -ForegroundColor White
Write-Host "3. Run 'npm run dev' to start development" -ForegroundColor White
Write-Host "4. Run 'npm run build' to build executables" -ForegroundColor White
Write-Host "`nProject structure created in: $(Get-Location)" -ForegroundColor Cyan