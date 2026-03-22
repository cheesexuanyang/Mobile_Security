# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ============================================
# NATIVE METHODS — Critical, must not be renamed
# ============================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# Replace the specific StringHelper rule with this
-keep class com.example.inf2007_mad_j1847.utils.StringHelper { *; }

# ============================================
# MALWARE / TEST CLASSES — Keep all
# ============================================
-keep class com.example.inf2007_mad_j1847.test.** { *; }

# ============================================
# FIREBASE — Must not be obfuscated
# ============================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep class com.google.firebase.database.GenericTypeIndicator { *; }
# ============================================
# MODELS — Keep field names for Firestore serialization
# ============================================
-keep class com.example.inf2007_mad_j1847.model.** { *; }

# ============================================
# VIEWMODELS
# ============================================
-keep class com.example.inf2007_mad_j1847.viewmodel.** { *; }

# ============================================
# NOTIFICATIONS
# ============================================
-keep class com.example.inf2007_mad_j1847.notifications.** { *; }

# ============================================
# UTILS
# ============================================
-keep class com.example.inf2007_mad_j1847.utils.** { *; }

# ============================================
# CLIPBOARD / MALWARE
# ============================================
-keep class com.example.inf2007_mad_j1847.clipboard.** { *; }
-keep class com.example.inf2007_mad_j1847.malware.** { *; }

# ============================================
# ANDROIDX / COMPOSE — Keep for UI to work
# ============================================
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.activity.** { *; }
-dontwarn androidx.**

# ============================================
# CAMERA / ZXING
# ============================================
-keep class com.journeyapps.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.journeyapps.**
-dontwarn com.google.zxing.**

# ============================================
# COIL (image loading)
# ============================================
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================
# SUPPRESS COMMON WARNINGS
# ============================================
-dontwarn java.lang.invoke.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
