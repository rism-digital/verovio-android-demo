
# Set the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.22.1)

# Set the path to the Verovio submodule
set(VEROVIO_SOURCE_DIR ${PROJECT_ROOT}/external/verovio)

# Build Verovio as a library
set(BUILD_AS_ANDROID_LIBRARY ON)
# Add the subdirectory with the Verovio CMakeLists.txt and a binary dir
add_subdirectory(${VEROVIO_SOURCE_DIR}/cmake ${CMAKE_CURRENT_BINARY_DIR}/verovio_build)
# Add the include paths with the Verovio files (for the toolkit)
file(GLOB VRV_INCLUDES "${VEROVIO_SOURCE_DIR}/include/*" "${VEROVIO_SOURCE_DIR}/libmei/*")
include_directories(${VRV_INCLUDES})

# Declare and name the project.
project("verovio-android")

# Creates a library with the C++ jni code
add_library(verovio-android SHARED verovio-jni-exports.cpp)

# Add the log library
find_library(log-lib log)

# Specify libraries CMake should link
target_link_libraries(verovio-android verovio ${log-lib})