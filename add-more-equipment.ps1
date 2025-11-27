# Script to add 20 more equipment items with varied types and locations
$API_URL = "https://equipment-failure-risk-predictor.onrender.com/api/v1"

# 20 equipment items with diverse types
$equipment = @(
    @{ name = "Hydraulic Press HP-101"; type = "PRESS"; location = "Factory - Assembly Line 1"; installationDate = "2019-03-15"; status = "OPERATIONAL" },
    @{ name = "Cooling Tower CT-201"; type = "COOLING_SYSTEM"; location = "Rooftop - Building B"; installationDate = "2018-07-22"; status = "OPERATIONAL" },
    @{ name = "Air Compressor AC-301"; type = "COMPRESSOR"; location = "Utility Room - Ground Floor"; installationDate = "2020-11-10"; status = "OPERATIONAL" },
    @{ name = "Conveyor Belt CB-401"; type = "CONVEYOR"; location = "Warehouse - Zone C"; installationDate = "2021-02-28"; status = "OPERATIONAL" },
    @{ name = "Boiler System BS-501"; type = "BOILER"; location = "Basement - Mechanical Room"; installationDate = "2017-05-14"; status = "OPERATIONAL" },
    @{ name = "Electric Motor EM-601"; type = "MOTOR"; location = "Production Floor"; installationDate = "2022-01-20"; status = "OPERATIONAL" },
    @{ name = "Ventilation Fan VF-701"; type = "FAN"; location = "Building C - Corridor"; installationDate = "2019-09-05"; status = "OPERATIONAL" },
    @{ name = "Chiller Unit CU-801"; type = "HVAC"; location = "Data Center"; installationDate = "2020-06-18"; status = "OPERATIONAL" },
    @{ name = "Pump Station PS-901"; type = "PUMP"; location = "Water Treatment"; installationDate = "2018-12-03"; status = "OPERATIONAL" },
    @{ name = "Transformer TR-1001"; type = "TRANSFORMER"; location = "Substation A"; installationDate = "2016-08-25"; status = "OPERATIONAL" },
    @{ name = "Turbine Generator TG-1101"; type = "GENERATOR"; location = "Power Plant - Unit 1"; installationDate = "2015-04-12"; status = "OPERATIONAL" },
    @{ name = "Conveyor System CS-1201"; type = "CONVEYOR"; location = "Distribution Center"; installationDate = "2021-07-30"; status = "OPERATIONAL" },
    @{ name = "HVAC Rooftop Unit RTU-1301"; type = "HVAC"; location = "Building D - Roof"; installationDate = "2019-11-22"; status = "OPERATIONAL" },
    @{ name = "Centrifugal Pump CP-1401"; type = "PUMP"; location = "Chemical Processing"; installationDate = "2020-03-17"; status = "OPERATIONAL" },
    @{ name = "Air Handler AH-1501"; type = "HVAC"; location = "Laboratory Wing"; installationDate = "2018-05-09"; status = "OPERATIONAL" },
    @{ name = "Industrial Fan IF-1601"; type = "FAN"; location = "Foundry - Ventilation"; installationDate = "2017-10-28"; status = "MAINTENANCE" },
    @{ name = "Screw Compressor SC-1701"; type = "COMPRESSOR"; location = "Manufacturing Plant"; installationDate = "2021-09-14"; status = "OPERATIONAL" },
    @{ name = "Backup Generator BG-1801"; type = "GENERATOR"; location = "Emergency Services"; installationDate = "2016-12-01"; status = "OPERATIONAL" },
    @{ name = "Heat Exchanger HE-1901"; type = "HEAT_EXCHANGER"; location = "Process Unit 3"; installationDate = "2019-02-19"; status = "OPERATIONAL" },
    @{ name = "Vacuum Pump VP-2001"; type = "PUMP"; location = "Clean Room Facility"; installationDate = "2020-08-07"; status = "OPERATIONAL" }
)

Write-Host "Adding 20 additional equipment items..." -ForegroundColor Cyan
Write-Host ""

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
Write-Host "Equipment addition complete!" -ForegroundColor Green
