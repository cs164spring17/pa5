package atrai.core;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static atrai.core.TransformationStep.ModifierType.*;

/**
 * TransformationStep represents a single transformer in the chain of responsibility.
 * It consists of a pattern, a lexer, and at least one of a modifier and a replacementTemplate.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
class TransformationStep {
    final Pattern pattern;
    Template replacementTemplate;
    Object modifier;

    enum ModifierType {PURE, NORETURN, DIRECT, PURECONTEXT, NORETURNCONTEXT, DIRECTCONTEXT, NONE}

    ;
    ModifierType modifierType;

    /**
     * Create a pure transformer. Both {@code pureModifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern             The tree pattern to match
     * @param pureModifier        (optional) a modifier for the matched tree
     * @param replacementTemplate (optional) a replacement pattern
     * @param patternLexer        The lexer for {@code pattern}
     * @param templateLexer       The lexer for {@code replacementTemplate}
     */
    TransformationStep(String pattern, Function<Object[], Object[]> pureModifier, String replacementTemplate, Lexer patternLexer, Lexer templateLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        if (pureModifier != null) {
            this.modifier = pureModifier;
            modifierType = PURE;
        } else {
            modifierType = NONE;
            this.modifier = null;
        }

        if (replacementTemplate != null)
            this.replacementTemplate = Template.parse(replacementTemplate, templateLexer);
    }

    /**
     * Create an impure transformer. Both {@code modifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern             The tree pattern to match
     * @param modifier            (optional) a modifier for the matched tree
     * @param replacementTemplate (optional) a replacement pattern
     * @param patternLexer        The lexer for {@code pattern}
     * @param templateLexer       The lexer for {@code replacementTemplate}
     */
    TransformationStep(String pattern, Consumer<Object[]> modifier, String replacementTemplate, Lexer patternLexer, Lexer templateLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        if (modifier != null) {
            this.modifier = modifier;
            modifierType = NORETURN;
        } else {
            modifierType = NONE;
            this.modifier = null;
        }
        if (replacementTemplate != null)
            this.replacementTemplate = Template.parse(replacementTemplate, templateLexer);
    }

    /**
     * Create an direct transformer. Both {@code modifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern        The tree pattern to match
     * @param directModifier a modifier for the matched tree
     * @param patternLexer        The lexer for {@code pattern}
     */
    TransformationStep(String pattern, Function<Object[], Object> directModifier, Lexer patternLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        if (directModifier != null) {
            this.modifier = directModifier;
            modifierType = DIRECT;
        } else {
            modifierType = NONE;
            this.modifier = null;
        }

    }

    /**
     * Create a pure transformer. Both {@code pureModifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern             The tree pattern to match
     * @param pureModifier        (optional) a modifier for the matched tree
     * @param replacementTemplate (optional) a replacement pattern
     * @param patternLexer        The lexer for {@code pattern}
     * @param templateLexer       The lexer for {@code replacementTemplate}
     */
    TransformationStep(String pattern, BiFunction<Object[], Object, Object[]> pureModifier, String replacementTemplate, Lexer patternLexer, Lexer templateLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        this.modifier = pureModifier;
        modifierType = PURECONTEXT;
        if (replacementTemplate != null)
            this.replacementTemplate = Template.parse(replacementTemplate, templateLexer);
    }

    /**
     * Create an impure transformer. Both {@code modifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern             The tree pattern to match
     * @param modifier            (optional) a modifier for the matched tree
     * @param replacementTemplate (optional) a replacement pattern
     * @param patternLexer        The lexer for {@code pattern}
     * @param templateLexer       The lexer for {@code replacementTemplate}
     */
    TransformationStep(String pattern, BiConsumer<Object[], Object> modifier, String replacementTemplate, Lexer patternLexer, Lexer templateLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        this.modifier = modifier;
        modifierType = NORETURNCONTEXT;
        if (replacementTemplate != null)
            this.replacementTemplate = Template.parse(replacementTemplate, templateLexer);
    }

    /**
     * Create an direct transformer. Both {@code modifier} and {@code replacementTemplate} cannot be null simultaneously.
     *
     * @param pattern        The tree pattern to match
     * @param directModifier a modifier for the matched tree
     * @param patternLexer        The lexer for {@code pattern}
     */
    TransformationStep(String pattern, BiFunction<Object[], Object, Object> directModifier, Lexer patternLexer) {
        this.pattern = Pattern.parse(pattern, patternLexer);
        this.modifier = directModifier;
        modifierType = DIRECTCONTEXT;
    }

    Function<Object[], Object[]> getPureModifier() {
        return (Function<Object[], Object[]>) modifier;
    }

    BiFunction<Object[], Object, Object[]> getPureModifierWithContext() {
        return (BiFunction<Object[], Object, Object[]>) modifier;
    }

    Consumer<Object[]> getModifier() {
        return (Consumer<Object[]>) modifier;
    }

    BiConsumer<Object[], Object> getModifierWithContext() {
        return (BiConsumer<Object[], Object>) modifier;
    }

    Function<Object[], Object> getDirectModifier() {
        return (Function<Object[], Object>) modifier;
    }

    BiFunction<Object[], Object, Object> getDirectModifierWithContext() {
        return (BiFunction<Object[], Object, Object>) modifier;
    }

}
