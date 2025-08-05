plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.verovio.android.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.verovio.android.demo"
        minSdk = 24
        targetSdk = 36
        versionCode = 7
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                arguments += listOf("-DPROJECT_ROOT=${rootDir.absolutePath}")
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

////////////////////////////////////////////
// Copy the verovio resource directory from the submodule to the project
tasks.register<Copy>("copyVerovioData") {
    from(rootDir.resolve("external/verovio/data"))
    into("src/main/assets/verovio/data")
}

tasks.named("preBuild") {
    dependsOn("copyVerovioData")
}

////////////////////////////////////////////
// Generate the java and cpp files using swig and write them into the project
val swigOutputJava = file("src/main/java/org/verovio/lib")
val swigOutputCpp = file("src/main/cpp/verovio_wrap.cxx")
val swigInterfaceFile = file("${rootDir.absolutePath}/external/verovio/bindings/java/verovio.i")

tasks.register<Exec>("generateSwigBindings") {
    group = "build"
    description = "Generate JNI bindings with SWIG"

    // Adjust working directory to the project root
    workingDir = rootProject.projectDir

    // Ensure output directories exist
    doFirst {
        swigOutputJava.mkdirs()
        swigOutputCpp.parentFile.mkdirs()
    }

    commandLine = listOf(
        "/opt/homebrew/bin/swig", // Change to "/opt/homebrew/bin/swig" or something else if needed
        "-java",
        "-c++",
        "-package", "org.verovio.lib",
        "-outdir", swigOutputJava.absolutePath,
        "-o", swigOutputCpp.absolutePath,
        swigInterfaceFile.absolutePath
    )
}

// Ensure SWIG runs before compilation
tasks.named("preBuild") {
    dependsOn("generateSwigBindings")
}

tasks.named("clean") {
    doFirst {
        delete("src/main/java/org/verovio/lib/*")
        delete("src/main/cpp/verovio_wrap.cxx")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

}