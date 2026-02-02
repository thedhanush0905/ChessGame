#!/bin/bash

# Helper script to initialize databases in PostgreSQL

POSTGRES_HOST=${1:-postgres}
POSTGRES_USER=${2:-indichess_user}
POSTGRES_PASSWORD=${3:-changeme123!secure}

echo "🐘 Initializing PostgreSQL databases for IndiChess..."

# Function to run SQL
run_sql() {
    local database=$1
    local sql=$2
    
    PGPASSWORD=$POSTGRES_PASSWORD psql \
        -h $POSTGRES_HOST \
        -U $POSTGRES_USER \
        -d $database \
        -c "$sql"
}

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if PGPASSWORD=$POSTGRES_PASSWORD psql -h $POSTGRES_HOST -U $POSTGRES_USER -d postgres -c "\q" 2>/dev/null; then
        echo "✅ PostgreSQL is ready!"
        break
    fi
    attempt=$((attempt+1))
    echo "Attempt $attempt/$max_attempts..."
    sleep 2
done

if [ $attempt -eq $max_attempts ]; then
    echo "❌ PostgreSQL not ready after $max_attempts attempts"
    exit 1
fi

# Create databases
echo "📦 Creating databases..."
PGPASSWORD=$POSTGRES_PASSWORD psql -h $POSTGRES_HOST -U $POSTGRES_USER -d postgres << EOF
CREATE DATABASE indichess_auth;
CREATE DATABASE indichess_user;
CREATE DATABASE indichess_game;
EOF

echo "✅ Databases created!"

# Create schemas (optional - applications will use Hibernate/JPA)
echo "🏗️ Creating initial schema structure..."

# You can add specific DDL here if needed
# The applications will handle schema creation via Hibernate

echo "✅ PostgreSQL initialization complete!"
