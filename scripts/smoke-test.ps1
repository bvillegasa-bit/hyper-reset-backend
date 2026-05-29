<#
.SYNOPSIS
    Hyper Reset API — Smoke Tests (PowerShell)
.DESCRIPTION
    Runs smoke tests against the running backend API.
    Start the backend first: .\mvnw.cmd spring-boot:run
.NOTES
    Run: powershell -ExecutionPolicy Bypass -File scripts\smoke-test.ps1
#>

$BaseUrl = "http://localhost:8080"
$Pass = 0
$Fail = 0
$Token = $null

function Write-Pass { $script:Pass++; Write-Host "  ✅ PASS" -ForegroundColor Green }
function Write-Fail($msg) { $script:Fail++; Write-Host "  ❌ FAIL: $msg" -ForegroundColor Red }

Write-Host "`n=========================================" -ForegroundColor Cyan
Write-Host "  Hyper Reset API — Smoke Tests" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# ------------------------------------------------------------------
# 1. Health Check
# ------------------------------------------------------------------
Write-Host "[1/5] Health Check — GET /api/health" -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "$BaseUrl/api/health" -Method Get -UseBasicParsing
    if ($health.StatusCode -eq 200) { Write-Pass } else { Write-Fail "Expected 200, got $($health.StatusCode)" }
} catch {
    Write-Fail "Health check failed: $_"
}
Write-Host ""

# ------------------------------------------------------------------
# 2. Register Coach
# ------------------------------------------------------------------
Write-Host "[2/5] Register Coach — POST /api/auth/register" -ForegroundColor Yellow
$registerBody = @{
    nombre   = "Carlos Ruiz"
    email    = "carlos.smoke@test.com"
    password = "password123"
    rol      = "COACH"
} | ConvertTo-Json

try {
    $register = Invoke-WebRequest -Uri "$BaseUrl/api/auth/register" -Method Post `
        -Body $registerBody -ContentType "application/json" -UseBasicParsing
    if ($register.StatusCode -eq 201) {
        Write-Pass
        $response = $register.Content | ConvertFrom-Json
        if ($response.data -and $response.data.token) {
            $Token = $response.data.token
        }
    } else {
        Write-Fail "Expected 201, got $($register.StatusCode)"
    }
} catch {
    # 409 Conflict means already registered — not a failure
    if ($_.Exception.Response.StatusCode -eq 409) {
        Write-Host "  ⚠ Already registered (409) — continuing" -ForegroundColor Yellow
        Write-Pass
    } else {
        Write-Fail "Register failed: $_"
    }
}
Write-Host ""

# ------------------------------------------------------------------
# 3. Login
# ------------------------------------------------------------------
Write-Host "[3/5] Login Coach — POST /api/auth/login" -ForegroundColor Yellow
$loginBody = @{
    email    = "carlos.smoke@test.com"
    password = "password123"
} | ConvertTo-Json

try {
    $login = Invoke-WebRequest -Uri "$BaseUrl/api/auth/login" -Method Post `
        -Body $loginBody -ContentType "application/json" -UseBasicParsing
    if ($login.StatusCode -eq 200) {
        Write-Pass
        $response = $login.Content | ConvertFrom-Json
        if (-not $Token -and $response.data -and $response.data.token) {
            $Token = $response.data.token
        }
    } else {
        Write-Fail "Expected 200, got $($login.StatusCode)"
    }
} catch {
    Write-Fail "Login failed: $_"
}

if ($Token) {
    Write-Host "  Token obtained: $($Token.Substring(0, [Math]::Min(30, $Token.Length)))..." -ForegroundColor Gray
} else {
    Write-Host "  ⚠ Could not obtain token" -ForegroundColor Yellow
}
Write-Host ""

# ------------------------------------------------------------------
# 4. Get Profile
# ------------------------------------------------------------------
Write-Host "[4/5] Get Profile — GET /api/auth/profile" -ForegroundColor Yellow
if ($Token) {
    try {
        $profile = Invoke-WebRequest -Uri "$BaseUrl/api/auth/profile" -Method Get `
            -Headers @{ Authorization = "Bearer $Token" } -UseBasicParsing
        if ($profile.StatusCode -eq 200) { Write-Pass } else { Write-Fail "Expected 200, got $($profile.StatusCode)" }
    } catch {
        Write-Fail "Profile request failed: $_"
    }
} else {
    Write-Fail "Skipped — no token available"
}
Write-Host ""

# ------------------------------------------------------------------
# 5. Unauthorized Access
# ------------------------------------------------------------------
Write-Host "[5/5] Unauthorized Access — GET /api/auth/profile (no token)" -ForegroundColor Yellow
try {
    $unauth = Invoke-WebRequest -Uri "$BaseUrl/api/auth/profile" -Method Get -UseBasicParsing
    Write-Fail "Expected 401, got $($unauth.StatusCode)"
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Pass
    } else {
        Write-Fail "Expected 401, got $($_.Exception.Response.StatusCode)"
    }
}
Write-Host ""

# ------------------------------------------------------------------
# Summary
# ------------------------------------------------------------------
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  Results: $Pass passed, $Fail failed" -ForegroundColor $(if ($Fail -gt 0) { "Red" } else { "Green" })
Write-Host "=========================================" -ForegroundColor Cyan

if ($Fail -gt 0) { exit 1 }
