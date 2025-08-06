# Verovio Android Demo

An Android demo application showcasing the integration of the [Verovio](https://www.verovio.org) Toolkit for rendering MEI (Music Encoding Initiative) music notation files as scalable SVG graphics.

Run the App from [Google Play](https://play.google.com/store/apps/details?id=org.verovio.android.demo)

<img width="540" height="1080" alt="image" src="https://github.com/user-attachments/assets/e38607a1-68ae-4bce-b8a7-05fcaffd8024" />

## Overview

This project demonstrates how to embed the Verovio music notation rendering library in an Android app using Kotlin and Jetpack Compose. It provides basic controls to load MEI files, navigate pages, zoom in/out, and display music notation using a WebView.

## Features

* Load MEI music files from device storage
* Render MEI to SVG in a WebView
* Page navigation controls (Previous/Next)
* Zoom in and out
* Change the music font

## Getting Started

### Prerequisites

* Android Studio Arctic Fox or later (tested on Android Studio Narwhal 2025.1.1)
* Android device or emulator running API level 21+
* CMake 3.22+ and NDK installed for native build

### Building the App

Clone the repository:

    git clone https://github.com/rism-digital/verovio-android-demo.git
    cd verovio-android-demo


Then
* Open the project in Android Studio.
* Build and run the app on your device or emulator.

## Verovio integration

This app uses the Verovio toolkit compiled as a native library (libverovio-android.so) via CMake and integrated with the Kotlin app through JNI. The integration involves several components:

1. Verovio as a Git Submodule

The Verovio source code is included as a git submodule within the project’s `external/verovio` directory. This allows you to keep the Verovio code separate and update it independently from the demo app.

2. SWIG Bindings

The project uses SWIG (Simplified Wrapper and Interface Generator) to generate JNI bindings for the Verovio C++ library, allowing Kotlin/Java code to call native Verovio functions seamlessly.

The SWIG interface file (`verovio.i`) describes the Verovio API to be exposed. SWIG generates the JNI wrapper code (`verovio_wrap.cxx`) which is compiled into the native library. The Kotlin `build.gradle.kts` script triggers SWIG during the build process and compiles the generated sources.

3. CMake Build Integration

Native compilation is managed by CMake, which is configured to:

* Include the Verovio source tree under external/verovio
* Build Verovio library
* Compile SWIG-generated JNI wrapper files
* Link Verovio and JNI wrapper into a single Android library

## License

This project is licensed under the MIT license — see the LICENSE file for details.
