package pkg.trader;

import java.util.ArrayList;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.order.Order;
import pkg.order.OrderType;
import pkg.order.BuyOrder;
import pkg.order.SellOrder;
import pkg.util.OrderUtility;

public class Trader {
	String name;
	// Cash left in the trader's hand
	double cashInHand;
	ArrayList<Order> stocksOwned;
	// Orders placed by the trader
	ArrayList<Order> ordersPlaced;

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.stocksOwned = new ArrayList<Order>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	public void buyFromBank(Market market, String symbol, int volume)
			throws StockMarketExpection {
		double cashLeftAfterPurchase = this.cashInHand
				- (market.getStockForSymbol(symbol).getPrice() * volume);
		if (cashLeftAfterPurchase >= 0) {
			stocksOwned.add(new BuyOrder(symbol, volume, market.getStockForSymbol(
					symbol).getPrice(), this));
			this.cashInHand = cashLeftAfterPurchase;
		} else {
			throw new StockMarketExpection("Not enough money to make purchase.");
		}
	}

	public void placeNewOrder(Market market, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		BuyOrder newBuyOrder = new BuyOrder(symbol, volume, price, this);
		if (!(OrderUtility.isAlreadyPresent(ordersPlaced, newBuyOrder))) {
			if (orderType == OrderType.BUY) {
				double cashLeftAfterPurchase = this.cashInHand
						- (market.getStockForSymbol(symbol).getPrice() * volume);
				if (cashLeftAfterPurchase >= 0) {
					ordersPlaced.add(newBuyOrder);
					market.addOrder(newBuyOrder);
				} else {
					throw new StockMarketExpection(
							"Not enough money to make purchase.");
				}
			} else {
				SellOrder newSellOrder = new SellOrder(symbol, volume, price,
						this);
				if (OrderUtility.owns(stocksOwned, symbol)) {
					for (Order eachPosition : stocksOwned) {
						if ((eachPosition instanceof SellOrder)
								&& (newSellOrder instanceof SellOrder)) {
							if (eachPosition.getStockSymbol().equals(
									newSellOrder.getStockSymbol())) {
								if (eachPosition.getSize() < volume) {
									throw new StockMarketExpection(
											"You do not have that many shares to sell");
								}
							}
						}
					}
					ordersPlaced.add(newSellOrder);
					market.addOrder(newSellOrder);
				} else {
					throw new StockMarketExpection(
							"You cannot sell a Stock you do not own.");
				}
			}
		} else {
			throw new StockMarketExpection(
					"You cannot make more than one offer on the same stock.");
		}
	}

	public void placeNewMarketOrder(Market market, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Similar to the other method, except the order is a market order
		BuyOrder newBuyOrder = new BuyOrder(symbol, volume, true, this);
		if (!(OrderUtility.isAlreadyPresent(ordersPlaced, newBuyOrder))) {
			if (orderType == OrderType.BUY) {
				double cashLeftAfterPurchase = this.cashInHand
						- (market.getStockForSymbol(symbol).getPrice() * volume);
				if (cashLeftAfterPurchase >= 0) {
					ordersPlaced.add(newBuyOrder);
					market.addOrder(newBuyOrder);
				} else {
					throw new StockMarketExpection(
							"Not enough money to make purchase.");
				}
			} else {
				SellOrder newSellOrder = new SellOrder(symbol, volume, true,
						this);
				if (OrderUtility.owns(stocksOwned, symbol)) {
					for (Order eachPosition : stocksOwned) {
						if ((eachPosition instanceof SellOrder)
								&& (newSellOrder instanceof SellOrder)) {
							if (eachPosition.getStockSymbol().equals(
									newSellOrder.getStockSymbol())) {
								if (eachPosition.getSize() < volume) {
									throw new StockMarketExpection(
											"You do not have that many shares to sell");
								}
							}
						}
					}
					ordersPlaced.add(newSellOrder);
					market.addOrder(newSellOrder);
				} else {
					throw new StockMarketExpection(
							"You cannot sell a Stock you do not own.");
				}
			}
		} else {
			throw new StockMarketExpection(
					"You cannot make more than one offer on the same stock.");
		}
	}

	public void tradePerformed(Order o, double matchPrice)
			throws StockMarketExpection {
		// Update the trader's orderPlaced, position, and cashInHand members
		// based on the notification.
		if (o.getType().equals("Buy")) {
			this.cashInHand -= o.getPrice();
			stocksOwned.add(o);
		} else {
			this.cashInHand += o.getPrice();
			stocksOwned.add(o);
		}
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Order o : stocksOwned) {
			o.printStockNameInOrder();
		}
		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}
}
