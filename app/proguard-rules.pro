# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in an Android SDK release build.preserve.cfg file.
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Basic ProGuard rules

# Kotlin specific rules
-dontwarn kotlin.**
-keepclassmembernames class kotlinx.coroutines.flow.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.coroutines.internal.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.coroutines.selects.** {
    volatile <fields>;
}
-keep class kotlinx.coroutines.** { *; }
-keepnames class kotlin.coroutines.Continuation

# OkHttp & Okio
-dontwarn okio.**
-dontwarn okhttp3.**
-keepnames class com.android.okhttp.HttpHandler {*;} # R8 includes this from AGP 4.2.0.
# -keep class org.conscrypt.** { *; } # Uncomment if you use Conscrypt for TLS

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions, Signature, InnerClasses
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature, InnerClasses
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }

# Keep Model classes (DTOs & Domain Models)
-keep class com.shenawynkov.movieapp.data.remote.dto.** { *; }
-keep class com.shenawynkov.movieapp.domain.model.** { *; }

# Gson specific: @SerializedName for enums and classes
-keepclassmembers enum * {
    @com.google.gson.annotations.SerializedName <fields>;
    public static final **[] $VALUES;
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson specific: TypeAdapterFactory, JsonSerializer, JsonDeserializer
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Hilt / Dagger rules (Uncomment and configure if needed)
# -keepattributes *Annotation*
# -keep class dagger.hilt.internal.aggregatedroot.codegen.** { *; }
# -keep class com.shenawynkov.movieapp.Hilt_** { *; }
# -keep @dagger.hilt.InstallIn @interface *
# -keep @dagger.hilt.components.SingletonComponent interface *
# -keep @dagger.hilt.DefineComponent @interface *
# -keep @dagger.hilt.EntryPoint @interface *
# -keep @dagger.hilt.GeneratedEntryPoint @interface *
# -keep @dagger.Module @interface *
# -keep @dagger.Provides @interface *
# -keep @javax.inject.Inject class *
# -keep @javax.inject.Singleton class *
# -keep class * { @javax.inject.Inject *; }
# -keep class * { @javax.inject.Singleton *; }
# -keepclassmembers class * { @dagger.hilt.android.lifecycle.HiltViewModel *; }
# -keepclassmembers @dagger.hilt.android.lifecycle.HiltViewModel class * { <init>(...); }

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile