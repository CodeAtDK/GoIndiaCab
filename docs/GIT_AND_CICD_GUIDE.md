# GoIndiaCab — Manual Git Push & CI/CD Setup Guide

This guide gives you the exact step-by-step commands to manually push this codebase to your remote Git repository (GitHub) and activate the production CI/CD pipelines.

---

## Part 1: Step-by-Step Manual Git Push

Open your terminal, ensure you are in the project root directory:

```bash
cd "/Users/dhruva/Documents/KMP Android/GoIndiaCab"
```

### Step 1: Initialize Git Repository
Initialize the local git repository with the default branch set to `main`:

```bash
git init -b main
```

*(If your git version does not support `-b main`, run `git init` followed by `git branch -M main`)*.

---

### Step 2: Verify `.gitignore` Status
Check that all build caches, `.idea`, `.DS_Store`, and `local.properties` are properly ignored:

```bash
git status
```

> **Verification:** You should **NOT** see any `.gradle/`, `build/`, `.idea/`, or `local.properties` listed under untracked files.

---

### Step 3: Stage and Commit All Files
Stage all clean project files, source code, designs, documentation, and GitHub Actions workflows:

```bash
git add .
git commit -m "feat: initial commit for GoIndiaCab KMP with production CI/CD workflows"
```

---

### Step 4: Create a New Repository on GitHub
1. Go to [github.com/new](https://github.com/new).
2. Repository name: `GoIndiaCab` (or your preferred name).
3. Set visibility: **Private** or **Public**.
4. **DO NOT** check "Add a README file", ".gitignore", or "license" (we already have them).
5. Click **Create repository**.

---

### Step 5: Add Remote Origin & Push to GitHub
Copy your repository URL from GitHub (HTTPS or SSH) and run:

#### Using HTTPS:
```bash
git remote add origin https://github.com/<your-username>/GoIndiaCab.git
git push -u origin main
```

#### Or Using SSH:
```bash
git remote add origin git@github.com:<your-username>/GoIndiaCab.git
git push -u origin main
```

---

## Part 2: What Happens After You Push (CI/CD Workflows)

### 1. Automated Pull Request & Push CI (`ci.yml`)
As soon as you push to `main` (or whenever a developer opens a Pull Request to `main` or `develop`), GitHub Actions automatically triggers:
- **🔍 Validate & Lint:**
  - Verifies the Gradle Wrapper checksum.
  - Sets up Java 21 with automatic Gradle dependency caching.
  - Runs Android Lint (`./gradlew :app:androidApp:lintDebug`).
- **🧪 Unit Tests:**
  - Runs unit tests for Android and the Ktor backend server.
  - Publishes test reports as downloadable artifacts.
- **📱 Build Android Artifacts:**
  - Compiles **Debug APK** (`app/androidApp/build/outputs/apk/debug/`).
  - Compiles **Release Bundle (.aab)** for Google Play Store.
  - Attaches both artifacts to the GitHub Actions run for immediate QA testing and download!
- **🖥️ Build Desktop & Server:**
  - Compiles the Desktop JAR and backend server distribution.

---

### 2. Automated Production Release Deployment (`release.yml`)
Whenever you want to release a new version to testing or production:

1. Create and push a version tag from your terminal:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. GitHub Actions will automatically:
   - Compile the production Android App Bundle (`.aab`) and universal `.apk`.
   - Create a new **GitHub Release** titled `GoIndiaCab v1.0.0`.
   - Attach the `.apk` and `.aab` files directly to the release page for 1-click download.

---

## Part 3: (Optional) Play Store Release Keystore Secrets

If you want GitHub Actions to automatically sign the release AAB/APK with your production keystore, add these secrets in your GitHub repository (**Settings > Secrets and variables > Actions > Repository secrets**):

| Secret Name | Description |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded string of your `release.keystore` file (`base64 -i my-release.keystore`) |
| `KEYSTORE_PASSWORD` | Password for your release keystore |
| `KEY_ALIAS` | Key alias name inside the keystore |
| `KEY_PASSWORD` | Password for the key alias |

*(If these secrets are not configured, the workflow safely generates unsigned release artifacts).*
