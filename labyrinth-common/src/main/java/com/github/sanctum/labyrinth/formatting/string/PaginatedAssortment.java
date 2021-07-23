package com.github.sanctum.labyrinth.formatting.string;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.library.TextLib;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @deprecated This class has been outmatched and over all replaced by the superior {@link com.github.sanctum.labyrinth.formatting.PaginatedList}
 */
@Deprecated
public class PaginatedAssortment {

	private Collection<String> targetList;
	private Map<String, Long> targetMap;
	private Map<String, Double> targetMapDouble;
	private MapType type;

	private int linesPerPage;
	private int bordersPerPage = 1;
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

	public PaginatedAssortment setBordersPerPage(int bordersPerPage) {
		this.bordersPerPage = bordersPerPage;
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
	public void export(int page) {
		int totalPageCount = 1;
		if ((targetList.size() % linesPerPage) == 0) {
			if (targetList.size() > 0) {
				totalPageCount = targetList.size() / linesPerPage;
			}
		} else {
			totalPageCount = (targetList.size() / linesPerPage) + 1;
		}
		if (page <= totalPageCount) {
			p.sendMessage(StringUtils.use(listTitle.replace("{PAGE}", page + "").replace("{TOTAL}", totalPageCount + "")).translate());
			if (bordersPerPage >= 2) {
				p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
			}
			if (targetList.isEmpty()) {
				p.sendMessage(new ColoredString("&fThe list is empty!", ColoredString.ColorType.MC).toString());
			} else {
				int i = 0, k = 0;
				page--;
				for (String entry : targetList) {
					k++;
					if ((((page * linesPerPage) + i + 1) == k) && (k != ((page * linesPerPage) + linesPerPage + 1))) {
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
				int point = page + 1;
				if (page >= 1) {
					int last = point - 1;
					point++;
					p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
					if (page < (totalPageCount - 1)) {
						BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
						BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
					if (page == (totalPageCount - 1)) {
						BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
						BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cYou are on the last page.");
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
				}
				if (page == 0) {
					if ((page + 1) == totalPageCount) {
						p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
						BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
						BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cThere is only one page.");
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					} else {
						point = page + 2;
						p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
						BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
						BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
				}
			}
		} else {
			p.sendMessage(new ColoredString("&eThere are only &f" + totalPageCount + " &e" + "pages!", ColoredString.ColorType.MC).toString());
		}
	}

	/**
	 * Send the more customized paginated list to the player. ( %p = playerName, %n = pageNumber, %t = totalPageCount )
	 */
	public void exportFancy(int page) {
		p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
		if (bordersPerPage >= 2) {
			p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
		}
		int totalPageCount = 1;
		if ((targetList.size() % linesPerPage) == 0) {
			if (targetList.size() > 0) {
				totalPageCount = targetList.size() / linesPerPage;
			}
		} else {
			totalPageCount = (targetList.size() / linesPerPage) + 1;
		}

		if (page <= totalPageCount) {

			if (targetList.isEmpty()) {
				p.sendMessage(new ColoredString("&fThe list is empty!", ColoredString.ColorType.MC).toString());
			} else {
				int i = 0, k = 0;
				page--;
				for (String entry : targetList) {
					k++;
					if ((((page * linesPerPage) + i + 1) == k) && (k != ((page * linesPerPage) + linesPerPage + 1))) {
						i++;
						if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
							p.spigot().sendMessage(TextLib.getInstance().textRunnable(String.format(normalText.replace("{ENTRY}", PlaceholderAPI.setPlaceholders(p, entry)), PlaceholderAPI.setPlaceholders(p, entry)), String.format(hoverText.replace("{ENTRY}", PlaceholderAPI.setPlaceholders(p, entry)), PlaceholderAPI.setPlaceholders(p, entry)), String.format(hoverTextMessage.replace("{ENTRY}", PlaceholderAPI.setPlaceholders(p, entry)), PlaceholderAPI.setPlaceholders(p, entry)), String.format(commandToRun.replace("{ENTRY}", PlaceholderAPI.setPlaceholders(p, entry)), PlaceholderAPI.setPlaceholders(p, entry))));
						} else {
							p.spigot().sendMessage(TextLib.getInstance().textRunnable(String.format(normalText.replace("{ENTRY}", entry), entry), String.format(hoverText.replace("{ENTRY}", entry), entry), String.format(hoverTextMessage.replace("{ENTRY}", entry), entry), String.format(commandToRun.replace("{ENTRY}", entry), entry)));
						}
					}
				}
				int point = page + 1;
				if (page >= 1) {
					int last = point - 1;
					point++;
					p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
					if (page < (totalPageCount - 1)) {
						BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
						BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
					if (page == (totalPageCount - 1)) {
						BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
						BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cYou are on the last page.");
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
				}
				if (page == 0) {
					if ((page + 1) == totalPageCount) {
						p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
						BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
						BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cThere is only one page.");
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					} else {
						point = page + 2;
						p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
						BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
						BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
						BaseComponent[] send = new BaseComponent[]{all, attach};
						p.spigot().sendMessage(send);
					}
				}
			}
		} else {
			p.sendMessage(new ColoredString("&eThere are only &f" + totalPageCount + " &e" + "pages!", ColoredString.ColorType.MC).toString());
		}
	}

	/**
	 * Send the paginated Map to the player.
	 */
	public void exportSorted(MapType type, int pageNum) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(LabyrinthProvider.getInstance().getPluginInstance(), () -> {
			int page = pageNum;

			int o = linesPerPage;

			if (type.equals(MapType.DOUBLE)) {
				// Double procedure
				HashMap<String, Double> tempMap = new HashMap<>(targetMapDouble);

				p.sendMessage(new ColoredString(listTitle, ColoredString.ColorType.MC).toString());
				if (bordersPerPage >= 2) {
					p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
				}
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
						TreeMap<String, Double> sorted_map = new TreeMap<>(tempMap);
						LinkedHashSet<Map.Entry<String, Double>> m = sorted_map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toCollection(LinkedHashSet::new));

						for (Map.Entry<String, Double> map : m) {
							int pageExact = page + 1;
							if (map.getValue() > nextTopAmount) {
								nextTop = map.getKey();
								nextTopAmount = map.getValue();
							}

							k++;
							if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
								i1++;


								p.spigot().sendMessage(TextLib.getInstance().textRunnable(String.format(normalText.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), nextTop, k, pageExact), String.format(commandToRun.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), nextTop)));

							}
							tempMap.remove(nextTop);
							nextTop = "";
							nextTopAmount = 0.0;

						}
						int point = page + 1;
						if (page >= 1) {
							int last = point - 1;
							point++;
							p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
							if (page < (totalPageCount - 1)) {
								BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
								BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							}
							if (page == (totalPageCount - 1)) {
								BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
								BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cYou are on the last page.");
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							}
						}
						if (page == 0) {
							if ((page + 1) == totalPageCount) {
								p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
								BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
								BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cThere is only one page.");
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							} else {
								point = page + 2;
								p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
								BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
								BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
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
				if (bordersPerPage >= 2) {
					p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
				}
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
						TreeMap<String, Long> sorted_map = new TreeMap<>(tempMap);
						LinkedHashSet<Map.Entry<String, Long>> m = sorted_map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toCollection(LinkedHashSet::new));

						for (Map.Entry<String, Long> map : m) {
							int pageExact = page + 1;
							if (map.getValue() > nextTopAmount) {
								nextTop = map.getKey();
								nextTopAmount = map.getValue();
							}


							k++;
							if ((((page * o) + i1 + 1) == k) && (k != ((page * o) + o + 1))) {
								i1++;
								p.spigot().sendMessage(TextLib.getInstance().textRunnable(String.format(normalText.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverText.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), k, nextTop, format(String.valueOf(nextTopAmount))), String.format(hoverTextMessage.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), nextTop, k, pageExact), String.format(commandToRun.replace("{PLACEMENT}", k + "").replace("{ENTRY}", nextTop).replace("{AMOUNT}", format(String.valueOf(nextTopAmount)) + "").replace("{PAGE}", pageExact + ""), nextTop)));

							}
							tempMap.remove(nextTop);
							nextTop = "";
							nextTopAmount = 0L;

						}
						int point = page + 1;
						if (page >= 1) {
							int last = point - 1;
							point++;
							p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
							if (page < (totalPageCount - 1)) {
								BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
								BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							}
							if (page == (totalPageCount - 1)) {
								BaseComponent all = TextLib.getInstance().textRunnable("&7Navigate ", "&3&l«", "&aGo back a page.", navigateCommand + " " + last);
								BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cYou are on the last page.");
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							}
						}
						if (page == 0) {
							if ((page + 1) == totalPageCount) {
								p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
								BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
								BaseComponent attach = TextLib.getInstance().textHoverable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3»", "&cThere is only one page.");
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							} else {
								point = page + 2;
								p.sendMessage(new ColoredString(listBorder, ColoredString.ColorType.MC).toString());
								BaseComponent all = TextLib.getInstance().textHoverable("&7Navigate ", "&3«", "&cYou are on the first page.");
								BaseComponent attach = TextLib.getInstance().textRunnable(" &r" + (page + 1) + "&7/&r" + totalPageCount, " &3&l»", "&aClick to go forward a page.", navigateCommand + " " + point);
								BaseComponent[] send = new BaseComponent[]{all, attach};
								p.spigot().sendMessage(send);
							}
						}
					}
					// end line
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "There are only " + ChatColor.GRAY + totalPageCount + ChatColor.DARK_AQUA + " pages!");
				}
			}
		}, 2L);
	}

	/**
	 * Get the collection of strings to be exported into a list.
	 *
	 * @return the strings to be sent to the player
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

	public PaginatedAssortment setPlayer(Player p) {
		this.p = p;
		return this;
	}

	public PaginatedAssortment setTargetList(Collection<String> targetList) {
		this.targetList = targetList;
		return this;
	}

}
