# AI Auto Shorts — Android starter

This repository contains a Java Android app starter and GitHub Actions workflow that compiles a debug APK.

## Build APK on GitHub using a phone
1. Create a GitHub repository named `AI-Auto-Shorts`.
2. Upload all files and folders from this project, preserving their paths.
3. Open the repository's **Actions** tab.
4. Select **Build Android APK** and tap **Run workflow** (or push to `main`).
5. Open the completed workflow run, then download the `AI-Auto-Shorts-debug-apk` artifact.

## Important scope note
This is a starter UI, not a finished AI clipping engine. The app currently accepts/presents YouTube and Google Drive URLs but does not download or process videos. To enable actual AI clipping, connect a backend that can lawfully access the source video, transcribe speech, score moments, cut clips, render captions, and return MP4 files. YouTube links are not generally direct video-download URLs; Drive links may require OAuth or public sharing permissions.

No watermark is added by this starter app. It cannot remove a watermark already present in source footage.
