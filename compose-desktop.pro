# SQLite JDBC
-keep class org.sqlite.** { *; }
-dontwarn org.sqlite.**
-dontwarn org.osgi.framework.**
-dontwarn org.ibex.nestedvm.**

# SLF4J
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**

# Kotlinx Serialization
-dontwarn kotlinx.serialization.**
-keepattributes *Annotation*, InnerClasses

# Coil
-keep class coil3.** { *; }
-keep class okio.** { *; }
-dontwarn coil3.**
-dontwarn okio.**
-dontwarn android.**
-dontwarn androidx.lifecycle.**
-dontwarn androidx.arch.core.**

# Coroutines (often needed for Coil/Compose)
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# General
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn org.jetbrains.annotations.**

# Keep enum classes and their methods from being stripped
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public enum model.QuestionType {
    *;
}
