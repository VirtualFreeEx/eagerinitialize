package lol.ffi.eagerinitialize

import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.jetbrains.annotations.Contract

/**
 * From a list of KS Class Declarations, creates a formatted exception message string.
 *
 * @throws IllegalArgumentException
 * @param invalids The list of invalid declarations.
 * @return The formatted exception message string.
 */
@Contract("_ -> new", pure = true)
private fun getMessage(invalids: List<KSClassDeclaration>): String {
    if (invalids.isEmpty()) throw IllegalArgumentException("EagerInitializeException thrown with an empty invalids list!")
    val builder = StringBuilder("The following files contain invalid @EagerInitialize annotations!")
    builder.appendLine()
    invalids.forEachIndexed { index, it ->
        builder.appendLine(
            "${it.containingFile?.fileName} on the ${it.qualifiedName} class" + if (index == invalids.size - 1) '.' else ';'
        )
    }
    return builder.toString()
}

/**
 * The Eager Initialize exception.
 *
 * This exception is throw if there is an invalid KSClassDeclaration supplied to the annotation processor.
 * I.E. A non-`object` class declaration annotated with [EagerInitialize] annotation.
 *
 * @param invalids The list of invalid declarations.
 * @throws IllegalArgumentException If the invalids list is empty.
 * @see EagerInitialize
 */
class EagerInitializeException(
    invalids: List<KSClassDeclaration>,
) : IllegalStateException(getMessage(invalids))