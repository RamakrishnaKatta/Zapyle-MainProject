 -keepclassmembers class com.dom925.xxxx {
   public *;
}

-dontwarn rx.**
-dontwarn com.android.**
-dontwarn com.freshdesk.hotline.**
-dontwarn uk.co.chrishenx.calligraphy.**
-dontwarn com.clevertap.android.sdk.**
   -keep class com.squareup.okhttp.** { *; }
   -keep interface com.squareup.okhttp.** { *; }
   -dontwarn com.squareup.okhttp.**
   -dontwarn okio.**
   -dontwarn retrofit.**
   -keep class retrofit.** { *; }
   -keepattributes Signature
   -keepattributes Exceptions
   -keep class com.google.gson.** { *; }
   -keep class com.google.inject.** { *; }
   -keep class com.android.support.v7.** { *; }
   -dontwarn com.android.support.v7.**
   -keep class org.apache.http.** { *; }
   -keep class org.apache.james.mime4j.** { *; }
   -keep class javax.inject.** { *; }

   -keepattributes *Annotation*
   -keep class in.juspay.** {*;}
   -dontwarn in.juspay.**


   -keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.DoNotStrip
   -keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.KeepGettersAndSetters

   # Do not strip any method/class that is annotated with @DoNotStrip
   -keep @com.facebook.crypto.proguard.annotations.DoNotStrip class *
   -keepclassmembers class * {
       @com.facebook.crypto.proguard.annotations.DoNotStrip *;
   }
   -keepclassmembers @com.facebook.crypto.proguard.annotations.KeepGettersAndSetters class * {
     void set*(***);
     *** get*();
   }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
