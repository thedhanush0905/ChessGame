#!/bin/bash

# Generate JWT token for testing

SECRET="${1:-mySecretKeyForJWTTokenGenerationAndValidationPurposeThatIsLongEnough}"
USER_ID="${2:-1}"
USERNAME="${3:-testuser}"

echo "🔐 Generating JWT Token"
echo "User ID: $USER_ID"
echo "Username: $USERNAME"
echo ""

# This is a simplified example - for real JWT generation, use proper tools
# You can use online JWT generators at jwt.io

# Install jq if needed:
# brew install jq  (macOS)
# apt-get install jq  (Linux)

# For actual token generation in production, use proper JWT libraries

echo "JWT Token generation requires a proper implementation."
echo "In production, use:"
echo "  - Your service's login endpoint to get real tokens"
echo "  - Or use libraries like:"
echo "    - PyJWT (Python)"
echo "    - jsonwebtoken (Node.js)"
echo "    - jjwt (Java)"
echo ""
echo "For testing, login through the API:"
echo "  curl -X POST http://localhost:8080/api/auth/login \\"
echo "    -H \"Content-Type: application/json\" \\"
echo "    -d '{\"username\":\"$USERNAME\",\"password\":\"your_password\"}'"
