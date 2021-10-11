package paperkuts.junit.jupiter.api

import org.junit.jupiter.api.DisplayNameGenerator
import paperkuts.junit.jupiter.support.nameToSentence
import java.lang.reflect.Method

class SentenceDisplayNameGenerator(
    targets: TargetSelection,
) : DisplayNameGenerator.Standard() {

    constructor() : this(TargetSelection.ClassAndMethodName)

    enum class TargetSelection {
        ClassName,
        MethodName,
        ClassAndMethodName
    }

    private val targetingClassName: Boolean =
        targets == TargetSelection.ClassAndMethodName || targets == TargetSelection.ClassName

    private val targetingMethodName: Boolean =
        targets == TargetSelection.ClassAndMethodName || targets == TargetSelection.MethodName


    override fun generateDisplayNameForClass(testClass: Class<*>): String {
        return when {
            targetingClassName -> nameToSentence(testClass.simpleName)
            else -> super.generateDisplayNameForClass(testClass)
        }
    }

    override fun generateDisplayNameForNestedClass(nestedClass: Class<*>): String {
        return when {
            targetingClassName -> nameToSentence(nestedClass.simpleName)
            else -> super.generateDisplayNameForNestedClass(nestedClass)
        }
    }

    override fun generateDisplayNameForMethod(testClass: Class<*>, testMethod: Method): String {
        return when {
            targetingMethodName -> nameToSentence(testMethod.name)
            else -> super.generateDisplayNameForMethod(testClass, testMethod)
        }
    }

}


