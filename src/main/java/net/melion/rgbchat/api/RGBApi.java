/*
 * MIT License
 *
 * Copyright (c) 2021 Sanctum <https://github.com/the-h-team>
 * Copyright (c) 2020 F1b3r <https://github.com/F1b3r>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.melion.rgbchat.api;

import net.melion.rgbchat.chat.RGBUtils;

/**
 * RGBApi
 * <p>
 * Written by <a href="https://github.com/F1b3r">
 *     F1b3r &lt;https://github.com/F1b3r&gt;</a>
 * <p>
 * Converted from Kotlin to Java by <a href="https://github.com/ms5984">
 *     Matt &lt;https://github.com/ms5984&gt;</a>.
 *
 * @author F1b3r
 * @author ms5984
 */
public final class RGBApi {
    private RGBApi() {}

    /**
     * Convert a raw chat message to colored.
     *
     * @param rawChatMessage message to be colored.
     * @return colored message
     */
    public static String toColoredMessage(String rawChatMessage) {
        return RGBUtils.INSTANCE.toChatColorString(rawChatMessage);
    }
}
