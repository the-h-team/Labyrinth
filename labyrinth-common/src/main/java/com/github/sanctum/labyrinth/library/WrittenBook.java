package com.github.sanctum.labyrinth.library;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.annotation.Experimental;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@Experimental(atRisk = true, dueTo = "NMS dependence. No spigot component support.")
public class WrittenBook {

    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) book.getItemMeta();
    int lines = 0;
    String title;
    FancyMessage current = new FancyMessage();

    public WrittenBook setTitle(String title) {
        bookMeta.setTitle(StringUtils.use(title).translate());
        this.title = title;
        return this;
    }

    public WrittenBook setAuthor(String author) {
        bookMeta.setAuthor(author);
        return this;
    }

    public WrittenBook add(BaseComponent[]... components) {
        bookMeta.spigot().addPage(components);
        return this;
    }

    public WrittenBook add(Message message) {
        if (lines >= 13) {
            bookMeta.spigot().addPage(current.build());
            this.lines = 0;
            this.current = new FancyMessage(title).then("\n").append(message).then("\n");
        } else if (lines == 0) {
            this.current.append(message);
        } else {
            this.current.append(message).then("\n");
        }
        lines += message.length();
        return this;
    }

    public void give(Player p)
    {
        if (bookMeta.spigot().getPages().size() == 0) {
            if (lines > 0) {
                bookMeta.spigot().addPage(current.build());
                this.lines = 0;
                this.current = new FancyMessage(title).then("\n");
            }
        }
        book.setItemMeta(bookMeta);
        LabyrinthProvider.getInstance().getItemComposter().add(book, p);
    }
}