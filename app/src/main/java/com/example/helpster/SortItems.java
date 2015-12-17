package com.example.helpster;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import com.example.helpster.Item.SORTBY;

public class SortItems {

	Item[] items;
	
	public SortItems(Item[] items) {
		this.items = items;
	}

	public Item[] sortBy(SORTBY sortById) {
		switch (sortById) {
		case place:
			Arrays.sort(items, new PlaceComparator());
			break;
		case name:
			Arrays.sort(items, new NameComparator());
			break;
		case amount:
			Arrays.sort(items, new AmountComparator());
			break;
		case request:
			Arrays.sort(items, new RequestComparator());
			break;
		case compensation:
			Arrays.sort(items, new CompensationComparator());
			break;
		case endTime:
			Arrays.sort(items, new EndtimeComparator());
			break;
		case delivery:
			Arrays.sort(items, new DeliveryComparator());
			break;
		}
		return items;
	}

	public class PlaceComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.place.compareTo(rhs.place);
		}
	}

	public class NameComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.name.toLowerCase(Locale.ENGLISH).compareTo(rhs.name.toLowerCase(Locale.ENGLISH));
		}
	}

	public class AmountComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return rhs.amount.compareTo(lhs.amount);
		}
	}

	public class RequestComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.request.compareTo(rhs.request);
		}
	}

	public class CompensationComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.compensation.compareTo(rhs.compensation);
		}
	}

	public class EndtimeComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.endTime.compareTo(rhs.endTime);
		}
	}

	public class DeliveryComparator implements Comparator<Item> {
		@Override
		public int compare(Item lhs, Item rhs) {
			return lhs.delivery.compareTo(rhs.delivery);
		}
	}

}
