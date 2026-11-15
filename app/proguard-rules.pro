# Keep metadata required by dependency injection, Kotlin reflection, and
# generated serializers while allowing R8 to optimize and obfuscate code.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,Signature,InnerClasses,EnclosingMethod

# Android creates these classes from the merged manifest or system callbacks.
-keep class com.xxmrk888ytxx.apexowner.ApexOwnerApp { *; }
-keep class com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity { *; }
-keep class com.xxmrk888ytxx.apexowner.view.oneshot.OneShotQuickLauncherActivity { *; }
-keep class com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity { *; }
-keep class com.xxmrk888ytxx.apexowner.bridge.PackageInstallerReceiver { *; }
-keep class com.xxmrk888ytxx.apexowner.bridge.ApexBridgeProvider { *; }
-keep class com.xxmrk888ytxx.apexowner.service.ApexPolicyService { *; }
-keep class com.xxmrk888ytxx.apexowner.service.InactivityAlarmReceiver { *; }
-keep class com.xxmrk888ytxx.apexowner.service.BootCompletedReceiver { *; }
-keep class com.xxmrk888ytxx.core.devicepolicy.ApexDeviceOwnerReceiver { *; }

# Keep Hilt-generated entry points and members used by generated injection code.
-keep class dagger.hilt.** { *; }
-keep class com.xxmrk888ytxx.apexowner.**_GeneratedInjector { *; }
-keep class com.xxmrk888ytxx.apexowner.**_HiltComponents* { *; }
