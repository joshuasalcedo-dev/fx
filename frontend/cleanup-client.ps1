# cleanup-client.ps1
# PowerShell script to clean up the generated TypeScript client

param(
    [string]$ClientDir = "./lib/client"
)

Write-Host "🧹 Cleaning up generated TypeScript client..." -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray

try {
    if (-not (Test-Path $ClientDir)) {
        Write-Host "❌ Client directory not found: $ClientDir" -ForegroundColor Red
        exit 1
    }

    # Files and directories to remove
    $itemsToRemove = @(
        "package.json",
        "package-lock.json",
        "node_modules",
        "tsconfig.json",
        ".npmignore",
        ".openapi-generator",
        ".openapi-generator-ignore",
        "git_push.sh",
        "README.md"
    )

    $removedItems = @()
    $skippedItems = @()

    foreach ($item in $itemsToRemove) {
        $itemPath = Join-Path $ClientDir $item
        
        if (Test-Path $itemPath) {
            try {
                if (Test-Path $itemPath -PathType Container) {
                    Remove-Item -Path $itemPath -Recurse -Force
                    $removedItems += "$item/ (directory)"
                } else {
                    Remove-Item -Path $itemPath -Force
                    $removedItems += $item
                }
            }
            catch {
                Write-Host "   ⚠️  Failed to remove $item`: $_" -ForegroundColor Yellow
                $skippedItems += $item
            }
        }
    }

    # Remove any .js files that might have been generated
    $jsFiles = Get-ChildItem -Path $ClientDir -Filter "*.js" -Recurse
    foreach ($jsFile in $jsFiles) {
        try {
            Remove-Item -Path $jsFile.FullName -Force
            $removedItems += $jsFile.Name
        }
        catch {
            Write-Host "   ⚠️  Failed to remove $($jsFile.Name): $_" -ForegroundColor Yellow
            $skippedItems += $jsFile.Name
        }
    }

    # Clean up empty directories
    $emptyDirs = Get-ChildItem -Path $ClientDir -Directory -Recurse | Where-Object { 
        (Get-ChildItem -Path $_.FullName -Force | Measure-Object).Count -eq 0 
    }
    
    foreach ($emptyDir in $emptyDirs) {
        try {
            Remove-Item -Path $emptyDir.FullName -Force
            $removedItems += "$($emptyDir.Name)/ (empty directory)"
        }
        catch {
            Write-Host "   ⚠️  Failed to remove empty directory $($emptyDir.Name): $_" -ForegroundColor Yellow
        }
    }

    # Display results
    Write-Host "`n📊 Cleanup Summary:" -ForegroundColor Green
    
    if ($removedItems.Count -gt 0) {
        Write-Host "   ✓ Removed $($removedItems.Count) items:" -ForegroundColor Green
        foreach ($item in $removedItems) {
            Write-Host "     - $item" -ForegroundColor Gray
        }
    } else {
        Write-Host "   ℹ️  No items to remove" -ForegroundColor Cyan
    }

    if ($skippedItems.Count -gt 0) {
        Write-Host "`n   ⚠️  Skipped $($skippedItems.Count) items:" -ForegroundColor Yellow
        foreach ($item in $skippedItems) {
            Write-Host "     - $item" -ForegroundColor Gray
        }
    }

    # Show remaining files
    Write-Host "`n📁 Remaining files in client directory:" -ForegroundColor Cyan
    $remainingFiles = Get-ChildItem -Path $ClientDir -Recurse -File | Sort-Object Name
    
    if ($remainingFiles.Count -gt 0) {
        foreach ($file in $remainingFiles) {
            $relativePath = $file.FullName.Replace((Resolve-Path $ClientDir).Path, "").TrimStart('\', '/')
            Write-Host "   📄 $relativePath" -ForegroundColor White
        }
    } else {
        Write-Host "   ℹ️  Directory is empty" -ForegroundColor Gray
    }

    Write-Host "`n✅ Client cleanup completed!" -ForegroundColor Green
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray
}
catch {
    Write-Host "`n❌ Error during cleanup: $_" -ForegroundColor Red
    exit 1
}