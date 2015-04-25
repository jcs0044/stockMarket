package pkg.util;

import java.util.ArrayList;

import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.SellOrder;

public class OrderUtility {
	public static boolean isAlreadyPresent(ArrayList<Order> ordersPlaced,
			Order newOrder) {
		for (Order order : ordersPlaced) {
			if (((order instanceof BuyOrder) && (newOrder instanceof BuyOrder))
					|| ((order instanceof SellOrder) && (newOrder instanceof SellOrder))) {
				if (order.getStockSymbol().equals(
						newOrder.getStockSymbol())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasStock(ArrayList<Order> position, String symbol) {
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				return true;
			}
		}
		return false;
	}

	public static Order findAndExtractOrder(ArrayList<Order> position,
			String symbol) {
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				position.remove(stock);
				return stock;
			}
		}
		return null;
	}

	public static int ownedQuantity(ArrayList<Order> position, String symbol) {
		int ownedQuantity = 0;
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				ownedQuantity += stock.getSize();
			}
		}
		return ownedQuantity;
	}

}
