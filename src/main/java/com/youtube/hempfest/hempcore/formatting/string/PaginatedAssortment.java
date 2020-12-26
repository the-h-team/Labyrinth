package com.youtube.hempfest.hempcore.formatting.string;

import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.formatting.SortedDoubleMap;
import com.youtube.hempfest.hempcore.formatting.SortedLongMap;
import com.youtube.hempfest.hempcore.formatting.component.Text;
import com.youtube.hempfest.hempcore.formatting.component.Text_R2;
import java.math.BigDecimal;
import java.math.MathContext;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PaginatedAssortment {

    private List<String> targetList;
    private Map<String, Long> targetMap;
    private Map<String, Double> targetMapDouble;

    private int linesPerPage;
    private String navigateCommand;
    private Player p;

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

    public PaginatedAssortment(List<String> targetList) {
        this.targetList = targetList;
    }

    public PaginatedAssortment(Player p, List<String> targetList) {
        this.targetList = targetList;
        this.p = p;
    }

    public PaginatedAssortment(Map<String, Long> targetMap, Map<String, Double> targetMap2) {
        this.targetMap = targetMap;
        if (targetMap2 != null) {
            this.targetMapDouble = targetMap2;
        }
    }

    public PaginatedAssortment(Player p, Map<String, Long> targetMap, Map<String, Double> targetMap2) {
        this.p = p;
        this.targetMap = targetMap;
        if (targetMap2 != null) {
            this.targetMapDouble = targetMap2;
        }
    }

    public enum MapType {
        LONG, DOUBLE
    }

    public void setLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    public void setNavigateCommand(String navigateCommand) {
        this.navigateCommand = navigateCommand;
    }

    public void setNormalText(String normalText) {
        this.normalText = normalText;
    }

    public void setHoverText(String hoverText) {
        this.hoverText = hoverText;
    }

    public void setHoverTextMessage(String hoverTextMessage) {
        this.hoverTextMessage = hoverTextMessage;
    }

    public void setListBorder(String listBorder) {
        this.listBorder = listBorder;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public void setCommandToRun(String commandToRun) {
        this.commandToRun = commandToRun;
    }

    static String fastReplace( String str, String target, String replacement ) {
        int targetLength = target.length();
        if( targetLength == 0 ) {
            return str;
        }
        int idx2 = str.indexOf( target );
        if( idx2 < 0 ) {
            return str;
        }
        StringBuilder buffer = new StringBuilder( targetLength > replacement.length() ? str.length() : str.length() * 2 );
        int idx1 = 0;
        do {
            buffer.append( str, idx1, idx2 );
            buffer.append( replacement );
            idx1 = idx2 + targetLength;
            idx2 = str.indexOf( target, idx1 );
        } while( idx2 > 0 );
        buffer.append( str, idx1, str.length() );
        return buffer.toString();
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
                        String a = entry.replaceAll("%p", p.getName());
                        String b = a.replaceAll("%n", String.valueOf(page + 1));
                        String c = b.replaceAll("%t", String.valueOf(totalPageCount));
                        p.sendMessage(new ColoredString(c, ColoredString.ColorType.MC).toString());
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(HempCore.getInstance(), new Runnable() {

            @Override
            public void run() {
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



}
