package com.github.sanctum.Labyrinth.formatting.string;

import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class ColoredString {


    private final ColorType chosen;
    private final String text;
    private final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public ColoredString(String text, ColorType type) {
        this.text = text;
        this.chosen = type;
    }

    public enum ColorType {
        MC, MC_COMPONENT, HEX
    }

    /**
     * Translate the text within a string body to color
     */
    public String toString() {
        String r = "No context to return";
        switch (chosen) {
            case MC:
                r = ChatColor.translateAlternateColorCodes('&', text);
                break;
            case MC_COMPONENT:
                r = "Cannot convert raw component to String";
                break;
            case HEX:
                r = translateHexString(text);
                break;
        }
        return r;
    }

    /**
     * Translate the text within a TextComponent body to color
     * @return Returns a string of text embedded as a Component
     */
    public TextComponent toComponent() {
        return translateHexComponent(text);
    }

    private String translateHexString(String text){

        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++){
            if (texts[i].equalsIgnoreCase("&")) {
                //get the next string
                i++;
                if (texts[i].charAt(0) == '#'){
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                }else{
                    finalText.append(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            }else{
                finalText.append(texts[i]);
            }
        }

        return finalText.toString();
    }

    private TextComponent translateHexComponent(String text){

        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        ComponentBuilder builder = new ComponentBuilder();

        for (int i = 0; i < texts.length; i++){
            TextComponent subComponent = new TextComponent();
            if (texts[i].equalsIgnoreCase("&")){
                //get the next string
                i++;
                if (texts[i].charAt(0) == '#'){
                    subComponent.setText(texts[i].substring(7));
                    subComponent.setColor(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)));
                    builder.append(subComponent);
                }else{
                    builder.append(translateHexString("&" + texts[i]));
                }
            }else{
                builder.append(texts[i]);
            }
        }

        return new TextComponent(builder.create());

    }


}
