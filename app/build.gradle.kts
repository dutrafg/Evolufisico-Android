plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "br.com.evolufisico.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.evolufisico.android"
        minSdk = 29 // Android 10+
        targetSdk = 35
        versionCode = 130
        versionName = "25.0.0-alpha.25"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (!System.getenv("EF_KEYSTORE_FILE").isNullOrBlank()) {
            create("distribution") {
                storeFile = file(System.getenv("EF_KEYSTORE_FILE"))
                storePassword = System.getenv("EF_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("EF_KEY_ALIAS")
                keyPassword = System.getenv("EF_KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        debug {
            // APK de homologação paralelo: mantém intacto o aplicativo oficial.
            applicationIdSuffix = ".alpha25"
            versionNameSuffix = "-preview"
        }
        release {
            signingConfigs.findByName("distribution")?.let { signingConfig = it }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
    buildFeatures { compose = true }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.core:core:1.16.0")
    implementation("androidx.work:work-runtime-ktx:2.10.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
