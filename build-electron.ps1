#!/usr/bin/env pwsh

Write-Host "🚀 Building frontend for Electron..." -ForegroundColor Green

# Change to frontend directory
Set-Location -Path "frontend"

# Step 1: Build the Next.js app
Write-Host "📦 Building Next.js app..." -ForegroundColor Yellow
try {
    pnpm run build
    Write-Host "✅ Next.js build completed" -ForegroundColor Green
} catch {
    Write-Host "❌ Next.js build failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 2: Copy built files to Electron app
$sourceDir = "out"
$targetDir = "../clipboard-electron/out"

Write-Host "📁 Copying files to Electron app..." -ForegroundColor Yellow

# Remove existing directory if it exists
if (Test-Path $targetDir) {
    Write-Host "🗑️  Removing existing files..." -ForegroundColor Cyan
    Remove-Item -Recurse -Force $targetDir
}

# Copy the out directory
try {
    Copy-Item -Recurse $sourceDir $targetDir
    Write-Host "✅ Files copied successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to copy files: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Go back to root
Set-Location -Path ".."

# Step 3: Build Electron app
Write-Host "⚡ Building Electron app..." -ForegroundColor Yellow
Set-Location -Path "clipboard-electron"

# Install dependencies
Write-Host "📦 Installing Electron dependencies..." -ForegroundColor Cyan
try {
    npm install
    Write-Host "✅ Dependencies installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to install dependencies: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Build Electron app
Write-Host "🔨 Building Electron app..." -ForegroundColor Cyan
try {
    npm run build
    Write-Host "✅ Electron build completed" -ForegroundColor Green
} catch {
    Write-Host "❌ Electron build failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Go back to root
Set-Location -Path ".."

Write-Host "🎉 Complete build process finished!" -ForegroundColor Green
Write-Host "📱 Your Electron app is ready!" -ForegroundColor Cyan