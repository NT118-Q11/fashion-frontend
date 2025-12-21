# Adding Image Loading - Quick Guide

## Current Status
✅ Product display works with placeholder images  
⚠️ Real product images from backend not loaded yet

## Solution: Add Glide Library

### Step 1: Add Dependency

Edit `app/build.gradle.kts`:

```kotlin
dependencies {
    // ...existing dependencies...
    
    // Image loading with Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}
```

### Step 2: Sync Gradle
```bash
./gradlew build
```

### Step 3: Update ProductAdapter

Replace the placeholder image loading in `ProductAdapter.kt`:

```kotlin
// BEFORE (line 33-34):
// TODO: Load image using Glide or Coil when implementing image loading
imgProduct.setImageResource(R.drawable.sample_woman)

// AFTER:
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

// In bind() method:
Glide.with(itemView.context)
    .load(product.images?.firstOrNull()) // Load first image from product
    .placeholder(R.drawable.sample_woman) // Show while loading
    .error(R.drawable.sample_woman) // Show if loading fails
    .centerCrop()
    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache for better performance
    .into(imgProduct)
```

### Complete Updated ProductAdapter.kt:

```kotlin
package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.fashionapp.R
import com.example.fashionapp.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        private val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)

        fun bind(product: Product) {
            txtTitle.text = product.brand ?: "Fashion Item"
            txtName.text = product.name
            txtPrice.text = "$${String.format("%.2f", product.price)}"
            
            // Load product image with Glide
            Glide.with(itemView.context)
                .load(product.images?.firstOrNull())
                .placeholder(R.drawable.sample_woman)
                .error(R.drawable.sample_woman)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProduct)
            
            // Handle favorite button click
            btnFavorite.setOnClickListener {
                // TODO: Implement favorite functionality
            }
            
            // Handle item click
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
```

## Alternative: Using Coil (Kotlin-first)

Coil is a modern, Kotlin-first image loading library.

### Step 1: Add Dependency
```kotlin
dependencies {
    implementation("io.coil-kt:coil:2.5.0")
}
```

### Step 2: Update ProductAdapter
```kotlin
import coil.load
import coil.transform.RoundedCornersTransformation

// In bind() method:
imgProduct.load(product.images?.firstOrNull()) {
    placeholder(R.drawable.sample_woman)
    error(R.drawable.sample_woman)
    crossfade(true)
    transformations(RoundedCornersTransformation(8f))
}
```

## Advanced Features

### 1. Circular Images
```kotlin
Glide.with(itemView.context)
    .load(product.images?.firstOrNull())
    .circleCrop() // Make image circular
    .into(imgProduct)
```

### 2. Rounded Corners
```kotlin
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

Glide.with(itemView.context)
    .load(product.images?.firstOrNull())
    .transform(RoundedCorners(16)) // 16dp rounded corners
    .into(imgProduct)
```

### 3. Blur Effect
```kotlin
import jp.wasabeef.glide.transformations.BlurTransformation

Glide.with(itemView.context)
    .load(product.images?.firstOrNull())
    .transform(BlurTransformation(25)) // Blur radius 25
    .into(imgProduct)
```

### 4. Loading Progress
```kotlin
Glide.with(itemView.context)
    .load(product.images?.firstOrNull())
    .placeholder(R.drawable.loading_animation) // Animated drawable
    .into(imgProduct)
```

### 5. Multiple Image Support
```kotlin
// If product has multiple images, show first one
// Add click listener to view all images
imgProduct.setOnClickListener {
    // Show gallery with all images
    showImageGallery(product.images ?: emptyList())
}
```

## Image URL Format

Backend should return full URLs in Product model:

```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Summer Dress",
  "images": [
    "https://yourdomain.com/images/products/dress1.jpg",
    "https://yourdomain.com/images/products/dress2.jpg"
  ]
}
```

Or relative paths that need base URL:

```kotlin
val baseImageUrl = "https://yourdomain.com/images/"
val fullImageUrl = baseImageUrl + product.images?.firstOrNull()

Glide.with(itemView.context)
    .load(fullImageUrl)
    .into(imgProduct)
```

## Network Security

If loading HTTP images (not HTTPS), update `network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">yourdomain.com</domain>
    </domain-config>
</network-security-config>
```

## Testing Image Loading

### Test with Sample URLs:
```kotlin
// Test in ProductAdapter temporarily
val testImageUrl = "https://picsum.photos/200/300"

Glide.with(itemView.context)
    .load(testImageUrl)
    .into(imgProduct)
```

## Performance Tips

### 1. Resize Images
```kotlin
Glide.with(itemView.context)
    .load(imageUrl)
    .override(400, 600) // Max size in pixels
    .into(imgProduct)
```

### 2. Cache Strategy
```kotlin
// Cache both original and transformed images
.diskCacheStrategy(DiskCacheStrategy.ALL)

// Only cache original
.diskCacheStrategy(DiskCacheStrategy.DATA)

// Only cache transformed
.diskCacheStrategy(DiskCacheStrategy.RESOURCE)

// No caching
.diskCacheStrategy(DiskCacheStrategy.NONE)
```

### 3. Memory Management
```kotlin
// In onCreate or onViewCreated
Glide.get(requireContext()).setMemoryCategory(MemoryCategory.HIGH)
```

## Troubleshooting

### Issue: Images not loading
**Solutions:**
1. Check internet permission (✅ already added)
2. Verify backend returns correct image URLs
3. Check network_security_config.xml for HTTP/HTTPS
4. Look for Glide errors in Logcat

### Issue: Images loading slowly
**Solutions:**
1. Use lower resolution images from backend
2. Implement server-side image optimization
3. Use Glide thumbnail feature:
   ```kotlin
   Glide.with(context)
       .load(fullImageUrl)
       .thumbnail(0.1f) // Load 10% quality first
       .into(imgProduct)
   ```

### Issue: OutOfMemoryError
**Solutions:**
1. Reduce image cache size
2. Use override() to limit image dimensions
3. Use RGB_565 format:
   ```kotlin
   .format(DecodeFormat.PREFER_RGB_565)
   ```

## Complete Example with All Features

```kotlin
fun bind(product: Product) {
    txtTitle.text = product.brand ?: "Fashion Item"
    txtName.text = product.name
    txtPrice.text = "$${String.format("%.2f", product.price)}"
    
    // Load product image with all features
    val imageUrl = product.images?.firstOrNull()
    
    Glide.with(itemView.context)
        .load(imageUrl)
        .placeholder(R.drawable.sample_woman) // Loading placeholder
        .error(R.drawable.sample_woman) // Error fallback
        .fallback(R.drawable.sample_woman) // Null URL fallback
        .centerCrop() // Crop to fill ImageView
        .override(400, 600) // Max dimensions
        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache everything
        .transform(RoundedCorners(12)) // Rounded corners
        .transition(DrawableTransitionOptions.withCrossFade()) // Smooth fade
        .into(imgProduct)
    
    // Handle favorite button
    btnFavorite.setOnClickListener {
        // TODO: Toggle favorite status
    }
    
    // Handle product click
    itemView.setOnClickListener {
        onItemClick(product)
    }
}
```

## Summary

✅ **Recommended:** Use Glide (most popular, mature)  
✅ **Alternative:** Use Coil (modern, Kotlin-first)  
✅ **Implementation:** Simple one-line change  
✅ **Performance:** Automatic caching and optimization  

**Estimated Time:** 5-10 minutes to implement  
**Difficulty:** Easy 🟢

---

**Next:** After adding Glide, your product images will load beautifully from the backend! 📸

