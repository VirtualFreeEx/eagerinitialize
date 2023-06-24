package lol.ffi.eagerinitialize.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import lol.ffi.eagerinitialize.EagerInitialize
import lol.ffi.eagerinitialize.EagerInitializeException

/**
 * # The Eager Initialize Kotlin Symbol Processor.
 *
 * ## Options
 * * `className` - The name of the output class. Defaults to: `EagerInitializeGenerated`
 * * `packageName` - The name of the output package. Defaults to: `lol.ffi`
 */
class EagerInitializeProcessor(private val codegen: CodeGenerator, options: Map<String, String>) :
    SymbolProcessor {
    private val className = options["className"] ?: "EagerInitializeGenerated"
    private val packageName = options["packageName"] ?: "lol.ffi"

    /**
     * Processes the annotations.
     * @throws EagerInitializeException If there is an invalid class declaration (I.E. the class kind is not object.)
     * @see EagerInitializeException
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Get the @EagerInitialize annotated `object` classes, discard the rest into a list of invalids.
        val (objects, invalids) = resolver.getSymbolsWithAnnotation(EagerInitialize::class.qualifiedName!!)
            .mapNotNull { it as? KSClassDeclaration }.partition { it.classKind == ClassKind.OBJECT }

        // For each of the invalid annotated classes, throw a build-time exception (to prevent the build from continuing).
        invalids.isNotEmpty() && throw EagerInitializeException(invalids)

        // Sort the object list (to initialize in the right order), after which we will
        // process the valid annotated objects into a list of Kotlin Poet statements,
        // and finally write it to the KSP Code Generator.
        FileSpec.builder(packageName, className)
            .addType(TypeSpec.objectBuilder(className)
                .addInitializerBlock(
                    objects.sortedBy {
                        it.annotations
                            .filter { annotation -> annotation.shortName.asString() == EagerInitialize::class.simpleName }
                            .first()
                            .arguments
                            .first()
                            .value as Int * -1
                    }.fold(CodeBlock.builder()) { acc, it ->
                        acc.addStatement("%L", it.qualifiedName)
                    }.build()
                ).build()
            ).build().writeTo(codegen, true)

        return listOf()
    }

}