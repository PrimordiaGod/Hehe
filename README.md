# MacroMaster - Android Macro Environment

MacroMaster is an advanced Android macro application that enables users to automate repetitive tasks through image recognition, AI-powered suggestions, and multi-layered task management.

## Features

- **Image Recognition**: Identify elements on screen without requiring root access
- **AI-Powered Automation**: Generate macros from natural language descriptions and optimize existing macros
- **Smart Screen Analysis**: Automatically detect actionable elements on screen
- **Multi-layered Task Management**: Run multiple tasks simultaneously with different priorities
- **Trigger-based Actions**: Monitor the screen for specific triggers and execute conditional actions
- **Task Interruption**: Automatically pause or stop tasks when specific conditions are met
- **Custom Macros**: Create, save, and edit custom macro sequences

## Requirements

- Android 8.0 (API level 26) or higher
- Permission to use Accessibility Services
- Permission to display overlays

## How It Works

MacroMaster uses Android's Accessibility Services, screen capture capabilities, and AI to detect elements on screen and perform actions. The app can:

1. Execute a sequence of taps and gestures
2. Monitor the screen for specific images or patterns
3. Interrupt ongoing tasks when triggers are detected
4. Switch between different task sequences based on screen conditions
5. Generate macros from natural language descriptions
6. Analyze screen content to suggest actions
7. Optimize macros for better performance

## AI Features

### Natural Language Macro Generation
Describe what you want your macro to do in plain English, and the AI will generate a sequence of actions for you.

Example: "Click login, type username 'admin', wait for 2 seconds, then click submit"

### Screen Analysis
The AI can analyze your current screen and suggest possible actions based on what it detects, such as buttons, input fields, and other UI elements.

### Macro Optimization
The AI can analyze your existing macros and suggest optimizations to make them more efficient and reliable.

## Use Cases

- Game automation
- UI testing
- Workflow automation
- Repetitive task automation
- Form filling
- Data entry

## Deployment

MacroMaster uses Codemagic for CI/CD. The configuration is defined in the `codemagic.yaml` file in the root directory.

### Build Types

- **Release Build**: Production-ready build with optimizations
- **Debug Build**: Development build with debugging enabled

### Deployment Channels

- **Internal Testing**: Builds are automatically deployed to internal testers
- **Google Play**: Release builds can be deployed to Google Play