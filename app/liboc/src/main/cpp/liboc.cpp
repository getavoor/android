#include <jni.h>
#include <string>
#include <cstdio>
#include <iostream>

extern "C" JNIEXPORT jstring JNICALL
Java_app_avoor_liboc_ShuffleUtils_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

void swap(
    JNIEnv *env,
    jobjectArray numbers,
    jint idx_a,
    jint idx_b
) {
    // get the elements
    jobject el_a = env->GetObjectArrayElement(numbers, idx_a);
    jobject el_b = env->GetObjectArrayElement(numbers, idx_b);
    // swap them
    env->SetObjectArrayElement(numbers, idx_a, el_b);
    env->SetObjectArrayElement(numbers, idx_b, el_a);
}

extern "C"
JNIEXPORT void JNICALL
Java_app_avoor_liboc_ShuffleUtils_shuffle(
    JNIEnv *env,
    jobject thiz,
    jobjectArray numbers,
    jint numSize
) {
    if (numSize < 2) {
        std::cerr << "not enough numbers" << std::endl;
        return;
    }

    // go from last to first:
    for (int i = numSize - 1; i >= 0; i--) {
        // generate a random index and swap the i-th element there
        int j = rand() % (numSize);
        swap(env, numbers, i, j);
    }
}