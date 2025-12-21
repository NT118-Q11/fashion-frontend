package com.example.fashionapp

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Properties

/**
 * Utility class to load environment variables from .env file
 */
object EnvironmentConfig {

    private var properties: Properties? = null

    /**
     * Initialize the environment config by loading .env file from multiple sources
     */
    fun init(context: Context) {
        if (properties == null) {
            properties = Properties()

            // Try loading from multiple sources in order of preference
            var loaded = false

            // 1. Try loading from external storage (project root .env)
            loaded = loadFromExternalStorage(context)

            // 2. If failed, try loading from raw resources folder
            if (!loaded) {
                loaded = loadFromRawResources(context)
            }

            // 3. If failed, try loading from assets folder
            if (!loaded) {
                loaded = loadFromAssets(context)
            }

            // 4. If all failed, use hardcoded fallback values
            if (!loaded) {
                loadFallbackValues()
            }
        }
    }

    /**
     * Try to load .env from external storage (project root)
     */
    private fun loadFromExternalStorage(context: Context): Boolean {
        return try {
            println("EnvironmentConfig: Trying to load from external storage...")

            // Try different possible locations for .env file
            val possiblePaths = mutableListOf<String>()

            // Add system property based paths
            System.getProperty("user.dir")?.let { userDir ->
                possiblePaths.add("$userDir/.env")
                possiblePaths.add("$userDir/../.env") // Parent directory
            }

            // Add environment variable based paths
            System.getenv("PWD")?.let { pwd ->
                possiblePaths.add("$pwd/.env")
            }

            // Add Android app paths
            possiblePaths.addAll(listOf(
                "/android_asset/.env",
                "${context.filesDir.parentFile?.parentFile?.parentFile?.parent}/.env",
                "${context.getExternalFilesDir(null)?.parentFile?.parentFile?.parentFile?.parentFile?.parent}/.env",
                "${context.applicationInfo.sourceDir.substringBeforeLast("/app/")}/.env"
            ))

            // Also try current working directory variations
            possiblePaths.addAll(listOf(
                ".env",
                "../.env",
                "../../.env",
                "/sdcard/.env",
                "${context.getExternalFilesDir(null)}/.env"
            ))

            for (path in possiblePaths) {
                println("EnvironmentConfig: Trying path: $path")
                try {
                    val file = java.io.File(path)
                    if (file.exists() && file.canRead()) {
                        println("EnvironmentConfig: Found readable .env file at: $path")
                        loadFromFile(file)
                        return true
                    } else {
                        println("EnvironmentConfig: File at $path - exists: ${file.exists()}, canRead: ${file.canRead()}")
                    }
                } catch (e: Exception) {
                    println("EnvironmentConfig: Failed to check/read $path: ${e.message}")
                }
            }

            println("EnvironmentConfig: No readable .env file found in any location")
            false
        } catch (e: Exception) {
            println("EnvironmentConfig: External storage loading failed: ${e.message}")
            false
        }
    }

    /**
     * Try to load .env from raw resources folder
     */
    private fun loadFromRawResources(context: Context): Boolean {
        return try {
            println("EnvironmentConfig: Trying to load from raw resources folder...")
            // Try to get resource id for 'env' file (without extension in raw folder)
            val resourceId = context.resources.getIdentifier("env", "raw", context.packageName)
            if (resourceId != 0) {
                val inputStream = context.resources.openRawResource(resourceId)
                val reader = BufferedReader(InputStreamReader(inputStream))

                loadFromReader(reader, "raw resources")
                inputStream.close()
                true
            } else {
                println("EnvironmentConfig: No 'env' file found in raw resources")
                false
            }
        } catch (e: Exception) {
            println("EnvironmentConfig: Raw resources loading failed: ${e.message}")
            false
        }
    }

    /**
     * Try to load .env from assets folder
     */
    private fun loadFromAssets(context: Context): Boolean {
        return try {
            println("EnvironmentConfig: Trying to load from assets folder...")
            val inputStream = context.assets.open(".env")
            val reader = BufferedReader(InputStreamReader(inputStream))

            loadFromReader(reader, "assets")
            inputStream.close()
            true
        } catch (e: Exception) {
            println("EnvironmentConfig: Assets loading failed: ${e.message}")
            false
        }
    }

    /**
     * Load .env content from a file
     */
    private fun loadFromFile(file: java.io.File) {
        val reader = BufferedReader(java.io.FileReader(file))
        loadFromReader(reader, file.absolutePath)
        reader.close()
    }

    /**
     * Parse .env content from a reader
     */
    private fun loadFromReader(reader: BufferedReader, source: String) {
        println("EnvironmentConfig: Loading .env from: $source")

        var lineNumber = 0
        reader.forEachLine { line ->
            lineNumber++
            val trimmedLine = line.trim()
            println("EnvironmentConfig: Line $lineNumber: '$trimmedLine'")

            // Skip empty lines and comments (# only, like backend)
            if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                val equalIndex = trimmedLine.indexOf('=')
                if (equalIndex <= 0) {
                    // Skip lines without proper key=value format
                    return@forEachLine
                }

                val key = trimmedLine.substring(0, equalIndex).trim()
                var value = if (equalIndex < trimmedLine.length - 1) {
                    trimmedLine.substring(equalIndex + 1).trim()
                } else {
                    ""
                }

                // Remove surrounding quotes if present (following backend pattern)
                if ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length - 1)
                }

                properties?.setProperty(key, value)
                println("EnvironmentConfig: Set property '$key' = '$value'")
            } else {
                println("EnvironmentConfig: Skipping line: '$trimmedLine'")
            }
        }

        // Debug: Print all loaded properties
        println("EnvironmentConfig: Total properties loaded from $source: ${properties?.size}")
        properties?.forEach { key, value ->
            println("EnvironmentConfig: Final property '$key' = '$value'")
        }
    }

    /**
     * Load fallback values when all file loading methods fail
     */
    private fun loadFallbackValues() {
        println("EnvironmentConfig: All file loading methods failed!")
        println("EnvironmentConfig: Please ensure .env file exists in:")
        println("EnvironmentConfig: - Project root: .env")
        println("EnvironmentConfig: - Raw resources: app/src/main/res/raw/env")
        println("EnvironmentConfig: - Assets folder: app/src/main/assets/.env")

        properties = Properties()
        // Do not set hardcoded credentials for security
        // App will gracefully handle missing credentials
    }

    /**
     * Get environment variable value by key
     */
    fun get(key: String, defaultValue: String = ""): String {
        val value = properties?.getProperty(key) ?: defaultValue
        println("EnvironmentConfig: get('$key') = '$value' (default: '$defaultValue')")
        return value
    }



    /**
     * Get Google Client ID from environment
     */
    fun getGoogleClientId(): String {
        val envClientId = properties?.getProperty("GOOGLE_CLIENT_ID")
        if (!envClientId.isNullOrEmpty()) {
        Log.d("EnvironmentConfig", "getGoogleClientId() from .env = '$envClientId'")
        return envClientId
        }

        // No hardcoded fallback for security - throw meaningful error
        Log.e("EnvironmentConfig", "GOOGLE_CLIENT_ID not found in any .env source!")
        throw IllegalStateException(
            "GOOGLE_CLIENT_ID not found. Please ensure .env file exists with GOOGLE_CLIENT_ID value in:\n" +
            "- Project root: .env\n" +
            "- Raw resources: app/src/main/res/raw/env\n" +
            "- Assets folder: app/src/main/assets/.env"
        )
    }

    /**
     * Get Google Client Secret from environment
     */
    fun getGoogleClientSecret(): String {
        val envClientSecret = properties?.getProperty("GOOGLE_CLIENT_SECRET")
        if (!envClientSecret.isNullOrEmpty()) {
            Log.d("EnvironmentConfig", "getGoogleClientSecret() from .env = '$envClientSecret'")
            return envClientSecret
        }

        // No hardcoded fallback for security - throw meaningful error
        Log.e("EnvironmentConfig", "GOOGLE_CLIENT_SECRET not found in any .env source!")
        throw IllegalStateException(
            "GOOGLE_CLIENT_SECRET not found. Please ensure .env file exists with GOOGLE_CLIENT_SECRET value in:\n" +
            "- Project root: .env\n" +
            "- Raw resources: app/src/main/res/raw/env\n" +
            "- Assets folder: app/src/main/assets/.env"
        )
    }

    /**
     * Get Google Android Client ID from environment
     */
    fun getGoogleAndroidClientId(): String {
        val envAndroidClientId = properties?.getProperty("GOOGLE_ANDROID_CLIENT_ID")
        if (!envAndroidClientId.isNullOrEmpty()) {
            Log.d("EnvironmentConfig", "getGoogleAndroidClientId() from .env = '$envAndroidClientId'")
            return envAndroidClientId
        }

        // No hardcoded fallback for security - throw meaningful error
        Log.e("EnvironmentConfig", "GOOGLE_ANDROID_CLIENT_ID not found in any .env source!")
        throw IllegalStateException(
            "GOOGLE_ANDROID_CLIENT_ID not found. Please ensure .env file exists with GOOGLE_ANDROID_CLIENT_ID value in:\n" +
            "- Project root: .env\n" +
            "- Raw resources: app/src/main/res/raw/env\n" +
            "- Assets folder: app/src/main/assets/.env"
        )
    }

    /**
     * Force reinitialize the environment config (for retry scenarios)
     */
    fun forceReinit(context: Context) {
        println("EnvironmentConfig: Force reinitializing...")
        properties = null
        init(context)
    }

    /**
     * Check if environment is properly initialized
     */
    fun isInitialized(): Boolean {
        return properties != null
    }
}
