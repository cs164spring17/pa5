package atrai.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import static atrai.core.TransformationStep.ModifierType.NONE;

/**
 * Executes a chain of transformers, including their matchers and replacers, on
 * a tree. The result of each transformer is passed to the next, allowing cascades
 * of transformations.
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
final class MatchAndReplace {
    private final static boolean DEBUG1 = Boolean.getBoolean("debug1");
    private final static boolean DEBUG2 = Boolean.getBoolean("debug2");

    /**
     * Runs the chain of {@code transformers} on a tree rooted at {@code treeNode}
     *
     * @param transformers Chain of transformers.
     * @param treeNode     Root of a tree
     * @return Root of resulting tree
     */
    static Object matchAndReplace(
            ObjectArrayList<TransformationStep> transformers,
            Object treeNode, Object context) {
        for (TransformationStep t : transformers) {
            Object[] captures = t.pattern.match(treeNode);
            Object[] captures2 = captures;
            if (captures != null || DEBUG2) {
                if (DEBUG1) {
                    System.out.println("Before transformation: " + treeNode);
                    System.out.println("Pattern: " + t.pattern);
                    System.out.println("Captures after matching:");
                    if (captures != null) {
                        int i = 0;
                        for (Object capture : captures) {
                            System.out.printf("captures[%d] = %s%n", i, capture);
                            i++;
                        }
                    } else {
                        System.out.println("Pattern match failed!");
                    }
                }
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.NORETURN) {
                t.getModifier().accept(captures);
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.PURE) {
                captures2 = t.getPureModifier().apply(captures);
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.DIRECT) {
                treeNode = t.getDirectModifier().apply(captures);
                if (DEBUG1) {
                    System.out.println("After transformation: " + treeNode);
                }
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.NORETURNCONTEXT) {
                t.getModifierWithContext().accept(captures, context);
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.PURECONTEXT) {
                captures2 = t.getPureModifierWithContext().apply(captures, context);
            }
            if (captures != null && t.modifierType == TransformationStep.ModifierType.DIRECTCONTEXT) {
                treeNode = t.getDirectModifierWithContext().apply(captures, context);
                if (DEBUG1) {
                    System.out.println("After transformation: " + treeNode);
                }
            }
            if (captures2 != null && t.modifierType != NONE && t.replacementTemplate != null) {
                if (DEBUG1) {
                    System.out.println("Captures after modification:");
                    int i = 0;
                    for (Object capture : captures2) {
                        System.out.printf("captures[%d] = %s%n", i, capture);
                        i++;
                    }
                }
            }
            if (captures2 != null && t.replacementTemplate != null) {
                treeNode = t.replacementTemplate.replace(captures2);
                if (DEBUG1) {
                    System.out.println("Template: " + t.replacementTemplate);
                    System.out.println("After transformation: " + treeNode);
                }
            }
        }
        return treeNode;
    }
}
