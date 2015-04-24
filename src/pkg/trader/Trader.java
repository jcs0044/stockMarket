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
	// Name of the trader
	String name;
	// Cash left in the trader's hand
	double cashInHand;
	// Stocks owned by the trader
	ArrayList<Order> position;
	// Orders placed by the trader
	ArrayList<Order> ordersPlaced;

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Order>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	public void buyFromBank(Market market, String symbol, int volume)
			throws StockMarketExpection {
		// Buy stock straight from the bank
		// Need not place the stock in the order list
		// Add it straight to the user's position
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// Adjust cash possessed since the trader spent money to purchase a
		// stock.
		double cashLeftAfterPurchase = this.cashInHand
				- (market.getStockForSymbol(symbol).getPrice() * volume);
		if (cashLeftAfterPurchase >= 0) {
			position.add(new BuyOrder(symbol, volume, market.getStockForSymbol(
					symbol).getPrice(), this));
			this.cashInHand = cashLeftAfterPurchase;
		} else {
			throw new StockMarketExpection("Not enough money to make purchase.");
		}
	}

	public void placeNewOrder(Market market, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Place a new order and add to the orderlist
		// Also enter the order into the orderbook of the market.
		// Note that no trade has been made yet. The order is in suspension
		// until a trade is triggered.
		//
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// A trader cannot place two orders for the same stock, throw an
		// exception if there are multiple orders for the same stock.-
		// Also a person cannot place a sell order for a stock that he does not
		// own. Or he cannot sell more stocks than he possesses. Throw an
		// exception in these cases.
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
				if (OrderUtility.owns(position, symbol)) {
					for (Order eachPosition : position) {
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
				if (OrderUtility.owns(position, symbol)) {
					for (Order eachPosition : position) {
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
		// Notification received that a trade has been made, the parameters are
		// the order corresponding to the trade, and the match price calculated
		// in the order book. Note than an order can sell some of the stocks he
		// bought, etc. Or add more stocks of a kind to his position. Handle
		// these situations.

		// Update the trader's orderPlaced, position, and cashInHand members
		// based on the notification.
		if (o.getType().equals("Buy")) {
			this.cashInHand -= o.getPrice();
			position.add(o);
		} else {
			this.cashInHand += o.getPrice();
			position.add(o);
		}
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Order o : position) {
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