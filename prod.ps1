Write-Host "Building native image with prod profile..."
mvn clean package -Pnative -Dspring.profiles.active=prod

Write-Host "Running native image..."
$appName = "fx"
$version = "1.0.0-SNAPSHOT"
$nativeImagePath = ".\target\$appName-$version.exe"

if (Test-Path $nativeImagePath) {
    & $nativeImagePath
} else {
    Write-Host "Native image not found at $nativeImagePath. Build may have failed."
}