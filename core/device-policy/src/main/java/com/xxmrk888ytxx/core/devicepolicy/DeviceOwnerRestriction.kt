package com.xxmrk888ytxx.core.devicepolicy

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API was intended to be managed with Device Owner rights, but during the development process I decided to abandon it, can I return to it or delete it."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS
)
annotation class DeprecatedDeviceOwnerRestriction
