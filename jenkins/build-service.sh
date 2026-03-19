#!/bin/bash
# Build script for individual service
# Usage: ./build-service.sh <service-name>
# Example: ./build-service.sh user-service

SERVICE_NAME=${1:-}
BACKEND_DIR="/Users/infodba/.openclaw/workspace/workspace-pm/backend"

if [ -z "$SERVICE_NAME" ]; then
    echo "Usage: $0 <service-name>"
    echo "Available services:"
    ls -1 "$BACKEND_DIR" | grep -v -E '(pom.xml|tests)'
    exit 1
fi

if [ ! -d "$BACKEND_DIR/$SERVICE_NAME" ]; then
    echo "Error: Service '$SERVICE_NAME' not found"
    exit 1
fi

echo "=========================================="
echo "Building service: $SERVICE_NAME"
echo "Time: $(date)"
echo "=========================================="

cd "$BACKEND_DIR"

# Build the service
mvn clean package -pl "$SERVICE_NAME" -am -DskipTests

echo "=========================================="
echo "Build complete for: $SERVICE_NAME"
echo "=========================================="

# Show output
JAR_FILE=$(find "$SERVICE_NAME/target" -name "*.jar" ! -name "*-sources.jar" 2>/dev/null | head -1)
if [ -n "$JAR_FILE" ]; then
    echo "Generated JAR: $JAR_FILE"
    ls -lh "$JAR_FILE"
fi