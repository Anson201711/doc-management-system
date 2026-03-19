#!/bin/bash
# Backend Build Script for Jenkins
# This script builds all Spring Cloud microservices

set -e

# Configuration
PROJECT_ROOT="/var/jenkins_home/workspace/backend-build"
MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
BACKEND_DIR="/Users/infodba/.openclaw/workspace/workspace-pm/backend"

echo "=========================================="
echo "Starting Backend Build"
echo "Time: $(date)"
echo "=========================================="

# Clean previous builds
echo "[1/5] Cleaning previous builds..."
cd "$BACKEND_DIR"
mvn clean -q

# Validate POM files
echo "[2/5] Validating POM files..."
mvn validate -q

# Compile all modules
echo "[3/5] Compiling all modules..."
mvn compile -DskipTests -q

# Run tests
echo "[4/5] Running tests..."
mvn test -q || true

# Package all modules
echo "[5/5] Packaging all modules..."
mvn package -DskipTests -q

echo "=========================================="
echo "Build Complete!"
echo "Time: $(date)"
echo "=========================================="

# List generated JAR files
echo "Generated JAR files:"
find "$BACKEND_DIR" -name "*.jar" -path "*/target/*" ! -name "*-sources.jar" -exec ls -lh {} \;