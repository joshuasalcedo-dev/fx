# PowerShell script to fix chart.tsx TypeScript errors

Write-Host "Fixing chart.tsx TypeScript errors..." -ForegroundColor Green

# Path to the chart.tsx file
$chartPath = "components/ui/chart.tsx"

# Check if file exists
if (-not (Test-Path $chartPath)) {
    Write-Host "Error: chart.tsx not found at $chartPath" -ForegroundColor Red
    exit 1
}

# Backup the original file
$backupPath = "$chartPath.backup"
Copy-Item -Path $chartPath -Destination $backupPath -Force
Write-Host "Created backup at: $backupPath" -ForegroundColor Yellow

# Read the file content
$content = Get-Content -Path $chartPath -Raw

# Fix 1: Update ChartTooltipContent to properly type the props
$tooltipFix = @'
interface ChartTooltipContentProps<TPayload = any> {
  active?: boolean;
  payload?: Array<{
    payload?: TPayload;
    value?: number | string;
    name?: string;
    dataKey?: string | number;
    color?: string;
    [key: string]: any;
  }>;
  label?: string;
  labelFormatter?: (value: any) => string;
  formatter?: (value: any, name: any, payload: any, index: number) => any;
  hideLabel?: boolean;
  hideIndicator?: boolean;
  indicator?: "line" | "dot" | "dashed";
  nameKey?: string;
  labelKey?: string;
}

function ChartTooltipContent<TPayload = any>({
  active,
  payload,
  label,
  labelFormatter,
  formatter,
  hideLabel,
  hideIndicator = false,
  indicator = "dot",
  nameKey,
  labelKey,
}: ChartTooltipContentProps<TPayload>) {
'@

# Fix 2: Update other problematic type definitions
$payloadTypeFix = @'
  if (!active || !payload || !payload.length) {
    return null;
  }

  const tooltipLabel = label ?? payload[0]?.payload?.[labelKey as keyof typeof payload[0]["payload"]] ?? "";
'@

# Apply fixes by replacing patterns
$content = $content -replace 'function ChartTooltipContent\({[\s\S]*?}\s*{', $tooltipFix
$content = $content -replace 'if \(!active\)', $payloadTypeFix

# Fix any remaining any type parameters
$content = $content -replace 'Parameter ''item'' implicitly has an ''any'' type\.', 'item: any'
$content = $content -replace 'Parameter ''index'' implicitly has an ''any'' type\.', 'index: number'

# Write the fixed content back
Set-Content -Path $chartPath -Value $content -NoNewline

Write-Host "Applied TypeScript fixes to chart.tsx" -ForegroundColor Green

# Install type definitions if missing
Write-Host "`nChecking for required type packages..." -ForegroundColor Yellow

$requiredPackages = @(
    "@types/react",
    "@types/node",
    "recharts"
)

foreach ($package in $requiredPackages) {
    $installed = npm list $package --depth=0 2>$null
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Installing $package..." -ForegroundColor Cyan
        npm install --save-dev $package
    }
}

# Create a type declaration file for additional fixes
$typeDeclarationPath = "types/chart.d.ts"
$typeDeclarationDir = "types"

if (-not (Test-Path $typeDeclarationDir)) {
    New-Item -ItemType Directory -Path $typeDeclarationDir -Force | Out-Null
}

$typeDeclarationContent = @'
// Type declarations for chart components
import { TooltipProps } from 'recharts';

declare module 'recharts' {
  export interface TooltipPayload<TValue = any, TName = any> {
    payload?: any;
    value?: TValue;
    name?: TName;
    dataKey?: string | number;
    color?: string;
    [key: string]: any;
  }
}

declare global {
  interface ChartConfig {
    [key: string]: {
      label: string;
      color?: string;
      icon?: React.ComponentType;
      theme?: {
        light?: string;
        dark?: string;
      };
    };
  }
}

export {};
'@

Set-Content -Path $typeDeclarationPath -Value $typeDeclarationContent -NoNewline
Write-Host "Created type declaration file at: $typeDeclarationPath" -ForegroundColor Green

# Update tsconfig.json to include the types directory
$tsconfigPath = "tsconfig.json"
if (Test-Path $tsconfigPath) {
    $tsconfig = Get-Content -Path $tsconfigPath -Raw | ConvertFrom-Json

    if (-not $tsconfig.include) {
        $tsconfig | Add-Member -MemberType NoteProperty -Name "include" -Value @() -Force
    }

    if ($tsconfig.include -notcontains "types/**/*") {
        $tsconfig.include += "types/**/*"
    }

    $tsconfig | ConvertTo-Json -Depth 10 | Set-Content -Path $tsconfigPath
    Write-Host "Updated tsconfig.json to include types directory" -ForegroundColor Green
}

Write-Host "`nRunning type check..." -ForegroundColor Yellow
npm run lint

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nAll TypeScript errors fixed successfully!" -ForegroundColor Green
    Write-Host "You can now run: npm run build" -ForegroundColor Cyan
} else {
    Write-Host "`nSome errors may still remain. Check the output above." -ForegroundColor Yellow
    Write-Host "Original file backed up at: $backupPath" -ForegroundColor Yellow
}
'@'