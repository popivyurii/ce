plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
}
kotlin {
    jvmToolchain(21)
}

android {
    namespace = "com.popivyurii.ce.main"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.kotlinx.coroutines.android)

    compileOnly(libs.androidx.compose.runtime)
    api(libs.kotlinx.immutable)

    api(project(":core:data"))

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}