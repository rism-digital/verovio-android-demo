
# Set the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.22.1)

# Set the path to the Verovio submodule
set(VEROVIO_SOURCE_DIR ${PROJECT_ROOT}/external/verovio)
set(SWIG_WRAP_FILE ${CMAKE_SOURCE_DIR}/verovio_wrap.cxx)

# Build Verovio as a library
set(BUILD_AS_ANDROID_LIBRARY ON)
# Add the subdirectory with the Verovio CMakeLists.txt and a binary dir
add_subdirectory(${VEROVIO_SOURCE_DIR}/cmake ${CMAKE_CURRENT_BINARY_DIR}/verovio_build)
# Add the include paths with the Verovio files (for the toolkit)
include_directories(
        ${VEROVIO_SOURCE_DIR}/include/vrv
        ${VEROVIO_SOURCE_DIR}/include/json
        ${VEROVIO_SOURCE_DIR}/include/pugi
        ${VEROVIO_SOURCE_DIR}/libmei/dist
        ${VEROVIO_SOURCE_DIR}/libmei/addons
        ${VEROVIO_SOURCE_DIR}/include/vrv
)

# Declare and name the project.
project("verovio-android")

# Creates a library with the C++ jni code
add_library(verovio-android SHARED ${SWIG_WRAP_FILE})

# Add the log library
find_library(log-lib log)

# Specify libraries CMake should link
target_link_libraries(verovio-android verovio ${log-lib})