# Script to add sensor logs with VARIED values to create LOW, MEDIUM, HIGH, and CRITICAL risk levels
$API_URL = "https://equipment-failure-risk-predictor.onrender.com/api/v1"

Write-Host "Fetching all equipment..." -ForegroundColor Cyan

try {
    $equipmentList = Invoke-RestMethod -Uri "$API_URL/equipment" -Method Get
    Write-Host "Found $($equipmentList.Count) equipment items" -ForegroundColor Green
}
catch {
    Write-Host "Failed to fetch equipment: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "Adding varied sensor logs to create different risk levels..." -ForegroundColor Cyan

# Risk calculation formula: 0.4*temp(normalized) + 0.35*vib(normalized) + 0.25*load(normalized)
# Ranges: temp(0-150), vib(0-50), load(0-100)
# Risk levels: LOW(<40), MEDIUM(40-64), HIGH(65-84), CRITICAL(>=85)

# Create different risk profiles
$riskProfiles = @{
    "LOW" = @(
        @{ temperature = 30; vibration = 1.5; loadPercentage = 25 },   # ~20 risk score
        @{ temperature = 35; vibration = 2.0; loadPercentage = 30 },   # ~23 risk score
        @{ temperature = 40; vibration = 2.5; loadPercentage = 35 }    # ~27 risk score
    )
    "MEDIUM" = @(
        @{ temperature = 65; vibration = 4.0; loadPercentage = 50 },   # ~45 risk score
        @{ temperature = 75; vibration = 5.0; loadPercentage = 60 },   # ~52 risk score
        @{ temperature = 85; vibration = 6.0; loadPercentage = 65 }    # ~58 risk score
    )
    "HIGH" = @(
        @{ temperature = 105; vibration = 15.0; loadPercentage = 75 }, # ~70 risk score
        @{ temperature = 115; vibration = 18.0; loadPercentage = 80 }, # ~76 risk score
        @{ temperature = 120; vibration = 20.0; loadPercentage = 85 }  # ~80 risk score
    )
    "CRITICAL" = @(
        @{ temperature = 135; vibration = 30.0; loadPercentage = 90 }, # ~88 risk score
        @{ temperature = 140; vibration = 35.0; loadPercentage = 95 }, # ~93 risk score
        @{ temperature = 145; vibration = 40.0; loadPercentage = 98 }  # ~97 risk score
    )
}

$equipmentIndex = 0
foreach ($equipment in $equipmentList) {
    $equipmentId = $equipment.id
    $equipmentName = $equipment.name
    
    # Assign risk level based on index to get a mix
    $riskType = switch ($equipmentIndex % 4) {
        0 { "LOW" }
        1 { "MEDIUM" }
        2 { "HIGH" }
        3 { "CRITICAL" }
    }
    
    Write-Host ""
    Write-Host "Adding $riskType risk logs for: $equipmentName" -ForegroundColor Yellow
    
    $logs = $riskProfiles[$riskType]
    
    foreach ($log in $logs) {
        $body = $log | ConvertTo-Json
        
        try {
            $response = Invoke-RestMethod -Uri "$API_URL/equipment/$equipmentId/logs" -Method Post -Body $body -ContentType "application/json"
            Write-Host "  Added log: T=$($log.temperature)C, V=$($log.vibration)mm/s, L=$($log.loadPercentage)%" -ForegroundColor Green
        }
        catch {
            Write-Host "  Failed to add log" -ForegroundColor Red
        }
    }
    
    $equipmentIndex++
}

Write-Host ""
Write-Host "Sensor logs added successfully!" -ForegroundColor Green
Write-Host "Expected distribution:" -ForegroundColor Cyan
Write-Host "  - LOW risk: ~$([math]::Ceiling($equipmentList.Count / 4)) equipment" -ForegroundColor Green
Write-Host "  - MEDIUM risk: ~$([math]::Ceiling($equipmentList.Count / 4)) equipment" -ForegroundColor Yellow
Write-Host "  - HIGH risk: ~$([math]::Ceiling($equipmentList.Count / 4)) equipment" -ForegroundColor DarkYellow
Write-Host "  - CRITICAL risk: ~$([math]::Ceiling($equipmentList.Count / 4)) equipment" -ForegroundColor Red
Write-Host ""
Write-Host "Refresh your dashboard to see the updated risk levels!" -ForegroundColor Cyan
