#!/bin/bash

# Anti-Vacuum Fabric Mod Setup Script
# This script sets up the development environment for the anti-vacuum Fabric mod on a cloud server

set -e  # Exit on any error

echo "🚀 Starting Anti-Vacuum Fabric Mod Setup..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Detect OS
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        if [ -f /etc/debian_version ]; then
            OS="debian"
        elif [ -f /etc/redhat-release ]; then
            OS="redhat"
        elif [ -f /etc/arch-release ]; then
            OS="arch"
        else
            OS="linux"
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        OS="macos"
    else
        OS="unknown"
    fi
    print_status "Detected OS: $OS"
}

# Update system packages
update_system() {
    print_status "Updating system packages..."
    case $OS in
        "debian")
            sudo apt-get update -y
            sudo apt-get upgrade -y
            ;;
        "redhat")
            sudo yum update -y
            ;;
        "arch")
            sudo pacman -Syu --noconfirm
            ;;
        "macos")
            print_warning "macOS detected. Please ensure Homebrew is installed."
            if ! command -v brew &> /dev/null; then
                print_error "Homebrew not found. Please install it first: https://brew.sh"
                exit 1
            fi
            brew update
            ;;
        *)
            print_warning "Unknown OS. Skipping system update."
            ;;
    esac
    print_success "System packages updated"
}

# Install Java 21
install_java() {
    print_status "Checking for Java 21..."
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" = "21" ]; then
            print_success "Java 21 is already installed"
            return
        else
            print_warning "Java $JAVA_VERSION found, but Java 21 is required"
        fi
    fi
    
    print_status "Installing Java 21..."
    case $OS in
        "debian")
            sudo apt-get install -y openjdk-21-jdk
            ;;
        "redhat")
            sudo yum install -y java-21-openjdk-devel
            ;;
        "arch")
            sudo pacman -S --noconfirm jdk21-openjdk
            ;;
        "macos")
            brew install openjdk@21
            # Add to PATH
            echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
            export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
            ;;
        *)
            print_error "Unsupported OS for automatic Java installation"
            print_status "Please install Java 21 manually"
            exit 1
            ;;
    esac
    
    # Verify installation
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" = "21" ]; then
            print_success "Java 21 installed successfully"
        else
            print_error "Java 21 installation failed or wrong version installed"
            exit 1
        fi
    else
        print_error "Java installation failed"
        exit 1
    fi
}

# Install Git (if not present)
install_git() {
    if command -v git &> /dev/null; then
        print_success "Git is already installed"
        return
    fi
    
    print_status "Installing Git..."
    case $OS in
        "debian")
            sudo apt-get install -y git
            ;;
        "redhat")
            sudo yum install -y git
            ;;
        "arch")
            sudo pacman -S --noconfirm git
            ;;
        "macos")
            brew install git
            ;;
        *)
            print_error "Unsupported OS for automatic Git installation"
            exit 1
            ;;
    esac
    print_success "Git installed successfully"
}

# Install additional development tools
install_dev_tools() {
    print_status "Installing development tools..."
    case $OS in
        "debian")
            sudo apt-get install -y curl wget unzip build-essential
            ;;
        "redhat")
            sudo yum groupinstall -y "Development Tools"
            sudo yum install -y curl wget unzip
            ;;
        "arch")
            sudo pacman -S --noconfirm curl wget unzip base-devel
            ;;
        "macos")
            brew install curl wget unzip
            ;;
        *)
            print_warning "Skipping development tools installation for unknown OS"
            ;;
    esac
    print_success "Development tools installed"
}

# Set up environment variables
setup_environment() {
    print_status "Setting up environment variables..."
    
    # Set JAVA_HOME
    case $OS in
        "debian"|"redhat"|"arch")
            JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
            ;;
        "macos")
            JAVA_HOME="/opt/homebrew/opt/openjdk@21"
            ;;
        *)
            JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
            ;;
    esac
    
    # Add to shell profile
    SHELL_PROFILE=""
    if [ -f ~/.bashrc ]; then
        SHELL_PROFILE="$HOME/.bashrc"
    elif [ -f ~/.zshrc ]; then
        SHELL_PROFILE="$HOME/.zshrc"
    elif [ -f ~/.profile ]; then
        SHELL_PROFILE="$HOME/.profile"
    fi
    
    if [ -n "$SHELL_PROFILE" ]; then
        echo "export JAVA_HOME=$JAVA_HOME" >> "$SHELL_PROFILE"
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$SHELL_PROFILE"
        print_success "Environment variables added to $SHELL_PROFILE"
    fi
    
    # Export for current session
    export JAVA_HOME="$JAVA_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
    
    print_success "Environment variables configured"
}

# Make gradlew executable and test build
setup_gradle() {
    print_status "Setting up Gradle..."
    
    # Make gradlew executable
    chmod +x ./gradlew
    
    print_status "Testing Gradle setup..."
    ./gradlew --version
    
    print_success "Gradle is ready"
}

# Build the project
build_project() {
    print_status "Building the project..."
    
    # Clean and build
    ./gradlew clean build
    
    print_success "Project built successfully"
}

# Create development scripts
create_dev_scripts() {
    print_status "Creating development helper scripts..."
    
    # Create run script
    cat > run_dev.sh << 'EOF'
#!/bin/bash
# Development run script
echo "Starting development environment..."
./gradlew runClient
EOF
    chmod +x run_dev.sh
    
    # Create build script
    cat > build.sh << 'EOF'
#!/bin/bash
# Build script
echo "Building project..."
./gradlew clean build
echo "Build complete. JAR files are in build/libs/"
EOF
    chmod +x build.sh
    
    # Create test script
    cat > test.sh << 'EOF'
#!/bin/bash
# Test script
echo "Running tests..."
./gradlew test
EOF
    chmod +x test.sh
    
    print_success "Development scripts created"
}

# Main setup function
main() {
    echo "=================================================="
    echo "  Anti-Vacuum Fabric Mod Development Setup"
    echo "=================================================="
    echo ""
    
    detect_os
    update_system
    install_git
    install_java
    install_dev_tools
    setup_environment
    setup_gradle
    build_project
    create_dev_scripts
    
    echo ""
    echo "=================================================="
    print_success "Setup completed successfully!"
    echo "=================================================="
    echo ""
    echo "📋 Next steps:"
    echo "  1. Source your shell profile: source ~/.bashrc (or ~/.zshrc)"
    echo "  2. Run './run_dev.sh' to start the development client"
    echo "  3. Run './build.sh' to build the mod"
    echo "  4. Run './test.sh' to run tests"
    echo ""
    echo "📁 Project structure:"
    echo "  - Source code: src/"
    echo "  - Built JARs: build/libs/"
    echo "  - Gradle wrapper: ./gradlew"
    echo ""
    echo "🔧 Useful commands:"
    echo "  - ./gradlew runClient    # Run Minecraft client with mod"
    echo "  - ./gradlew runServer    # Run Minecraft server with mod"
    echo "  - ./gradlew build        # Build the mod"
    echo "  - ./gradlew clean        # Clean build artifacts"
    echo ""
    print_success "Happy modding! 🎮"
}

# Run main function
main "$@" 