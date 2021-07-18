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
package net.melion.rgbchat.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RGBUtils
 * <p>
 * Written by <a href="https://github.com/F1b3r">
 * F1b3r &lt;https://github.com/F1b3r&gt;</a>
 * <p>
 * Converted from Kotlin to Java by <a href="https://github.com/ms5984">
 * Matt &lt;https://github.com/ms5984&gt;</a>.
 *
 * @author F1b3r
 * @author ms5984
 */
public final class RGBUtils {
    public static final RGBUtils INSTANCE = new RGBUtils();
    private final Pattern hex = Pattern.compile("#[0-9a-fA-F]{6}");
    private final Pattern fix2 = Pattern.compile("\\{#[0-9a-fA-F]{6}\\}");
    private final Pattern fix3 = Pattern.compile("\\&x[\\&0-9a-fA-F]{12}");
    private final Pattern gradient1 = Pattern.compile("<#[0-9a-fA-F]{6}>[^<]*</#[0-9a-fA-F]{6}>");
    private final Pattern gradient2 = Pattern.compile("\\{#[0-9a-fA-F]{6}>\\}[^\\{]*\\{#[0-9a-fA-F]{6}<\\}");

    private RGBUtils() {
    }

    private String toChatColor(String hexCode) {
        final StringBuilder magic = new StringBuilder("§x");
        for (char c : hexCode.substring(1).toCharArray()) {
            magic.append('§').append(c);
        }
        return magic.toString();
    }

    private String toHexString(int red, int green, int blue) {
        final StringBuilder sb = new StringBuilder(Integer.toHexString((red << 16) + (green << 8) + blue));
        while (sb.length() < 6) sb.insert(0, "0");
        return sb.toString();
    }

    private String applyFormats(String textInput) {
        textInput = fixFormat1(textInput);
        textInput = fixFormat2(textInput);
        textInput = fixFormat3(textInput);
        textInput = setGradient1(textInput);
        textInput = setGradient2(textInput);
        return textInput;
    }

    public String toChatColorString(String textInput) {
        textInput = applyFormats(textInput);
        final Matcher m = hex.matcher(textInput);
        while (m.find()) {
            final String hexCode = m.group();
            textInput = textInput.replace(hexCode, toChatColor(hexCode));
        }
        return textInput;
    }

    //&#RRGGBB
    private String fixFormat1(String text) {
        return text.replace("&#", "#");
    }

    //{#RRGGBB}
    private String fixFormat2(String input) {
        final Matcher m = fix2.matcher(input);
        while (m.find()) {
            final String hexcode = m.group();
            input = input.replace(hexcode, "#" + hexcode.substring(2, 8));
        }
        return input;
    }

    //&x&R&R&G&G&B&B
    private String fixFormat3(String text) {
        text = text.replace('\u00a7', '&');
        final Matcher m = fix3.matcher(text);
        while (m.find()) {
            final String hexcode = m.group();
//            text = text.replace(hexcode, "#" + String(charArrayOf(hexcode[3], hexcode[5], hexcode[7], hexcode[9], hexcode[11], hexcode[13])));
            final StringBuilder fixed = new StringBuilder("#");
            fixed.append(hexcode.charAt(3));
            fixed.append(hexcode.charAt(5));
            fixed.append(hexcode.charAt(7));
            fixed.append(hexcode.charAt(9));
            fixed.append(hexcode.charAt(11));
            fixed.append(hexcode.charAt(13));
            text = text.replace(hexcode, fixed);
        }
        return text;
    }

    //<#RRGGBB>Text</#RRGGBB>
    private String setGradient1(String input) {
        final Matcher m = gradient1.matcher(input);
        while (m.find()) {
            final String format = m.group();
            final TextColor start = new TextColor(format.substring(2, 8));
            final String message = format.substring(9, format.length() - 10);
            final TextColor end = new TextColor(format.substring(format.length() - 7, format.length() - 1));
            input = input.replace(format, asGradient(start, message, end));
        }
        return input;
    }

    //{#RRGGBB>}text{#RRGGBB<}
    private String setGradient2(String input) {
        final Matcher m = gradient2.matcher(input);
        while (m.find()) {
            final String format = m.group();
            final int length = format.length();
            final TextColor start = new TextColor(format.substring(2, 8));
            final String message = format.substring(10, length - 10);
            final TextColor end = new TextColor(format.substring(length - 8, length - 2));
            input = input.replace(format, asGradient(start, message, end));
        }
        return input;
    }

    private String asGradient(TextColor start, String text, TextColor end) {
        final StringBuilder sb = new StringBuilder();
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final int red = (int) (start.red + ((float) (end.red - start.red)) / (length - 1) * i);
            final int green = (int) (start.green + ((float) (end.green - start.green)) / (length - 1) * i);
            final int blue = (int) (start.blue + ((float) (end.blue - start.blue)) / (length - 1) * i);
            sb.append("#").append(toHexString(red, green, blue)).append(text.charAt(i));
        }
        return sb.toString();
    }
}
