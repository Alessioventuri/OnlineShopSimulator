package com.online.shop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDate;

public class Cart {

	private List<Item> items;
	private String label;
	private String date;

	public Cart() {
		items = new ArrayList<>();
		label = "";
		date = LocalDate.now().toString();
	}

	public Cart(String label, String date) {
		items = new ArrayList<>();
		this.label = label;
		this.date = date;
	}

	public Cart(String label, String date, List<Item> items) {
		this.label = label;
		this.date = date;
		this.items = items;
	}

	public Cart(List<Item> items, String label) {
		this.items = items;
		this.label = label;
		date = LocalDate.now().toString();
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public String getDate() {
		return date;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Cart) {
			final Cart other = (Cart) obj;
			return Objects.equals(label, other.label) && Objects.equals(date, other.date);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(label, date);
	}

	@Override
	public String toString() {
		return label + " purchased on " + date;

	}
}