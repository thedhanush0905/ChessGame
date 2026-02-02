#!/bin/bash

# Test the API endpoints

BASE_URL="${1:-http://localhost:8080}"
USERNAME="${2:-testuser$(date +%s)}"
EMAIL="${3:-test$(date +%s)@example.com}"
PASSWORD="${4:-testpass123}"

echo "🧪 Testing IndiChess API"
echo "📍 Base URL: $BASE_URL"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_code=${4:-200}
    
    echo -n "  Testing $method $endpoint... "
    
    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Authorization: Bearer $TOKEN" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            -d "$data" \
            "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✅${NC} ($http_code)"
        echo "$body"
    else
        echo -e "${RED}❌${NC} (Expected $expected_code, got $http_code)"
        echo "$body"
    fi
    echo ""
}

# Test 1: Sign Up
echo -e "${YELLOW}=== Test 1: Sign Up ===${NC}"
signup_response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"$USERNAME\",
        \"email\": \"$EMAIL\",
        \"password\": \"$PASSWORD\",
        \"confirmPassword\": \"$PASSWORD\"
    }" \
    "$BASE_URL/api/auth/signup")

http_code=$(echo "$signup_response" | tail -n 1)
body=$(echo "$signup_response" | head -n -1)

if [ "$http_code" = "201" ]; then
    echo -e "${GREEN}✅${NC} Sign up successful"
    TOKEN=$(echo "$body" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
    USER_ID=$(echo "$body" | grep -o '"userId":[0-9]*' | cut -d':' -f2)
    echo "  Token: ${TOKEN:0:50}..."
    echo "  User ID: $USER_ID"
else
    echo -e "${RED}❌${NC} Sign up failed (HTTP $http_code)"
    echo "$body"
    exit 1
fi
echo ""

# Test 2: Login
echo -e "${YELLOW}=== Test 2: Login ===${NC}"
test_endpoint "POST" "/api/auth/login" "{
    \"username\": \"$USERNAME\",
    \"password\": \"$PASSWORD\"
}" "200"

# Test 3: Get User Profile
echo -e "${YELLOW}=== Test 3: Get User Profile ===${NC}"
if [ ! -z "$USER_ID" ]; then
    test_endpoint "GET" "/api/users/$USER_ID" "" "200"
else
    echo "  Skipping (User ID not found)"
fi

# Test 4: Update User Profile
echo -e "${YELLOW}=== Test 4: Update User Profile ===${NC}"
if [ ! -z "$USER_ID" ]; then
    test_endpoint "PUT" "/api/users/$USER_ID" "{
        \"fullName\": \"Test User\",
        \"country\": \"USA\",
        \"bio\": \"Testing microservices\"
    }" "200"
else
    echo "  Skipping (User ID not found)"
fi

# Test 5: Create Game
echo -e "${YELLOW}=== Test 5: Create Game ===${NC}"
test_endpoint "POST" "/api/games?whitePlayerId=1&blackPlayerId=2" "" "201"

# Test 6: Get Leaderboard
echo -e "${YELLOW}=== Test 6: Get Leaderboard ===${NC}"
test_endpoint "GET" "/api/users/leaderboard?page=0&size=10" "" "200"

echo ""
echo -e "${GREEN}✅ API tests completed!${NC}"
