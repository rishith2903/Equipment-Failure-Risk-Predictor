# Script to add demo sensor logs to equipment
$API_URL = "https://equipment-failure-risk-predictor.onrender.com/api/v1"

Write-Host "Fetching equipment list..." -ForegroundColor Cyan

# Get all equipment
try {
    $equipmentList = Invoke-RestMethod -Uri "$API_URL/equipment" -Method Get
    
    if ($equipmentList.Count -eq 0) {
        Write-Host "No equipment found. Please add equipment first using add-demo-data.ps1" -ForegroundColor Red
        exit
    }
    
    Write-Host "Found $($equipmentList.Count) equipment items" -ForegroundColor Green
}
catch {
    Write-Host "Failed to fetch equipment: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "Adding sensor logs..." -ForegroundColor Cyan

foreach ($equipment in $equipmentList) {
    $equipmentId = $equipment.id
    $equipmentName = $equipment.name
    
    Write-Host ""
    Write-Host "Adding logs for: $equipmentName" -ForegroundColor Yellow
    
    # Add 5 sensor logs per equipment with varying values
    $logs = @(
        @{
            temperature = 75.5
            vibration = 0.8
            loadPercentage = 45.0
        },
        @{
            temperature = 78.2
            vibration = 1.2
            loadPercentage = 52.0
        },
        @{
            temperature = 82.1
            vibration = 1.8
            loadPercentage = 61.0
        },
        @{
            temperature = 85.5
            vibration = 2.5
            loadPercentage = 70.0
        },
        @{
            temperature = 88.0
            vibration = 3.2
            loadPercentage = 78.0
        }
    )
    
    foreach ($log in $logs) {
        $body = $log | ConvertTo-Json
        
        try {
            $response = Invoke-RestMethod -Uri "$API_URL/equipment/$equipmentId/logs" -Method Post -Body $body -ContentType "application/json"
            Write-Host "  Added log: T=$($log.temperature)C, V=$($log.vibration)mm/s, Load=$($log.loadPercentage)%" -ForegroundColor Green
        }
        catch {
            Write-Host "  Failed to add log: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

Write-Host ""
Write-Host "Demo sensor logs added successfully!" -ForegroundColor Green
Write-Host "Refresh your dashboard to see the sensor data and risk predictions." -ForegroundColor Yellow
