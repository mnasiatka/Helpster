package com.example.helpster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchItems {

	static List<Item> oldItems;
	static ArrayList<User> oldeItems;
	static String oldQuery;
	static String oldeQuery;

	public static Item[] search(List<Item> items, String query) {
		query = query.toLowerCase(Locale.ENGLISH);
		if ((oldQuery != null) && query.contains(oldQuery)) {
			items = oldItems;
		}
		oldItems = new ArrayList<Item>();
		for (Item item : items) {
			for (String attr : item.getAttributes()) {
				if ((attr != null) && attr.toLowerCase(Locale.ENGLISH).contains(query)) {
					oldItems.add(item);
					break;
				}
			}
		}
		oldQuery = query;
		Item[] it = new Item[oldItems.size()];
		for (int i = 0; i < it.length; i++) {
			it[i] = oldItems.get(i);
		}
		return it;
	}

	public static Item[] search(Item[] items, String query) {
		return search(Arrays.asList(items), query);
	}

	public static ArrayList<User> msearch(List<User> itemse, String query) {
		query = query.toLowerCase(Locale.ENGLISH);
		if ((oldeQuery != null) && query.contains(oldeQuery)) {
			itemse = oldeItems;
		}
		oldeItems = new ArrayList<User>();
		for (User item : itemse) {
			for (String attr : item.getAttributes()) {
				if ((attr != null) && attr.toLowerCase(Locale.ENGLISH).contains(query)) {
					oldeItems.add(item);
					break;
				}
			}
		}
		oldeQuery = query;
		// User[] it = new User[oldeItems.size()];
		// for (int i = 0; i < it.length; i++) {
		// it[i] = oldeItems.get(i);
		// }
		return oldeItems;
	}

	// public static User[] search(User[] items,
	// String query) {
	// return search(Arrays.asList(items), query);
	// }

	//
	// public T[] search(List<T> items, String query) {
	// query = query.toLowerCase(Locale.ENGLISH);
	// if ((oldQuery != null) && query.contains(oldQuery)) {
	// items = oldItems;
	// }
	// if (items.get(0) instanceof Item) {
	//
	// }
	// oldItems = new ArrayList<T>();
	// for (T item : items) {
	// for (String attr : item.getAttributes()) {
	// if ((attr != null) && attr.toLowerCase(Locale.ENGLISH).contains(query)) {
	// oldItems.add(item);
	// break;
	// }
	// }
	// }
	// oldQuery = query;
	// Item[] it = new Item[oldItems.size()];
	// for (int i = 0; i < it.length; i++) {
	// it[i] = oldItems.get(i);
	// }
	// return it;
	// }
	//
	// public static Item[] search(Item[] items, String query) {
	// return search(Arrays.asList(items), query);
	// }

}
