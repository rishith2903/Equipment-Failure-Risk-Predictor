# Script to add demo equipment to the database
$API_URL = "https://equipment-failure-risk-predictor.onrender.com/api/v1"

# Demo Equipment Data
$equipment = @(
    @{
        name = "Industrial Pump A1"
        type = "PUMP"
        location = "Building A - Floor 1"
        installationDate = "2020-01-15"
        status = "OPERATIONAL"
    },
    @{
        name = "Conveyor Belt B2"
        type = "CONVEYOR"
        location = "Warehouse - Zone B"
        installationDate = "2019-06-20"
        status = "OPERATIONAL"
    },
    @{
        name = "HVAC System C3"
        type = "HVAC"
        location = "Office Building - Roof"
        installationDate = "2021-03-10"
        status = "OPERATIONAL"
    },
    @{
        name = "Compressor D4"
        type = "COMPRESSOR"
        location = "Factory - Production Line 1"
        installationDate = "2018-11-05"
        status = "MAINTENANCE"
    },
    @{
        name = "Generator E5"
        type = "GENERATOR"
        location = "Power Station - Main Hall"
        installationDate = "2017-08-22"
        status = "OPERATIONAL"
    }
)

Write-Host "Adding demo equipment..." -ForegroundColor Cyan

foreach ($item in $equipment) {
    $body = $item | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$API_URL/equipment" -Method Post -Body $body -ContentType "application/json"
        Write-Host "Added: $($item.name)" -ForegroundColor Green
    }
    catch {
        Write-Host "Failed to add: $($item.name)" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Demo equipment added successfully!" -ForegroundColor Green
Write-Host "Refresh your dashboard to see the new equipment." -ForegroundColor Yellow
