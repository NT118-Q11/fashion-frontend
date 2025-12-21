# Fashion Frontend

Android app for fashion e-commerce with Google Sign-In integration.

## Setup

### Environment Configuration

This app uses environment variables for sensitive configuration like Google Sign-In credentials. You need to set up a `.env` file before building the app.

#### Option 1: Project Root .env (Recommended)
1. Copy `.env.example` to `.env` in the project root
2. Fill in your actual Google credentials:
```bash
cp .env.example .env
```

#### Option 2: Raw Resources
1. Create/edit `app/src/main/res/raw/env` file
2. Add your Google credentials in the same format

#### Option 3: Assets Folder  
1. Create/edit `app/src/main/assets/.env` file
2. Add your Google credentials in the same format

### Required Environment Variables

- `GOOGLE_CLIENT_ID`: Your Google OAuth 2.0 Client ID
- `GOOGLE_CLIENT_SECRET`: Your Google OAuth 2.0 Client Secret

## Building

```bash
./gradlew assembleDebug
```

**Note**: The app will crash if .env file is not properly configured with Google credentials.

