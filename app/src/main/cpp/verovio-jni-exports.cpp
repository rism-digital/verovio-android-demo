#include <jni.h>
#include <string>
#include "toolkit.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_constructor(JNIEnv *env, jobject thiz, jstring resourcePath) {
    auto *toolkit = new vrv::Toolkit(false);
    const char *resourcePathCStr = env->GetStringUTFChars(resourcePath, nullptr);
    toolkit->SetResourcePath(resourcePathCStr);
    env->ReleaseStringUTFChars(resourcePath, resourcePathCStr);
    toolkit->SetOptions("{'svgViewBox': 'true'}");
    toolkit->SetOptions("{'scaleToPageSize': 'true'}");
    toolkit->SetOptions("{'adjustPageHeight': 'true'}");
    return reinterpret_cast<jlong>(toolkit);
}


JNIEXPORT void JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_destructor(JNIEnv *env, jobject thiz,
                                                               jlong ptr) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    delete toolkit;
}

JNIEXPORT jstring JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_GetVersion(JNIEnv *env, jobject thiz,
                                                               jlong ptr) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return env->NewStringUTF("null");
    std::string version = toolkit->GetVersion();
    return env->NewStringUTF(version.c_str());
}

JNIEXPORT jint JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_GetPageCount(JNIEnv *env, jobject thiz,
                                                               jlong ptr) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return 0;
    return toolkit->GetPageCount();
}

JNIEXPORT jboolean JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_LoadData(JNIEnv *env, jobject, jlong ptr,
                                                             jstring meiInput) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return JNI_FALSE;
    const char *meiCStr = env->GetStringUTFChars(meiInput, nullptr);
    bool res = toolkit->LoadData(meiCStr);
    env->ReleaseStringUTFChars(meiInput, meiCStr);
    return res;
}

JNIEXPORT jstring JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_RenderToSVG(JNIEnv *env, jobject, jlong ptr,
                                                             int page) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return env->NewStringUTF("null");
    std::string svg = toolkit->RenderToSVG(page);
    return env->NewStringUTF(svg.c_str());
}

JNIEXPORT void JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_RedoLayout(JNIEnv *env, jobject, jlong ptr) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return;
    toolkit->RedoLayout();
}

JNIEXPORT jboolean JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_SetOptions(JNIEnv *env, jobject, jlong ptr, jstring jsonOptions) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return JNI_FALSE;
    const char *jsonOptionsCStr = env->GetStringUTFChars(jsonOptions, nullptr);
    bool res = toolkit->SetOptions(jsonOptionsCStr);
    env->ReleaseStringUTFChars(jsonOptions, jsonOptionsCStr);
    return res;
}

}