#include <jni.h>

#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_inf2007_1mad_1j1847_utils_StringHelper_getNativePart16(
        JNIEnv* env,
        jclass clazz) {

    jbyte raw[16] = {
            (jbyte)0x11, (jbyte)0x22, (jbyte)0x33, (jbyte)0x44,
            (jbyte)0x55, (jbyte)0x66, (jbyte)0x77, (jbyte)0x88,
            (jbyte)0x99, (jbyte)0xAA, (jbyte)0xBB, (jbyte)0xCC,
            (jbyte)0xDD, (jbyte)0xEE, (jbyte)0xF0, (jbyte)0x0F
    };

    // optional light transform
    for (int i = 0; i < 16; i++) {
        raw[i] = (jbyte)(raw[i] ^ (0x10 + i));
    }

    jbyteArray arr = env->NewByteArray(16);
    env->SetByteArrayRegion(arr, 0, 16, raw);
    return arr;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_inf2007_1mad_1j1847_utils_StringHelper_nativePing(
        JNIEnv* env,
        jclass clazz) {
    return env->NewStringUTF("native ok");
}