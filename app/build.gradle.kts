import java.util.Properties
import java.io.FileInputStream
import java.io.File

// Las propiedades se leerán directamente
val envProperties = Properties()
val envFile = project.rootDir.resolve(".env.local")
if (envFile.exists()) {
    envProperties.load(FileInputStream(envFile))
}

// Nota: La generación del google-services.json ahora se realiza en settings.gradle.kts
// para asegurar que el archivo exista antes de que los plugins sean evaluados.

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.adobe.marketing.mobile.messagingsample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adobe.marketing.mobile.messagingsample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // --- INYECCIÓN DE SECRETOS ---
        val adobeAppId = envProperties.getProperty("ADOBE_APP_ID", "").trim().removeSurrounding("\"")
        val adobeAssuranceSessionId = envProperties.getProperty("ADOBE_ASSURANCE_SESSION_ID", "").trim().removeSurrounding("\"")
        
        buildConfigField("String", "ADOBE_APP_ID", "\"$adobeAppId\"")
        buildConfigField("String", "ADOBE_ASSURANCE_SESSION_ID", "\"$adobeAssuranceSessionId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(platform("com.adobe.marketing.mobile:sdk-bom:3.+"))

    implementation("com.adobe.marketing.mobile:core")
    implementation("com.adobe.marketing.mobile:edge")
    implementation("com.adobe.marketing.mobile:edgeidentity")
    implementation("com.adobe.marketing.mobile:lifecycle")
    implementation("com.adobe.marketing.mobile:messaging")
    implementation("com.adobe.marketing.mobile:assurance")
    implementation("com.adobe.marketing.mobile:places")
    implementation("com.adobe.marketing.mobile:audience")

    implementation("com.google.android.gms:play-services-location:21.2.0")

    implementation("com.google.firebase:firebase-messaging:23.4.1")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
   // implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-location:21.4.0")

}
