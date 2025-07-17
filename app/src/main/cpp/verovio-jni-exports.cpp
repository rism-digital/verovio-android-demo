#include <jni.h>
#include <string>
#include "toolkit.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_constructor(JNIEnv *env, jobject thiz) {
    auto *ptr = new vrv::Toolkit(false);
    return reinterpret_cast<jlong>(ptr);
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

JNIEXPORT jstring JNICALL
Java_org_verovio_android_demo_VerovioToolkitWrapper_LoadData(JNIEnv *env, jobject, jlong ptr,
                                                             jstring meiInput) {
    auto *toolkit = reinterpret_cast<vrv::Toolkit *>(ptr);
    if (toolkit == nullptr) return env->NewStringUTF("null");
    const char *meiCStr = env->GetStringUTFChars(meiInput, nullptr);
    std::string svg = toolkit->RenderData(meiCStr, "{}");
    env->ReleaseStringUTFChars(meiInput, meiCStr);
    return env->NewStringUTF(svg.c_str());
}

}