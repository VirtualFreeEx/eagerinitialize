package lol.ffi.eagerinitialize

/**
 * The Eager Initialize annotation.
 *
 * With this annotation present on an `object` class, the object will be included in a compiler-generated object,
 * which will forcefully initialize the classes that have this annotation upon this object being referenced.
 *
 * The package and name of the generated class is defined in the `ksp` block, and defaults to
 * `lol.ffi` for the package, 'EagerInitializeGenerated' for the classname.
 *
 * @param priority The priority of the annotation. Defaults to 0, higher priority - earlier initialization.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class EagerInitialize(val priority: Int = 0)