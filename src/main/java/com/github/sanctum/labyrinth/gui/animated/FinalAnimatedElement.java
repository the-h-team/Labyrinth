package com.github.sanctum.labyrinth.gui.animated;

/**
 * Encapsulates animated element in place, include slot.
 */
public final class FinalAnimatedElement {
    public final AnimatedElement animatedElement;
    public final int slot;

    public FinalAnimatedElement(AnimatedElement animatedElement, int slot) {
        this.animatedElement = animatedElement;
        this.slot = slot;
    }
}
