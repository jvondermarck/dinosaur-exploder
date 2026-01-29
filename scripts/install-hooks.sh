#!/bin/bash
#
# Install git hooks for the dinosaur-exploder project
#
# Usage:
#   ./scripts/install-hooks.sh
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
HOOKS_DIR="$PROJECT_ROOT/.git/hooks"
SOURCE_HOOKS_DIR="$PROJECT_ROOT/hooks"

echo "Installing git hooks..."

# Check if we're in a git repository
if [ ! -d "$PROJECT_ROOT/.git" ]; then
    echo "Error: Not a git repository. Please run this script from the project root."
    exit 1
fi

# Install all hooks
for hook_file in "$SOURCE_HOOKS_DIR"/*; do
    if [ -f "$hook_file" ]; then
        hook_name=$(basename "$hook_file")
        
        cp "$hook_file" "$HOOKS_DIR/$hook_name"
        chmod +x "$HOOKS_DIR/$hook_name"
        
        echo "✅ $hook_name hook installed"
    else
        echo "Skipping $(basename "$hook_file"): not a valid hook file."
    fi
done

echo ""
echo "Git hooks installation complete!"
echo "The pre-commit hook will run 'mvn pmd:check' before each commit."
