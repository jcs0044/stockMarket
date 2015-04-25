package pkg.order;

import java.util.ArrayList;
import java.util.HashMap;
import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import java.util.Map.Entry;
import pkg.trader.Trader;


public class OrderBook {
	Market m;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;
	ArrayList<Order> stockArray;
	
	public OrderBook(Market m) {
		this.m = m;
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
		// Complete the trading.
		// 1. Follow and create the orderbook data representation (see spec)
		// 2. Find the matching price
		// 3. Update the stocks price in the market using the PriceSetter.
		// Note that PriceSetter follows the Observer pattern. Use the pattern.
		// 4. Remove the traded orders from the orderbook
		// 5. Delegate to trader that the trade has been made, so that the
		// trader's orders can be placed to his possession (a trader's position
		// is the stocks he owns)
		// (Add other methods as necessary)
		
		for (Entry<String, ArrayList<Order>> entry : buyOrders.entrySet()) {
			String stockKey = entry.getKey();
			ArrayList<Order> stockBuyList = entry.getValue();
			if (stockBuyList != null) {
				if (sellOrders.get(stockKey) != null) {
					ArrayList<Order> stockSellList = sellOrders.get(stockKey);
					for (Order buy : stockBuyList) {
						for (Order sell : stockSellList) {
							if (buy.getPrice() == sell.getPrice()) {
								// Zhu Li do the thing
								Trader buyTrader = buy.getTrader();
								Trader sellTrader = sell.getTrader();
								// notify buy trader
								try {
									buyTrader.tradePerformed(buy, buy.getPrice());
								} catch (StockMarketExpection e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// notify sell trader
								try {
									sellTrader.tradePerformed(sell, sell.getPrice());
								} catch (StockMarketExpection e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// update stock price the easy way (using market function)
								try {
									m.updateStockPrice(buy.getStockSymbol(), buy.getPrice());
								} catch (StockMarketExpection e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// update the map by removing the order from the arraylists and then
								// replacing the current arraylists in the maps with the new ones
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
