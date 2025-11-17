plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.secrets.gradle.plugin)
    
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.cases.carefull"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cases.carefull"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "secret.properties"
}

kotlin {
    jvmToolchain(21)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":features"))
    implementation(project(":features:carefullcommon"))
    implementation(project(":features:carefullcontents"))
    implementation(project(":features:carefullmainui"))

    implementation(libs.bundles.android.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.libraries)

    implementation(libs.bundles.navigation)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.libraries)

    // kakao
    implementation(libs.v2.user)

    implementation(libs.bundles.pose.detection)

    api(libs.androidx.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    api(libs.androidx.navigation.compose)
    
    ksp(libs.hilt.compiler)
    
    implementation(libs.bundles.hilt.runtime)
    
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
}