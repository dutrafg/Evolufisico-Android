plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "br.com.evolufisico.nativo"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.evolufisico.nativo"
        minSdk = 29
        targetSdk = 36
        versionCode = 721
        versionName = "0.1.0-native-beta.17"
    }

    val releaseKeystorePath = providers.environmentVariable("EF_RELEASE_KEYSTORE_FILE").orNull
    val releaseStorePassword = providers.environmentVariable("EF_RELEASE_STORE_PASSWORD").orNull
    val releaseKeyAlias = providers.environmentVariable("EF_RELEASE_KEY_ALIAS").orNull
    val releaseKeyPassword = providers.environmentVariable("EF_RELEASE_KEY_PASSWORD").orNull
    val hasReleaseSigning = listOf(releaseKeystorePath, releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
        .all { !it.isNullOrBlank() }
    signingConfigs {
        if (hasReleaseSigning) {
            create("evoluRelease") {
                storeFile = file(requireNotNull(releaseKeystorePath))
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("evoluRelease")
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("androidx.work:work-runtime-ktx:2.10.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
