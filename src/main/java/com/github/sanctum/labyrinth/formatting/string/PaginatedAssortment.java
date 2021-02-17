package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.formatting.SortedDoubleMap;
import com.github.sanctum.labyrinth.formatting.SortedLongMap;
import com.github.sanctum.labyrinth.Labyrinth;
import com.github.sanctum.labyrinth.formatting.component.Text;
import com.github.sanctum.labyrinth.formatting.component.Text_R2;
import java.math.BigDecimal;
import java.math.MathContext;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PaginatedAssortment {

    private Collection<String> targetList;
    private Map<String, Long> targetMap;
    private Map<String, Double> targetMapDouble;
    private MapType type;

    private int linesPerPage;
    private String navigateCommand;
    private final Player p;

    // ------------ Formatting ------------- //
    private String listTitle = "&o-- [Specify the returned list's title here] --";
    private String listBorder = "&7&o&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
    private String normalText = "";
    private String hoverText = "#%placement %holder : %amount";
    private String hoverTextMessage = "%holder places %placement on page %page";
    private String commandToRun = "command <#index>";
    // ------------------------------------- //

    protected double format(String amount) {
        BigDecimal b1 = new BigDecimal(amount);
        MathContext m = new MathContext(3);
        BigDecimal b2 = b1.round(m);
        return b2.doubleValue();
    }

    public PaginatedAssortment(Player p, Collection<String> targetList) {
        this.targetList = targetList;
        this.p = p;
    }

    public PaginatedAssortment(Player p, Map<String, Long> targetMap, Map<String, Double> targetMap2) {
        this.p = p;
        this.targetMap = targetMap;
        if (targetMap2 != null)
            this.targetMapDouble = targetMap2;
    }

    public enum MapType {
        LONG, DOUBLE
    }

    public PaginatedAssortment setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        return this;
    }

    public PaginatedAssortment setNavigateCommand(String navigateCommand) {
        this.navigateCommand = navigateCommand;
        return this;
    }

    public PaginatedAssortment setNormalText(String normalText) {
        this.normalText = normalText;
        return this;
    }

    public PaginatedAssortment setHoverText(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }

    public PaginatedAssortment setHoverTextMessage(String hoverTextMessage) {
        this.hoverTextMessage = hoverTextMessage;
        return this;
    }

    public PaginatedAssortment setListBorder(String listBorder) {
        this.listBorder = listBorder;
        return this;
    }

    public PaginatedAssortment setListTitle(String listTitle) {
        this.listTitle = listTitle;
        return this;
    }

    public PaginatedAssortment setCommandToRun(String commandToRun) {
        this.commandToRun = commandToRun;
        return this;
    }

    /**
     * Send the paginated list to the player. ( %p = playerName, %n = pageNumber, %t = totalPageCount )
     */
    public void export(int page)
    {
        p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
        int totalPageCount = 1;
        if((targetList.size() % linesPerPage) == 0)
        {
            if(targetList.size() > 0)
            {
                totalPageCount = targetList.size() / linesPerPage;
            }
        }
        else
        {
            totalPageCount = (targetList.size() / linesPerPage) + 1;
        }

        if(page <= totalPageCount)
        {

            if(targetList.isEmpty())
            {
                p.sendMessage(new ColoredString("&fThe list is empty!", ColoredString.ColorType.MC).toString());
            }
            else
            {
                int i = 0, k = 0;
                page--;
                for (String entry : targetList)
                {
                    k++;
                    if ((((page * linesPerPage) + i + 1) == k) && (k != ((page * linesPerPage) + linesPerPage + 1)))
                    {
                        i++;
                        if (Bukkit.getVersion().contains("1.16")) {
                            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                p.sendMessage(new ColoredString(PlaceholderAPI.setPlaceholders(p, entry), ColoredString.ColorType.HEX).toString());
                            } else {
                                p.sendMessage(new ColoredString(entry, ColoredString.ColorType.HEX).toString());
                            }
                        } else {
                            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                p.sendMessage(new ColoredString(PlaceholderAPI.setPlaceholders(p, entry), ColoredString.ColorType.MC).toString());
                            } else {
                                p.sendMessage(new ColoredString(entry, ColoredString.ColorType.MC).toString());
                            }
                        }
                    }
                }
                int point; point = page + 1; if (page >= 1) {
                int last; last = point - 1; point = point + 1;
                p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                if (page < (totalPageCount - 1)) {
                    if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                        p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK&7]", "&7 : [", "&b&oNEXT&7]", "&b&oClick to go &d&oback a page", "&b&oClick to goto the &5&onext page", navigateCommand + " " + last, navigateCommand + " " + point));
                    } else {
                        p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK&7]", "&7 : [", "&b&oNEXT&7]", "&b&oClick to go &d&oback a page", "&b&oClick to goto the &5&onext page", navigateCommand + " " + last, navigateCommand + " " + point));
                    }
                }
                if (page == (totalPageCount - 1)) {
                      if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                          p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK", "&7]", "&b&oClick to go &d&oback a page", navigateCommand + " " + last));
                      } else {
                          p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK", "&7]", "&b&oClick to go &d&oback a page", navigateCommand + " " + last));
                      }
                }
            } if (page == 0) {
                point = page + 1 + 1;
                p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                  if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                      p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&b&oNEXT", "&7]", "&b&oClick to goto the &5&onext page", navigateCommand + " " + point));
                  } else {
                      p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&b&oNEXT", "&7]", "&b&oClick to goto the &5&onext page", navigateCommand + " " + point));
                  }
            }
            }
        }
        else
        {
            p.sendMessage(new ColoredString("&eThere are only &f" + totalPageCount + " &epages!", ColoredString.ColorType.MC).toString());
        }
    }

    /**
     * Send the more customized paginated list to the player. ( %p = playerName, %n = pageNumber, %t = totalPageCount )
     */
    public void exportFancy(int page)
    {
        p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
        int totalPageCount = 1;
        if((targetList.size() % linesPerPage) == 0)
        {
            if(targetList.size() > 0)
            {
                totalPageCount = targetList.size() / linesPerPage;
            }
        }
        else
        {
            totalPageCount = (targetList.size() / linesPerPage) + 1;
        }

        if(page <= totalPageCount)
        {

            if(targetList.isEmpty())
            {
                p.sendMessage(new ColoredString("&fThe list is empty!", ColoredString.ColorType.MC).toString());
            }
            else
            {
                int i = 0, k = 0;
                page--;
                for (String entry : targetList)
                {
                    k++;
                    if ((((page * linesPerPage) + i + 1) == k) && (k != ((page * linesPerPage) + linesPerPage + 1)))
                    {
                        i++;
                        if (Bukkit.getServer().getVersion().contains("1.16")) {
                            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                p.spigot().sendMessage(new Text().textRunnable(String.format(normalText, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(hoverText, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(hoverTextMessage, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(commandToRun, PlaceholderAPI.setBracketPlaceholders(p, entry))));
                            }  else {
                                p.spigot().sendMessage(new Text().textRunnable(String.format(normalText, entry), String.format(hoverText, entry), String.format(hoverTextMessage, entry), String.format(commandToRun, entry)));
                            }
                        } else {
                            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                                p.spigot().sendMessage(Text_R2.textRunnable(String.format(normalText, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(hoverText, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(hoverTextMessage, PlaceholderAPI.setBracketPlaceholders(p, entry)), String.format(commandToRun, PlaceholderAPI.setBracketPlaceholders(p, entry))));
                            }  else {
                                p.spigot().sendMessage(Text_R2.textRunnable(String.format(normalText, entry), String.format(hoverText, entry), String.format(hoverTextMessage, entry), String.format(commandToRun, entry)));
                            }
                        }
                    }
                }
                int point; point = page + 1; if (page >= 1) {
                int last; last = point - 1; point = point + 1;
                p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                if (page < (totalPageCount - 1)) {
                    if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                        p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK&7]", "&7 : [", "&b&oNEXT&7]", "&b&oClick to go &d&oback a page", "&b&oClick to goto the &5&onext page", navigateCommand + " " + last, navigateCommand + " " + point));
                    } else {
                        p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK&7]", "&7 : [", "&b&oNEXT&7]", "&b&oClick to go &d&oback a page", "&b&oClick to goto the &5&onext page", navigateCommand + " " + last, navigateCommand + " " + point));
                    }
                }
                if (page == (totalPageCount - 1)) {
                    if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                        p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK", "&7]", "&b&oClick to go &d&oback a page", navigateCommand + " " + last));
                    } else {
                        p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&c&oBACK", "&7]", "&b&oClick to go &d&oback a page", navigateCommand + " " + last));
                    }
                }
            } if (page == 0) {
                point = page + 1 + 1;
                p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
                    p.spigot().sendMessage(new Text().textRunnable("&7Navigate &b&o&m--&b> &7[", "&b&oNEXT", "&7]", "&b&oClick to goto the &5&onext page", navigateCommand + " " + point));
                } else {
                    p.spigot().sendMessage(Text_R2.textRunnable("&7Navigate &b&o&m--&b> &7[", "&b&oNEXT", "&7]", "&b&oClick to goto the &5&onext page", navigateCommand + " " + point));
                }
            }
            }
        }
        else
        {
            p.sendMessage(new ColoredString("&eThere are only &f" + totalPageCount + " &epages!", ColoredString.ColorType.MC).toString());
        }
    }

    /**
     * Send the paginated Map to the player.
     */
    public void exportSorted(MapType type, int pageNum) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Labyrinth.getInstance(), () -> {
            int page = pageNum;

            int o = linesPerPage;

            if (type.equals(MapType.DOUBLE)) {
                // Double procedure
                HashMap<String, Double> tempMap = new HashMap<>(targetMapDouble);

                p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
                int totalPageCount = 1;
                if ((tempMap.size() % o) == 0) {
                    if (tempMap.size() > 0) {
                        totalPageCount = tempMap.size() / o;
                    }
                } else {
                    totalPageCount = (tempMap.size() / o) + 1;
                }
                String nextTop = "";
                double nextTopAmount = 0.0;

                if (page <= totalPageCount) {
                    // begin line
                    if (tempMap.isEmpty()) {
                        p.sendMessage(ChatColor.WHITE + "The list is empty!");
                    } else {
                        int i1 = 0, k = 0;
                        page--;
                        SortedDoubleMap comp = new SortedDoubleMap(tempMap);
                        TreeMap<String, Double> sorted_map = new TreeMap<>(comp);
                        sorted_map.putAll(tempMap);

                        for (Map.Entry<String, Double> map : sorted_map.entrySet()) {
                            int pageExact = page + 1;
                            if (map.getValue() > nextTopAmount) {
                                nextTop = map.getKey();
                                nextTopAmount = map.getValue();
                            }

                            k++;
                            if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
                                i1++;


                                    if (Bukkit.getServer().getVersion().contains("1.16")) {
                                        p.spigot().sendMessage(new Text().textRunnable(String.format(normalText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage, nextTop, k, pageExact), String.format(commandToRun, nextTop)));
                                    } else {
                                        p.spigot().sendMessage(Text_R2.textRunnable(String.format(normalText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage, nextTop, k, pageExact), String.format(commandToRun, nextTop)));
                                    }

                            }
                            tempMap.remove(nextTop);
                            nextTop = "";
                            nextTopAmount = 0.0;

                        }
                        int point = page + 1;
                        if (page >= 1) {
                            p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                            int last = point - 1;
                            point = point + 1;
                            if (Bukkit.getServer().getVersion().contains("1.16")) {
                                p.spigot().sendMessage(new Text().textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7] : &7[", "&c&lCLICK&7]", "&b&oClick this to goto the &5&onext page.", "&b&oClick this to go &d&oback a page.", navigateCommand + " " + point, navigateCommand + " " + last));
                            } else {
                                p.spigot().sendMessage(Text_R2.textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7] : &7[", "&c&lCLICK&7]", "&b&oClick this to goto the &5&onext page.", "&b&oClick this to go &d&oback a page.", navigateCommand + " " + point, navigateCommand + " " + last));
                            }
                        }
                        if (page == 0) {
                            point = page + 2;
                            p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                            if (Bukkit.getServer().getVersion().contains("1.16")) {
                                p.spigot().sendMessage(new Text().textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7]", "&b&oClick this to goto the &5&onext page.", navigateCommand + " " + point));
                            } else {
                                p.spigot().sendMessage(Text_R2.textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7]", "&b&oClick this to goto the &5&onext page.", navigateCommand + " " + point));
                            }
                        }
                    }
                    // end line
                } else {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are only " + ChatColor.GRAY + totalPageCount + ChatColor.DARK_AQUA + " pages!");
                }
            } else if (type.equals(MapType.LONG)) {

                // Long procedure
                HashMap<String, Long> tempMap = new HashMap<>(targetMap);

                p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
                int totalPageCount = 1;
                if ((tempMap.size() % o) == 0) {
                    if (tempMap.size() > 0) {
                        totalPageCount = tempMap.size() / o;
                    }
                } else {
                    totalPageCount = (tempMap.size() / o) + 1;
                }
                String nextTop = "";
                Long nextTopAmount = 0L;

                if (page <= totalPageCount) {
                    // begin line
                    if (tempMap.isEmpty()) {
                        p.sendMessage(ChatColor.WHITE + "The list is empty!");
                    } else {
                        int i1 = 0, k = 0;
                        page--;
                        SortedLongMap comp = new SortedLongMap(tempMap);
                        TreeMap<String, Long> sorted_map = new TreeMap<>(comp);
                        sorted_map.putAll(tempMap);

                        for (Map.Entry<String, Long> map : sorted_map.entrySet()) {
                            int pageExact = page + 1;
                            if (map.getValue() > nextTopAmount) {
                                nextTop = map.getKey();
                                nextTopAmount = map.getValue();
                            }


                            k++;
                            if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
                                i1++;
                                if (Bukkit.getServer().getVersion().contains("1.16")) {
                                    p.spigot().sendMessage(new Text().textRunnable(String.format(normalText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage, nextTop, k, pageExact), String.format(commandToRun, nextTop)));
                                } else {
                                    p.spigot().sendMessage(Text_R2.textRunnable(String.format(normalText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText, k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage, nextTop, k, pageExact), String.format(commandToRun, nextTop)));
                                }


                            }
                            tempMap.remove(nextTop);
                            nextTop = "";
                            nextTopAmount = 0L;

                        }
                        int point = page + 1;
                        if (page >= 1) {
                            p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                            int last = point - 1;
                            point = point + 1;
                            if (Bukkit.getServer().getVersion().contains("1.16")) {
                                p.spigot().sendMessage(new Text().textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7] : &7[", "&c&lCLICK&7]", "&b&oClick this to goto the &5&onext page.", "&b&oClick this to go &d&oback a page.", navigateCommand + " " + point, navigateCommand + " " + last));
                            } else {
                                p.spigot().sendMessage(Text_R2.textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7] : &7[", "&c&lCLICK&7]", "&b&oClick this to goto the &5&onext page.", "&b&oClick this to go &d&oback a page.", navigateCommand + " " + point, navigateCommand + " " + last));
                            }
                        }
                        if (page == 0) {
                            point = page + 2;
                            p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
                            if (Bukkit.getServer().getVersion().contains("1.16")) {
                                p.spigot().sendMessage(new Text().textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7]", "&b&oClick this to goto the &5&onext page.", navigateCommand + " " + point));
                            } else {
                                p.spigot().sendMessage(Text_R2.textRunnable("&b&oNavigate &7[", "&3&lCLICK", "&7]", "&b&oClick this to goto the &5&onext page.", navigateCommand + " " + point));
                            }
                        }
                    }
                    // end line
                } else {
                    p.sendMessage(ChatColor.DARK_AQUA + "There are only " + ChatColor.GRAY + totalPageCount + ChatColor.DARK_AQUA + " pages!");
                }
            }
        },2L);
    }

    /**
     * Returns the targetMap as sorted.
     */
    public TreeMap<String, Long> longMapSorted() {
        HashMap<String, Long> tempMap = new HashMap<>(targetMap);
        SortedLongMap comp =  new SortedLongMap(tempMap);
        return new TreeMap<>(comp);
    }

    /**
     * Returns the targetMap as sorted.
     */
    public TreeMap<String, Double> doubleMapSorted() {
        HashMap<String, Double> tempMap = new HashMap<>(targetMapDouble);
        SortedDoubleMap comp =  new SortedDoubleMap(tempMap);
        return new TreeMap<>(comp);
    }

    /**
     * Gets the collection of strings to be exported into a list.
     * @return The list of strings to be sent to the player.
     */
    public Collection<String> getTargetList() {
        return targetList;
    }

    public String getNormalText() {
        return normalText;
    }

    public String getCommandToRun() {
        return commandToRun;
    }

    public String getListTitle() {
        return listTitle;
    }

    public String getListBorder() {
        return listBorder;
    }

    public String getHoverText() {
        return hoverText;
    }

    public String getHoverTextMessage() {
        return hoverTextMessage;
    }

    public String getNavigateCommand() {
        return navigateCommand;
    }

    public int getLinesPerPage() {
        return linesPerPage;
    }

    public MapType getType() {
        return type;
    }

    public PaginatedAssortment setType(MapType type) {
        this.type = type;
        return this;
    }

    public PaginatedAssortment setTargetList(Collection<String> targetList) {
        this.targetList = targetList;
        return this;
    }

}
