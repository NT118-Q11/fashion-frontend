# Google Sign-In Integration Guide

## Tổng quan
Dự án đã được tích hợp Google Sign-In SDK để cho phép người dùng đăng nhập/đăng ký bằng tài khoản Google. Khi người dùng đăng nhập thành công qua Google, ứng dụng sẽ lấy ID token và gọi backend API để xác thực.

## Các file đã thay đổi

### 1. **GoogleSignInManager.kt** (Mới)
Class quản lý toàn bộ flow Google Sign-In:
- Khởi tạo Google Sign-In Client
- Xử lý sign-in intent và kết quả
- Lấy ID token, email, name, và photo URL
- Hỗ trợ sign out và revoke access

### 2. **SignInFragment.kt** (Đã cập nhật)
- Thêm Google Sign-In button handler
- Tích hợp ActivityResultLauncher để xử lý kết quả Google Sign-In
- Gọi API `POST /api/auth/login-gmail` với ID token
- Xử lý cả đăng nhập email/password và Google Sign-In

### 3. **RegisterFragment.kt** (Đã cập nhật)
- Thêm Google Sign-In button handler  
- Tích hợp ActivityResultLauncher để xử lý kết quả Google Sign-In
- Gọi API `POST /api/auth/register-gmail` với ID token
- Xử lý cả đăng ký email/password và Google Sign-In

### 4. **AppRoute.kt** (Đã có sẵn)
- Định nghĩa API endpoints cho Google OAuth2:
  - `POST /api/auth/login-gmail` - Đăng nhập bằng Google
  - `POST /api/auth/register-gmail` - Đăng ký bằng Google
- Data class `GoogleOAuth2UserInfo` để gửi thông tin Google lên backend

### 5. **AndroidManifest.xml** (Đã cập nhật)
- Thêm permission `INTERNET` để gọi API

### 6. **gradle/libs.versions.toml** (Đã cập nhật)
- Downgrade AGP từ 8.13.0 xuống 8.12.0 để tương thích với IDE

### 7. **app/build.gradle.kts** (Đã có sẵn)
- Dependency `play-services-auth:21.2.0` đã được thêm

## Cấu hình Google Cloud Console

### Bước 1: Tạo Project trên Google Cloud Console
1. Truy cập https://console.cloud.google.com/
2. Tạo project mới hoặc chọn project hiện có
3. Enable **Google+ API** (hoặc **Google Identity Services**)

### Bước 2: Tạo OAuth 2.0 Client ID
1. Vào **APIs & Services** → **Credentials**
2. Click **Create Credentials** → **OAuth client ID**
3. Chọn **Application type**: **Web application**
4. Đặt tên (ví dụ: "Fashion App Backend")
5. Thêm **Authorized redirect URIs** nếu cần
6. Click **Create** và **copy Web Client ID** (dạng `xxxx.apps.googleusercontent.com`)

### Bước 3: (Optional) Tạo Android OAuth Client ID
1. Tạo thêm một OAuth client ID với type **Android**
2. Nhập **Package name**: `com.example.fashionapp`
3. Lấy **SHA-1 fingerprint**:
   ```bash
   # Debug keystore (cho development)
   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```
4. Paste SHA-1 fingerprint vào form
5. Click **Create**

## Cấu hình trong Code

### Cập nhật Web Client ID
Mở file `GoogleSignInManager.kt` và thay thế `YOUR_WEB_CLIENT_ID`:

```kotlin
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken("PASTE_YOUR_WEB_CLIENT_ID_HERE") // ← Thay đổi ở đây
    .requestEmail()
    .requestProfile()
    .build()
```

**Quan trọng:** Phải sử dụng **Web Client ID**, không phải Android Client ID!

## Flow hoạt động

### Đăng nhập với Google (Sign In)
1. User click "SIGN IN WITH GOOGLE" button
2. `SignInFragment` gọi `signInWithGoogle()`
3. Mở Google Account Picker qua `googleSignInLauncher`
4. User chọn tài khoản Google
5. `GoogleSignInManager.handleSignInResult()` xử lý kết quả:
   - Lấy **ID token** từ Google
   - Lấy email, name, photo URL
6. Gọi backend API `POST /api/auth/login-gmail` với payload:
   ```json
   {
     "idToken": "eyJhbGciOi...",
     "email": "user@gmail.com",
     "name": "User Name",
     "picture": "https://..."
   }
   ```
7. Backend verify ID token với Google và trả về user info
8. Navigate đến Home screen

### Đăng ký với Google (Register)
Flow tương tự nhưng gọi `POST /api/auth/register-gmail` thay vì login.

## Kiểm tra và Debug

### 1. Sync Gradle
```powershell
.\gradlew.bat --refresh-dependencies
```

### 2. Build project
```powershell
.\gradlew.bat assembleDebug
```

### 3. Chạy trên emulator/device
- Đảm bảo device/emulator có Google Play Services
- Đăng nhập tài khoản Google trên device
- Test Sign In và Register flow

### 4. Kiểm tra logs
```kotlin
// Thêm logs vào GoogleSignInManager.kt nếu cần debug
Log.d("GoogleSignIn", "ID Token: ${account.idToken}")
Log.d("GoogleSignIn", "Email: ${account.email}")
```

## Xử lý lỗi thường gặp

### Lỗi: "Developer Error" hoặc "Sign in failed: 10"
- **Nguyên nhân:** Web Client ID sai hoặc SHA-1 fingerprint không khớp
- **Giải pháp:** 
  - Kiểm tra lại Web Client ID
  - Đảm bảo SHA-1 fingerprint được thêm vào OAuth client (nếu dùng Android client)
  - Đợi vài phút sau khi thay đổi config trên Google Cloud

### Lỗi: "ApiException: 12500"
- **Nguyên nhân:** Google Play Services chưa cài hoặc cũ
- **Giải pháp:** Update Google Play Services trên device/emulator

### Lỗi: "INTERNET permission denied"
- **Giải pháp:** Đã thêm `<uses-permission android:name="android.permission.INTERNET" />` trong AndroidManifest.xml

### Backend API trả về 401/403
- **Nguyên nhân:** Backend không verify được ID token
- **Giải pháp:** 
  - Đảm bảo backend sử dụng Google API Client Library để verify token
  - Check Web Client ID trên backend khớp với client

## API Backend Expected

### POST /api/auth/login-gmail
**Request:**
```json
{
  "idToken": "string",
  "email": "string",
  "name": "string (optional)",
  "picture": "string (optional)"
}
```

**Response:**
```json
{
  "message": "string",
  "user": {
    "id": 123,
    "username": "string",
    "email": "string",
    "phoneNumber": "string (optional)",
    "userAddress": "string (optional)"
  }
}
```

### POST /api/auth/register-gmail
Format giống như login-gmail

## Tài liệu tham khảo
- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android/start)
- [Verify ID Token on Backend](https://developers.google.com/identity/sign-in/android/backend-auth)
- [OAuth 2.0 Client IDs](https://console.cloud.google.com/apis/credentials)

## Testing Checklist
- [ ] Cập nhật Web Client ID trong `GoogleSignInManager.kt`
- [ ] Sync Gradle thành công
- [ ] Build project không lỗi
- [ ] Test Google Sign-In trên emulator/device
- [ ] Test API call đến backend
- [ ] Test cả Sign In và Register flow
- [ ] Test error handling (cancel, network error, etc.)

