# generate-clipboard-api-client.ps1
# PowerShell script to generate TypeScript Axios client from OpenAPI specification

param(
    [string]$OutputDir = "./lib/client",
    [string]$PackageName = "clipboard-api-client",
    [string]$PackageVersion = "1.0.0"
)

# Configuration
$OpenApiUrl = "http://localhost:5000/v3/api-docs"
$GeneratorVersion = "7.2.0"
$TempSpecFile = "./clipboard-api-spec.json"

Write-Host "🚀 Starting TypeScript Axios Client Generation" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray

# Function to check if a command exists
function Test-CommandExists {
    param($Command)
    $null = Get-Command $Command -ErrorAction SilentlyContinue
    return $?
}

# Check if Java is installed (required for OpenAPI Generator)
if (-not (Test-CommandExists "java")) {
    Write-Host "❌ Java is not installed. OpenAPI Generator requires Java." -ForegroundColor Red
    Write-Host "   Please install Java from: https://www.java.com/download/" -ForegroundColor Yellow
    exit 1
}

# Check if npm is installed
if (-not (Test-CommandExists "npm")) {
    Write-Host "❌ npm is not installed. Please install Node.js/npm first." -ForegroundColor Red
    exit 1
}

try {
    # Step 1: Download OpenAPI spec from the server
    Write-Host "`n📥 Downloading OpenAPI specification..." -ForegroundColor Green
    try {
        Invoke-RestMethod -Uri $OpenApiUrl -OutFile $TempSpecFile -ErrorAction Stop
        Write-Host "   ✓ OpenAPI spec downloaded successfully" -ForegroundColor Green
    }
    catch {
        Write-Host "   ❌ Failed to download OpenAPI spec from $OpenApiUrl" -ForegroundColor Red
        Write-Host "   Make sure your API server is running on http://localhost:5000" -ForegroundColor Yellow

        # If download fails, check if paste.txt exists and use it
        if (Test-Path "./paste.txt") {
            Write-Host "   📄 Using local paste.txt file instead..." -ForegroundColor Yellow
            Copy-Item "./paste.txt" $TempSpecFile
        }
        else {
            throw $_
        }
    }

    # Step 2: Install OpenAPI Generator CLI if not already installed
    Write-Host "`n📦 Checking OpenAPI Generator CLI..." -ForegroundColor Green
    $openApiGeneratorCmd = "openapi-generator-cli"

    if (-not (Test-CommandExists $openApiGeneratorCmd)) {
        Write-Host "   Installing OpenAPI Generator CLI globally..." -ForegroundColor Yellow
        npm install -g @openapitools/openapi-generator-cli

        # Set the version
        & $openApiGeneratorCmd version-manager set $GeneratorVersion
    }
    else {
        Write-Host "   ✓ OpenAPI Generator CLI is already installed" -ForegroundColor Green
    }

    # Step 3: Clean output directory
    if (Test-Path $OutputDir) {
        Write-Host "`n🧹 Cleaning existing output directory..." -ForegroundColor Yellow
        Remove-Item -Path $OutputDir -Recurse -Force
    }

    # Step 4: Generate TypeScript Axios client
    Write-Host "`n🔨 Generating TypeScript Axios client..." -ForegroundColor Green

    & $openApiGeneratorCmd generate `
        -i $TempSpecFile `
        -g typescript-axios `
        -o $OutputDir `
        --additional-properties=npmName=$PackageName `
        --additional-properties=npmVersion=$PackageVersion `
        --additional-properties=supportsES6=true `
        --additional-properties=withSeparateModelsAndApi=true `
        --additional-properties=withInterfaces=true `
        --additional-properties=modelPropertyNaming=camelCase `
        --additional-properties=apiPackage=api `
        --additional-properties=modelPackage=models

    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Client generated successfully!" -ForegroundColor Green
    }
    else {
        throw "OpenAPI Generator failed with exit code $LASTEXITCODE"
    }

    # Step 5: Install dependencies in the generated client
    Write-Host "`n📦 Installing client dependencies..." -ForegroundColor Green
    Push-Location $OutputDir
    try {
        npm install
        Write-Host "   ✓ Dependencies installed" -ForegroundColor Green
    }
    finally {
        Pop-Location
    }

    # Step 6: Create a usage example
    $exampleFile = Join-Path $OutputDir "example-usage.ts"
    $exampleContent = @"
// Example usage of the generated Clipboard API client

import { Configuration, ClipboardApi, ClipboardExportApi, HealthApi } from './api';

// Create configuration
const config = new Configuration({
    basePath: 'http://localhost:5000',
});

// Initialize API clients
const clipboardApi = new ClipboardApi(config);
const exportApi = new ClipboardExportApi(config);
const healthApi = new HealthApi(config);

// Example: Get all clipboard entries
async function getClipboards() {
    try {
        const response = await clipboardApi.clipboards(0, 20);
        console.log('Clipboard entries:', response.data);
    } catch (error) {
        console.error('Error fetching clipboards:', error);
    }
}

// Example: Search clipboard entries
async function searchClipboards(query: string) {
    try {
        const response = await clipboardApi.searchClipboards(query, 0, 20);
        console.log('Search results:', response.data);
    } catch (error) {
        console.error('Error searching clipboards:', error);
    }
}

// Example: Toggle pin status
async function togglePin(id: number) {
    try {
        const response = await clipboardApi.pinClipboards({ id });
        console.log('Pin toggled:', response.data);
    } catch (error) {
        console.error('Error toggling pin:', error);
    }
}

// Example: Export as JSON
async function exportAsJson() {
    try {
        const response = await exportApi.exportAsJson(true);
        console.log('Exported data:', response.data);
    } catch (error) {
        console.error('Error exporting:', error);
    }
}

// Example: Health check
async function checkHealth() {
    try {
        const response = await healthApi.health();
        console.log('Health status:', response.data);
    } catch (error) {
        console.error('Error checking health:', error);
    }
}

// Run examples
(async () => {
    await checkHealth();
    await getClipboards();
    await searchClipboards('example');
})();
"@

    Set-Content -Path $exampleFile -Value $exampleContent
    Write-Host "`n📝 Created example usage file: $exampleFile" -ForegroundColor Green

    # Final success message
    Write-Host "`n✅ TypeScript Axios client generated successfully!" -ForegroundColor Green
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor DarkGray
    Write-Host "`n📁 Output location: $OutputDir" -ForegroundColor Cyan
    Write-Host "📄 Example usage: $exampleFile" -ForegroundColor Cyan
    Write-Host "`nTo use the client in your Next.js project:" -ForegroundColor Yellow
    Write-Host "  1. The client is now generated in lib/client directory" -ForegroundColor White
    Write-Host "  2. Import API classes: import { ClipboardApi } from '@/lib/client/api'" -ForegroundColor White
    Write-Host "  3. Use the example file for reference: lib/client/example-usage.ts" -ForegroundColor White
    Write-Host "  4. The API base URL is configured for localhost:5000" -ForegroundColor White
}
catch {
    Write-Host "`n❌ Error: $_" -ForegroundColor Red
    exit 1
}
finally {
    # Cleanup
    if (Test-Path $TempSpecFile) {
        Remove-Item $TempSpecFile -Force
    }
}