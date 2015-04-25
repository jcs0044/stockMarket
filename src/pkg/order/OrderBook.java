package pkg.order;

import java.util.ArrayList;
import java.util.HashMap;
import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import java.util.Map.Entry;
import pkg.trader.Trader;


public class OrderBook {
	private Market market;
	private HashMap<String, ArrayList<Order>> buyOrders;
	private HashMap<String, ArrayList<Order>> sellOrders;
	private ArrayList<Order> stockArray;
	
	public OrderBook(Market market) {
		this.market = market;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
		stockArray = new ArrayList<Order>();
	}

	public void addToOrderBook(Order order) {
		// Populate the buyOrders and sellOrders data structures, whichever
		// appropriate
		if (order.getType() == "Buy") {
			if (buyOrders.get(order.getStockSymbol()) != null) {
				stockArray = buyOrders.get(order.getStockSymbol());
				stockArray.add(order);
			} else {
				stockArray = new ArrayList<Order>();
				stockArray.add(order);
			}
			buyOrders.put(order.getStockSymbol(), stockArray);
		} else {
			if (buyOrders.get(order.getStockSymbol()) != null) {
				stockArray = buyOrders.get(order.getStockSymbol());
				stockArray.add(order);
			} else {
				stockArray = new ArrayList<Order>();
				stockArray.add(order);
			}
			sellOrders.put(order.getStockSymbol(), stockArray);
		}
		
	}

	public void trade()
	{		
		for (Entry<String, ArrayList<Order>> entry : buyOrders.entrySet()) {
			String stockKey = entry.getKey();
			ArrayList<Order> stockBuyList = entry.getValue();
			if (stockBuyList != null && sellOrders.get(stockKey) != null) {
					ArrayList<Order> stockSellList = sellOrders.get(stockKey);
					for (Order buy : stockBuyList) {
						for (Order sell : stockSellList) {
							if (buy.getPrice() == sell.getPrice()) {
								Trader buyTrader = buy.getTrader();
								Trader sellTrader = sell.getTrader();
								// notify buy trader
								try {
									buyTrader.tradePerformed(buy, buy.getPrice());
								} catch (StockMarketExpection e) {
									e.printStackTrace();
								}
								// notify sell trader
								try {
									sellTrader.tradePerformed(sell, sell.getPrice());
								} catch (StockMarketExpection e) {
									e.printStackTrace();
								}
								// update stock price the easy way (using market function)
								try {
									market.updateStockPrice(buy.getStockSymbol(), buy.getPrice());
								} catch (StockMarketExpection e) {
									e.printStackTrace();
								}
								stockBuyList.remove(buy);
								stockSellList.remove(sell);
								buyOrders.put(stockKey, stockBuyList);
								sellOrders.put(stockKey, stockSellList);
							}
						}
					}
				}
			}
		}
	}
	
}
