
package com.example.fashionapp.data

object ProductRepository {
    fun getSampleProducts(): List<Product> = listOf(
        Product(
            id = 1,
            title = "Floral Summer Dress",
            description = "Lightweight floral midi dress, perfect for sunny days.",
            price = 49.99,
            assetImage = "women/women1.jpg"
        ),
        Product(
            id = 2,
            title = "Classic Denim Jacket",
            description = "Timeless denim jacket with comfortable fit.",
            price = 79.50,
            assetImage = "women/women2.jpg"
        ),
        Product(
            id = 3,
            title = "Casual White Sneakers",
            description = "Minimalist sneakers with cushioned sole.",
            price = 59.00,
            assetImage = "man/men1.jpg"
        ),
        Product(
            id = 4,
            title = "Leather Crossbody Bag",
            description = "Compact crossbody with adjustable strap.",
            price = 89.99,
            assetImage = "man/men2.jpg"
        ),
        Product(
            id = 5,
            title = "Striped Knit Sweater",
            description = "Cozy knit sweater with navy stripes.",
            price = 39.99,
            assetImage = "kids/kids1.jpg"
        ),
        Product(
            id = 6,
            title = "Wide-Leg Trousers",
            description = "High-waist wide-leg trousers for a modern look.",
            price = 69.99,
            assetImage = "kids/kids2.jpg"
        )
    )
}
