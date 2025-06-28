#!/usr/bin/env pwsh

Write-Host "🚀 Starting development environment..." -ForegroundColor Green

# Function to kill background jobs on exit
function Cleanup {
    Write-Host "`n🛑 Shutting down development servers..." -ForegroundColor Yellow
    Get-Job | Stop-Job
    Get-Job | Remove-Job
    exit
}

# Register cleanup function
Register-EngineEvent PowerShell.Exiting -Action { Cleanup }

# Start Spring Boot backend
Write-Host "🌱 Starting Spring Boot backend..." -ForegroundColor Green
Start-Job -Name "Backend" -ScriptBlock {
    mvn spring-boot:run
}

# Start frontend dev server
Write-Host "📦 Starting frontend dev server..." -ForegroundColor Cyan
Start-Job -Name "Frontend" -ScriptBlock {
    Set-Location "frontend"
    pnpm dev
}

# Wait for services to start
Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Start Electron
Write-Host "⚡ Starting Electron app..." -ForegroundColor Yellow
Set-Location "clipboard-electron"

try {
    # Check if electron is installed
    if (Get-Command electron -ErrorAction SilentlyContinue) {
        electron .
    } else {
        # Use npx electron
        npx electron .
    }
} catch {
    Write-Host "❌ Failed to start Electron: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "💡 Make sure Electron dependencies are installed" -ForegroundColor Cyan
} finally {
    Cleanup
}