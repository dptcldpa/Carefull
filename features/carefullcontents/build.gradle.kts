plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.cases.carefull.features.carefullcontents"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
}
kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":features:carefullcommon"))

    implementation(libs.bundles.android.core)

    implementation(libs.guava)

    // Permissions
    implementation(libs.google.accompanist.permissions)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.libraries)

    implementation(libs.bundles.maps.sdk)

    implementation(libs.bundles.coil)

    implementation(libs.bundles.androidx.ktx)

    // 로그 확인용
    implementation(libs.logging.interceptor)

    implementation(libs.bundles.camerax)

    implementation(libs.bundles.pose.detection)

    // 지도 위치 정보
    implementation("com.google.android.gms:play-services-location:21.0.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    ksp(libs.hilt.compiler)
    
    implementation(libs.bundles.hilt.runtime)
    
    implementation(libs.androidx.runtime.livedata)
    
}