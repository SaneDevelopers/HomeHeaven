#!/bin/bash

# HomeHeaven Setup Script for macOS/Linux
# This script downloads Maven and installs all project dependencies

set -e  # Exit on error

echo "========================================="
echo "HomeHeaven Setup Script"
echo "========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
MAVEN_VERSION="3.9.6"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
MAVEN_DIR="./tools/apache-maven-${MAVEN_VERSION}"
JAVA_REQUIRED_VERSION=21

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if Java is installed
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge "$JAVA_REQUIRED_VERSION" ]; then
        print_success "Java $JAVA_VERSION is installed"
    else
        print_error "Java $JAVA_REQUIRED_VERSION or higher is required. Found Java $JAVA_VERSION"
        echo ""
        echo "Please install Java $JAVA_REQUIRED_VERSION from:"
        echo "  - Oracle: https://www.oracle.com/java/technologies/downloads/"
        echo "  - OpenJDK: https://adoptium.net/"
        echo ""
        echo "For macOS, you can also use Homebrew:"
        echo "  brew install openjdk@21"
        exit 1
    fi
else
    print_error "Java is not installed"
    echo ""
    echo "Please install Java $JAVA_REQUIRED_VERSION from:"
    echo "  - Oracle: https://www.oracle.com/java/technologies/downloads/"
    echo "  - OpenJDK: https://adoptium.net/"
    echo ""
    echo "For macOS, you can also use Homebrew:"
    echo "  brew install openjdk@21"
    exit 1
fi

# Check if Maven is already installed locally or in PATH
echo ""
echo "Checking Maven installation..."
MAVEN_CMD=""

if [ -f "$MAVEN_DIR/bin/mvn" ]; then
    print_success "Maven found in local tools directory"
    MAVEN_CMD="$MAVEN_DIR/bin/mvn"
elif command -v mvn &> /dev/null; then
    MAVEN_VERSION_INSTALLED=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven $MAVEN_VERSION_INSTALLED is installed in system PATH"
    MAVEN_CMD="mvn"
else
    print_warning "Maven not found. Downloading Maven ${MAVEN_VERSION}..."
    echo ""
    
    # Create tools directory
    mkdir -p tools
    cd tools
    
    # Download Maven
    echo "Downloading from: $MAVEN_URL"
    if command -v curl &> /dev/null; then
        curl -L -O "$MAVEN_URL"
    elif command -v wget &> /dev/null; then
        wget "$MAVEN_URL"
    else
        print_error "Neither curl nor wget is available. Please install one of them."
        exit 1
    fi
    
    # Extract Maven
    echo "Extracting Maven..."
    tar -xzf "apache-maven-${MAVEN_VERSION}-bin.tar.gz"
    rm "apache-maven-${MAVEN_VERSION}-bin.tar.gz"
    
    cd ..
    print_success "Maven ${MAVEN_VERSION} installed successfully"
    MAVEN_CMD="$MAVEN_DIR/bin/mvn"
fi

# Display Maven version
echo ""
echo "Maven version:"
$MAVEN_CMD -version | head -n 1

# Install project dependencies
echo ""
echo "========================================="
echo "Installing Project Dependencies"
echo "========================================="
echo ""
echo "This may take a few minutes on first run..."
echo ""

if $MAVEN_CMD clean install -DskipTests; then
    print_success "Dependencies installed successfully!"
else
    print_error "Failed to install dependencies"
    exit 1
fi

# Create uploads directory if it doesn't exist
echo ""
echo "Creating required directories..."
mkdir -p uploads
mkdir -p src/main/resources/static/uploads
print_success "Directories created"

# Display next steps
echo ""
echo "========================================="
echo "Setup Complete!"
echo "========================================="
echo ""
print_success "All dependencies have been installed successfully!"
echo ""
echo "Next steps:"
echo "  1. Configure your database in src/main/resources/application.properties"
echo "  2. Set up MySQL database (see DATABASE_QUICK_REFERENCE.md)"
echo "  3. Run the application:"
echo "     ./start-homeheaven.sh"
echo ""
echo "Or run with Maven directly:"
if [ "$MAVEN_CMD" = "$MAVEN_DIR/bin/mvn" ]; then
    echo "  $MAVEN_CMD spring-boot:run"
else
    echo "  mvn spring-boot:run"
fi
echo ""
