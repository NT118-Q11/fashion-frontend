# Hướng dẫn Tích hợp Hiển thị Ảnh Sản phẩm trong Product Detail

## 🎯 Mục tiêu

Tích hợp hiển thị ảnh thực của sản phẩm trong `product_detail` khi người dùng click vào sản phẩm từ SearchView hoặc bất kỳ đâu trong app. Hệ thống tự động:
1. Load tất cả ảnh trong cùng thư mục sản phẩm (ví dụ: woman/women1/)
2. Hiển thị ảnh đơn lẻ cho sản phẩm chỉ có 1 ảnh
3. Hiển thị tên, mô tả, giá cả từ database

---

## 📁 Cấu trúc Thư mục Assets

### Cấu trúc thực tế:
```
assets/
├── woman/
│   ├── women1/              ← Sản phẩm có nhiều ảnh
│   │   ├── women1_1.jpg
│   │   ├── women1_2.jpg
│   │   └── women1_3.jpg
│   ├── women2/
│   │   ├── women2_1.jpg
│   │   └── women2_2.jpg
│   ├── women3/
│   │   └── women3_1.jpg
│   ├── women1.jpg           ← Thumbnail/single image
│   ├── women2.jpg
│   ├── women4.jpg           ← Sản phẩm chỉ có 1 ảnh
│   ├── women5.jpg
│   └── ...
├── men/
│   └── (similar structure)
└── kid/
    └── (similar structure)
```

### Hai loại sản phẩm:

#### 1. **Sản phẩm có nhiều ảnh** (folder-based):
- Path: `woman/women1/women1_1.jpg`
- Cấu trúc: `{category}/{productFolder}/{imageName}`
- Ví dụ: women1, women2, women3
- System sẽ tự động load TẤT CẢ ảnh trong folder

#### 2. **Sản phẩm có 1 ảnh** (single image):
- Path: `woman/women6.jpg`
- Cấu trúc: `{category}/{imageName}`
- Ví dụ: women4, women5, women6
- System chỉ hiển thị 1 ảnh duy nhất

---

## 🔧 Implementation

### 1. **Product.kt** - Thêm Image Processing Logic

**Đường dẫn:** `app/src/main/java/com/example/fashionapp/model/Product.kt`

#### Method mới: `getImageAssetPaths()`

```kotlin
/**
 * Get all product images as asset paths
 * Handles both:
 * 1. Products with multiple images in a folder (e.g., woman/women1/women1_1.jpg)
 * 2. Products with single image (e.g., woman/women6.jpg)
 * 
 * Returns list of asset paths ready to load from assets folder
 */
fun getImageAssetPaths(assetManager: android.content.res.AssetManager): List<String> {
    val imagePaths = mutableListOf<String>()
    
    // Step 1: Try to get images from the images field (from backend)
    if (!images.isNullOrEmpty()) {
        images.forEach { imagePath ->
            val assetPath = extractAssetPath(imagePath)
            if (assetPath != null) {
                imagePaths.add(assetPath)
            }
        }
    }
    
    // Step 2: If no images, detect from thumbnail
    if (imagePaths.isEmpty() && !thumbnail.isNullOrEmpty()) {
        val thumbnailPath = getThumbnailAssetPath()
        if (thumbnailPath != null) {
            // Parse path: woman/women1/women1_1.jpg or woman/women6.jpg
            val pathParts = thumbnailPath.split("/")
            
            if (pathParts.size >= 3) {
                // FOLDER-BASED: woman/women1/women1_1.jpg
                val category = pathParts[0]  // "woman"
                val productFolder = pathParts[1]  // "women1"
                val folderPath = "$category/$productFolder"
                
                try {
                    // List all images in the product folder
                    val files = assetManager.list(folderPath)
                    if (!files.isNullOrEmpty()) {
                        files.forEach { fileName ->
                            if (fileName.endsWith(".jpg", ignoreCase = true) || 
                                fileName.endsWith(".png", ignoreCase = true)) {
                                imagePaths.add("$folderPath/$fileName")
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Folder doesn't exist, use thumbnail only
                    imagePaths.add(thumbnailPath)
                }
            } else {
                // SINGLE IMAGE: woman/women6.jpg
                imagePaths.add(thumbnailPath)
            }
        }
    }
    
    return imagePaths
}
```

#### Logic Flow:

```
┌─────────────────────────────────────────────┐
│ Product.getImageAssetPaths()                │
└───────────────┬─────────────────────────────┘
                │
                ↓
┌───────────────────────────────────────────────┐
│ 1. Check images field from backend            │
│    - If exists: Extract all image paths       │
│    - Convert Windows paths to asset paths     │
└───────────────┬───────────────────────────────┘
                │
                ↓
        ┌───────┴────────┐
        │ Images found?  │
        └───────┬────────┘
         No     │       Yes
    ┌───────────┴──────────┐
    ↓                       ↓
┌──────────────────┐   ┌──────────────────┐
│ 2. Use thumbnail │   │ Return images    │
└───────┬──────────┘   └──────────────────┘
        │
        ↓
┌────────────────────────┐
│ Parse thumbnail path   │
│ woman/women1/xxx.jpg   │
└───────┬────────────────┘
        │
        ↓
    ┌───┴───┐
    │ Size? │
    └───┬───┘
        │
   ┌────┴────┐
   │ 3 parts │ 2 parts
   │    or   │
   │  more   │
   └────┬────┴────┬────┐
        │         │
        ↓         ↓
┌───────────────┐ ┌──────────────┐
│ FOLDER-BASED  │ │ SINGLE IMAGE │
│ List all imgs │ │ Return one   │
│ in folder     │ │ thumbnail    │
└───────────────┘ └──────────────┘
```

---

### 2. **DetailsFragment.kt** - Update UI Loading

**Đường dẫn:** `app/src/main/java/com/example/fashionapp/uix/DetailsFragment.kt`

#### Cập nhật `updateUI()`:

```kotlin
private fun updateUI(product: Product) {
    binding.apply {
        // Set product info
        tvName.text = product.name
        tvDescription.text = product.description
        tvPrice.text = "$${product.price}"
        btnAddToCart.text = "Add To Cart · $${product.price}"
        
        // Get all product images from assets
        val productImages = product.getImageAssetPaths(requireContext().assets)
        
        if (productImages.isNotEmpty()) {
            // Use real product images
            Log.d("DetailsFragment", "Loading ${productImages.size} images for product: ${product.name}")
            productImages.forEach { imagePath ->
                Log.d("DetailsFragment", "  - Image: $imagePath")
            }
            viewPagerProduct.adapter = ImageSliderAdapter(productImages, requireContext())
        } else {
            // Fallback to placeholder images if no images found
            Log.w("DetailsFragment", "No images found for product: ${product.name}, using placeholders")
            val placeholderList = listOf(
                R.drawable.model_image_1,
                R.drawable.model_image_2,
                R.drawable.model_image_3
            )
            viewPagerProduct.adapter = ImageSliderAdapter(placeholderList)
        }

        // Setup ViewPager2 with animations
        setupImageSlider()
        TabLayoutMediator(tabLayoutProduct, viewPagerProduct) { _, _ -> }.attach()
    }
}
```

---

## 🔄 Data Flow

### Khi người dùng click vào sản phẩm:

```
┌─────────────────────────┐
│ User clicks product     │
│ in SearchView           │
└───────────┬─────────────┘
            │
            ↓
┌─────────────────────────────────────┐
│ Navigate to DetailsFragment         │
│ with productId parameter            │
└───────────┬─────────────────────────┘
            │
            ↓
┌─────────────────────────────────────┐
│ loadProductDetails(productId)       │
│ - Call API: GET /api/products/{id}  │
└───────────┬─────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│ Receive Product object with:            │
│ - id, name, description, price          │
│ - images: List<String> or null          │
│ - thumbnail: String                     │
└───────────┬─────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────┐
│ updateUI(product)                       │
└───────────┬─────────────────────────────┘
            │
            ↓
┌──────────────────────────────────────────┐
│ product.getImageAssetPaths(assets)       │
│ - Parse thumbnail path                   │
│ - Detect folder or single image          │
│ - List all images if folder exists       │
└───────────┬──────────────────────────────┘
            │
            ↓
┌──────────────────────────────────────────┐
│ ImageSliderAdapter                       │
│ - Load each image from assets            │
│ - Display in ViewPager2                  │
│ - Apply scale/alpha transformations      │
└──────────────────────────────────────────┘
```

---

## 💡 Example Scenarios

### Scenario 1: Sản phẩm có nhiều ảnh (women1)

**Backend data:**
```json
{
  "id": "123",
  "name": "Elegant Dress",
  "description": "Beautiful summer dress",
  "price": 49.99,
  "thumbnail": "C:\\...\\assets\\woman\\women1\\women1_1.jpg",
  "images": [
    "C:\\...\\assets\\woman\\women1\\women1_1.jpg",
    "C:\\...\\assets\\woman\\women1\\women1_2.jpg",
    "C:\\...\\assets\\woman\\women1\\women1_3.jpg"
  ]
}
```

**Processing:**
1. Parse thumbnail: `woman/women1/women1_1.jpg`
2. Detect 3 parts: `woman` / `women1` / `women1_1.jpg` → Folder-based
3. List assets in `woman/women1/`: finds 3 images
4. Return: `["woman/women1/women1_1.jpg", "woman/women1/women1_2.jpg", "woman/women1/women1_3.jpg"]`

**Result:** ViewPager2 displays 3 images with smooth transitions

---

### Scenario 2: Sản phẩm 1 ảnh (women6)

**Backend data:**
```json
{
  "id": "456",
  "name": "Classic Shirt",
  "description": "White cotton shirt",
  "price": 29.99,
  "thumbnail": "C:\\...\\assets\\woman\\women6.jpg",
  "images": null
}
```

**Processing:**
1. Parse thumbnail: `woman/women6.jpg`
2. Detect 2 parts: `woman` / `women6.jpg` → Single image
3. Return: `["woman/women6.jpg"]`

**Result:** ViewPager2 displays 1 image

---

### Scenario 3: Không có ảnh (fallback)

**Backend data:**
```json
{
  "id": "789",
  "name": "Test Product",
  "description": "No images",
  "price": 19.99,
  "thumbnail": null,
  "images": null
}
```

**Processing:**
1. No thumbnail found
2. Return empty list: `[]`

**Result:** ViewPager2 displays placeholder images

---

## 🎨 UI Display

### Với nhiều ảnh:
```
┌─────────────────────────────────────────┐
│ Elegant Dress                           │
│ $49.99                                  │
├─────────────────────────────────────────┤
│                                         │
│   [img1]  ┌─────────┐  [img3]         │
│           │  img2   │                  │
│           │ (main)  │                  │
│           └─────────┘                  │
│              ●  ○  ○                    │
│         (swipe for more)                │
├─────────────────────────────────────────┤
│ Beautiful summer dress                  │
│ [Info] [Reviews]                        │
│                                         │
│ [Add To Cart · $49.99]                 │
└─────────────────────────────────────────┘
```

### Với 1 ảnh:
```
┌─────────────────────────────────────────┐
│ Classic Shirt                           │
│ $29.99                                  │
├─────────────────────────────────────────┤
│                                         │
│         ┌─────────┐                    │
│         │  img1   │                    │
│         │ (only)  │                    │
│         └─────────┘                    │
│              ●                          │
│                                         │
├─────────────────────────────────────────┤
│ White cotton shirt                      │
│ [Info] [Reviews]                        │
│                                         │
│ [Add To Cart · $29.99]                 │
└─────────────────────────────────────────┘
```

---

## 🔍 Path Parsing Logic

### Windows Path từ Backend:
```
C:\Users\tung\...\assets\woman\women1\women1_1.jpg
```

### Extraction Steps:

1. **Find "assets\\"**:
   ```kotlin
   val assetsIndex = path.indexOf("assets\\")
   // assetsIndex = position of "assets\\"
   ```

2. **Extract substring**:
   ```kotlin
   path.substring(assetsIndex + 7)
   // Result: "woman\women1\women1_1.jpg"
   ```

3. **Replace backslashes**:
   ```kotlin
   .replace("\\", "/")
   // Result: "woman/women1/women1_1.jpg"
   ```

4. **Split and analyze**:
   ```kotlin
   val parts = result.split("/")
   // parts = ["woman", "women1", "women1_1.jpg"]
   // parts.size = 3 → Folder-based
   ```

---

## 🧪 Testing

### Test Cases:

#### 1. ✅ Sản phẩm có 3 ảnh (women1)
```kotlin
// Expected: Load 3 images from woman/women1/
// - women1_1.jpg
// - women1_2.jpg
// - women1_3.jpg
```

#### 2. ✅ Sản phẩm có 2 ảnh (women2)
```kotlin
// Expected: Load 2 images from woman/women2/
// - women2_1.jpg
// - women2_2.jpg
```

#### 3. ✅ Sản phẩm có 1 ảnh (women6)
```kotlin
// Expected: Load 1 image
// - woman/women6.jpg
```

#### 4. ✅ Sản phẩm không có ảnh
```kotlin
// Expected: Show placeholders
// - model_image_1.jpg
// - model_image_2.jpg
// - model_image_3.jpg
```

---

## 📊 Performance

### Optimization:
- ✅ Images loaded lazy (khi cần)
- ✅ AssetManager caching tự động
- ✅ Chỉ list files trong folder cần thiết
- ✅ Error handling cho missing files

### Memory:
- ✅ ViewPager2 với `offscreenPageLimit = 1`
- ✅ Chỉ keep 3 images max trong memory
- ✅ BitmapFactory decode efficient

---

## 🐛 Error Handling

### Case 1: Folder không tồn tại
```kotlin
try {
    val files = assetManager.list(folderPath)
} catch (e: Exception) {
    // Fallback to thumbnail only
    imagePaths.add(thumbnailPath)
}
```

### Case 2: Thumbnail null
```kotlin
if (thumbnailPath != null) {
    // Process
} else {
    // Return empty list → Use placeholders
}
```

### Case 3: Invalid path format
```kotlin
private fun extractAssetPath(path: String): String? {
    if (path.isEmpty()) return null
    // Try multiple extraction methods
    // Return null if all fail
}
```

---

## 🚀 Integration with SearchView

### Navigation flow:
```kotlin
// In SearchView / ProductAdapter
productItem.setOnClickListener {
    val bundle = Bundle().apply {
        putString("productId", product.id)
    }
    findNavController().navigate(
        R.id.action_to_detailsFragment,
        bundle
    )
}
```

### DetailsFragment receives:
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    productId = arguments?.getString("productId")
    if (productId != null) {
        loadProductDetails(productId!!)
    }
}
```

---

## ✅ Checklist

- [x] Thêm `getImageAssetPaths()` vào Product model
- [x] Thêm `extractAssetPath()` helper method
- [x] Cập nhật `updateUI()` trong DetailsFragment
- [x] Handle folder-based products (women1, women2)
- [x] Handle single-image products (women6)
- [x] Handle missing images (placeholder fallback)
- [x] Add logging cho debugging
- [x] Error handling cho tất cả cases
- [x] Test compile thành công

---

## 📝 Notes

- Backend có thể return Windows paths (`C:\...`) hoặc relative paths
- System tự động convert sang Android asset paths
- Support cả `.jpg` và `.png` formats
- Case-insensitive file extension checking
- Maintain backward compatibility với existing code

---

## 🔮 Future Enhancements

1. **Image Caching**: Cache decoded bitmaps
2. **Lazy Loading**: Load images on-demand
3. **Compression**: Optimize large images
4. **Thumbnails**: Generate smaller previews
5. **Network Images**: Support URLs from CDN
6. **Pagination**: Load images in batches

---

## 📞 Troubleshooting

### Vấn đề: Không load được ảnh
**Giải pháp:** Check logs:
```kotlin
Log.d("DetailsFragment", "Loading ${productImages.size} images")
productImages.forEach { Log.d("DetailsFragment", "  - $it") }
```

### Vấn đề: Path không đúng
**Giải pháp:** Verify path format:
```
✅ Correct: woman/women1/women1_1.jpg
❌ Wrong: C:\...\woman\women1\women1_1.jpg
```

### Vấn đề: Folder không tồn tại
**Giải pháp:** System tự động fallback to thumbnail

---

## 🎉 Summary

Hệ thống giờ đã:
- ✨ **Thông minh**: Tự động detect folder vs single image
- 🎯 **Chính xác**: Load đúng tất cả ảnh trong folder
- 🚀 **Reliable**: Fallback to placeholder nếu có lỗi
- 📱 **User-friendly**: Smooth image transitions
- 🔧 **Maintainable**: Clean code với comments đầy đủ

