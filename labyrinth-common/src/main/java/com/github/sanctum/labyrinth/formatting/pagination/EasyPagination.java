package com.github.sanctum.labyrinth.formatting.pagination;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.FancyMessageChain;
import com.github.sanctum.panther.util.AbstractPaginatedCollection;
import com.github.sanctum.panther.util.Page;
import com.github.sanctum.panther.util.TriadConsumer;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * A predefined paginated template.
 *
 * @param <T> The type of element
 */
public final class EasyPagination<T> {

	private final AbstractPaginatedCollection<T> collection;
	private TriadConsumer<T, Integer, FancyMessage> format;
	private BiConsumer<Player, FancyMessage> header;
	private BiConsumer<Player, FancyMessage> footer;
	private final Player player;

	public EasyPagination(Player target, Collection<T> collection) {
		this.collection = AbstractPaginatedCollection.of(collection);
		this.player = target;
	}

	public EasyPagination(Player target, Collection<T> collection, Comparator<? super T> comparator) {
		this.collection = AbstractPaginatedCollection.of(collection).sort(comparator);
		this.player = target;
	}

	public EasyPagination(Player target, Collection<T> collection, Comparator<? super T> comparator, Predicate<? super T> predicate) {
		this.collection = AbstractPaginatedCollection.of(collection).sort(comparator).filter(predicate);
		this.player = target;
	}

	@SafeVarargs
	public EasyPagination(Player target, T... collection) {
		this.collection = AbstractPaginatedCollection.of(collection);
		this.player = target;
	}

	@SafeVarargs
	public EasyPagination(Player target, Comparator<? super T> comparator, T... collection) {
		this.collection = AbstractPaginatedCollection.of(collection).sort(comparator);
		this.player = target;
	}

	@SafeVarargs
	public EasyPagination(Player target, Comparator<? super T> comparator, Predicate<? super T> predicate, T... collection) {
		this.collection = AbstractPaginatedCollection.of(collection).sort(comparator).filter(predicate);
		this.player = target;
	}

	public void setHeader(BiConsumer<Player, FancyMessage> consumer) {
		this.header = consumer;
	}

	public void setFooter(BiConsumer<Player, FancyMessage> consumer) {
		this.footer = consumer;
	}

	public void setFormat(TriadConsumer<T, Integer, FancyMessage> messageConsumer) {
		this.format = messageConsumer;
	}

	public void limit(int elements) {
		this.collection.limit(elements);
	}

	public int size() {
		return collection.size();
	}

	private String calc(int i) {
		String val = String.valueOf(i);
		int size = String.valueOf(i).length();
		if (val.contains("-")) {
			if (size == 2) {
				val = "-0" + i;
			}
		} else {
			if (size == 1) {
				val = "0" + i;
			}
		}
		return val;
	}

	public void send(int page) {
		Page<T> p = collection.get(page);
		int totalPages = collection.size();
		// Header here
		FancyMessageChain chain = new FancyMessageChain();
		chain.append(header -> this.header.accept(player, header));
		int testing = 0;
		if (page > 0) {
			for (int i = page; i > 0; i--) {
				if (i == page) continue;
				testing += collection.get(i).size();
			}
		}
		for (int i = 0; i < p.size(); i++) {
			T t = p.get(i);
			// send each clan here.
			FancyMessage message = new FancyMessage();
			format.accept(t, testing + i + 1, message);
			chain.append(message);
		}
		chain.append(footer -> this.footer.accept(player, footer));
		chain.append(footer -> {
			FancyMessage pages = new FancyMessage();
			for (int i = page - 2; i < totalPages; i++) {
				int finalI = i + 1;
				if (pages.length() < 17) {
					if (finalI == page) {
						if (i == totalPages - 1) {
							pages.then("&a&l" + calc(finalI)).hover("&cYou're already on this page.");
						} else {
							pages.then("&a&l" + calc(finalI)).hover("&cYou're already on this page.").then("&8...");
						}
					} else {
						if (i == totalPages - 1) {
							if (String.valueOf(i).contains("-")) {
								pages.then("&7" + calc(finalI));
							} else {
								pages.then("&7" + calc(finalI)).action(() -> send(finalI)).hover("&6Click to goto this page.");
							}
						} else {
							if (String.valueOf(i).contains("-")) {
								pages.then("&7" + calc(finalI)).then("&8...");
							} else {
								pages.then("&7" + calc(finalI)).action(() -> send(finalI)).hover("&6Click to goto this page.").then("&8...");
							}
						}
					}
				}
			}
			if (page == 1 || page == 0) {
				if (totalPages == 1) {
					FancyMessage t = (FancyMessage) footer.then("«").color(ChatColor.DARK_GRAY).then(" ").append(pages);
					t.then(" ").then("»").color(ChatColor.DARK_GRAY);
					return;
				}
				FancyMessage t = (FancyMessage) footer.then("«").color(ChatColor.DARK_GRAY).then(" ").append(pages);
				t.then(" ").then("»").action(() -> send(page + 1)).color(ChatColor.DARK_AQUA);
				return;
			}
			if (page == totalPages) {
				FancyMessage t = (FancyMessage) footer.then("«").action(() -> send(page - 1)).color(ChatColor.DARK_AQUA).then(" ").append(pages);
				t.then(" ").then("»").color(ChatColor.DARK_GRAY);
				return;
			}
			FancyMessage t = (FancyMessage) footer.then("«").action(() -> send(page - 1)).color(ChatColor.DARK_AQUA).then(" ").append(pages);
			t.then(" ").then("»").action(() -> send(page + 1)).color(ChatColor.DARK_AQUA);
		});
		chain.send(player).deploy();
		// Footer here
	}


}
