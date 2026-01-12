# Setup Minecraft test environment PowerShell script
Write-Host "Setting up Minecraft test environment..." -ForegroundColor Green

# Define directory paths
$testEnvPath = "src\test-env"
$librariesPath = "src\test-env\libraries"
$minecraftVersion = "1.21.10"

# Check if directories exist
if (Test-Path $testEnvPath) {
    Write-Host "Test environment directory already exists: $testEnvPath" -ForegroundColor Yellow
} else {
    Write-Host "Creating test environment directory: $testEnvPath" -ForegroundColor Green
    New-Item -ItemType Directory -Path $testEnvPath -Force
}

if (Test-Path $librariesPath) {
    Write-Host "Libraries directory already exists: $librariesPath" -ForegroundColor Yellow
} else {
    Write-Host "Creating libraries directory: $librariesPath" -ForegroundColor Green
    New-Item -ItemType Directory -Path $librariesPath -Force
}

# Check if Minecraft main jar file exists
$minecraftJarPath = "$testEnvPath\$minecraftVersion.jar"
if (Test-Path $minecraftJarPath) {
    Write-Host "Minecraft main jar file already exists: $minecraftJarPath" -ForegroundColor Yellow
} else {
    Write-Host "Minecraft main jar file does not exist: $minecraftJarPath" -ForegroundColor Red
    Write-Host "Please manually download Minecraft $minecraftVersion jar file and place it in $testEnvPath directory" -ForegroundColor Red
    Write-Host "Or copy from Minecraft launcher after downloading the version" -ForegroundColor Red
}

Write-Host ""
Write-Host "Test environment setup completed!" -ForegroundColor Green
Write-Host "Directory structure:" -ForegroundColor Green
Write-Host "├── $testEnvPath" -ForegroundColor Cyan
Write-Host "    ├── $minecraftVersion.jar  <- Need to add manually" -ForegroundColor Red
Write-Host "    └── libraries/" -ForegroundColor Cyan
Write-Host "        └── *.jar  <- Need to add all Minecraft dependencies" -ForegroundColor Red

Write-Host ""
Write-Host "To run test environment, use: ./gradlew runTestEnv" -ForegroundColor Green