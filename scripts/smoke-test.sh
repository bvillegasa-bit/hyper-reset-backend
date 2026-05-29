#!/bin/bash
# ==========================================================
# Hyper Reset API — Smoke Tests
# ==========================================================
# Usage:
#   1. Start the backend: ./mvnw.cmd spring-boot:run
#   2. In another terminal: bash scripts/smoke-test.sh
# ==========================================================

BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

pass() { PASS=$((PASS+1)); echo "  ✅ PASS"; }
fail() { FAIL=$((FAIL+1)); echo "  ❌ FAIL: $1"; }

echo ""
echo "========================================="
echo "  Hyper Reset API — Smoke Tests"
echo "========================================="
echo ""

# ------------------------------------------------------------------
# 1. Health Check
# ------------------------------------------------------------------
echo "[1/5] Health Check — GET /api/health"
HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/health" 2>/dev/null)
if [ "$HEALTH" = "200" ]; then
    pass
else
    fail "Expected 200, got $HEALTH"
fi
echo ""

# ------------------------------------------------------------------
# 2. Register a new coach
# ------------------------------------------------------------------
echo "[2/5] Register Coach — POST /api/auth/register"
REGISTER_HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "nombre": "Carlos Ruiz",
        "email": "carlos.smoke@test.com",
        "password": "password123",
        "rol": "COACH"
    }' 2>/dev/null)

# Also capture response for token extraction
REGISTER_RESP=$(curl -s -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "nombre": "Carlos Ruiz",
        "email": "carlos.smoke@test.com",
        "password": "password123",
        "rol": "COACH"
    }' 2>/dev/null)

# Check if register was 201 Created or 409 (already exists from previous run)
if [ "$REGISTER_HTTP" = "201" ] || [ "$REGISTER_HTTP" = "409" ]; then
    echo "  HTTP: $REGISTER_HTTP"
    if [ "$REGISTER_HTTP" = "201" ]; then
        pass
    else
        echo "  ⚠ Already registered (409) — continuing"
        pass
    fi
else
    fail "Expected 201, got $REGISTER_HTTP"
fi
echo ""

# ------------------------------------------------------------------
# 3. Login with the registered coach
# ------------------------------------------------------------------
echo "[3/5] Login Coach — POST /api/auth/login"
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "carlos.smoke@test.com",
        "password": "password123"
    }' 2>/dev/null)

LOGIN_HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "email": "carlos.smoke@test.com",
        "password": "password123"
    }' 2>/dev/null)

if [ "$LOGIN_HTTP" = "200" ]; then
    echo "  HTTP: $LOGIN_HTTP"
    pass
else
    fail "Expected 200, got $LOGIN_HTTP"
fi

# Extract token from response
TOKEN=$(echo "$LOGIN_RESP" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
if [ -z "$TOKEN" ]; then
    # Try extracting from nested data object
    TOKEN=$(echo "$LOGIN_RESP" | grep -o '"data":{[^}]*"token":"[^"]*"' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
fi

if [ -n "$TOKEN" ]; then
    echo "  Token obtained: ${TOKEN:0:30}..."
else
    echo "  ⚠ Could not extract token, some tests may fail"
fi
echo ""

# ------------------------------------------------------------------
# 4. Get profile with token
# ------------------------------------------------------------------
echo "[4/5] Get Profile — GET /api/auth/profile"
if [ -n "$TOKEN" ]; then
    PROFILE_HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/auth/profile" \
        -H "Authorization: Bearer $TOKEN" 2>/dev/null)

    if [ "$PROFILE_HTTP" = "200" ]; then
        pass
    else
        fail "Expected 200, got $PROFILE_HTTP"
    fi
else
    fail "Skipped — no token available"
fi
echo ""

# ------------------------------------------------------------------
# 5. Access protected endpoint without token (should fail 401)
# ------------------------------------------------------------------
echo "[5/5] Unauthorized Access — GET /api/auth/profile (no token)"
NOAUTH_HTTP=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/auth/profile" 2>/dev/null)

if [ "$NOAUTH_HTTP" = "401" ]; then
    pass
else
    fail "Expected 401, got $NOAUTH_HTTP"
fi
echo ""

# ------------------------------------------------------------------
# Summary
# ------------------------------------------------------------------
echo "========================================="
echo "  Results: $PASS passed, $FAIL failed"
echo "========================================="

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
