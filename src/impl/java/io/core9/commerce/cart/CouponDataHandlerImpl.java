package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerConfig;
import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.cart.lineitem.CouponLineItem;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.LinkedLineItem;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CouponDataHandlerImpl<T extends DataHandlerDefaultConfig> implements CouponDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	private CrudRepository<Coupon> coupons;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		coupons = factory.getRepository(Coupon.class);
	}
	
	@Override
	public String getName() {
		return "Commerce-Coupon";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<T>() {
			
			@Override
			public Map<String, Object> handle(Request req, Map<String,Object> context) {
				Map<String,Object> result = new HashMap<String, Object>();
				Cart cart = helper.getCart(req);
				if(context != null && (context.get("handled") == null || (Boolean) context.get("handled") == false)) {
					handleCouponCall(req, cart, context);
					context.put("handled", true);
				}
				result.put("available", getCoupons(req.getVirtualHost(), getCartItemIDs(cart)).size() > 0);
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
		};
	}
	
	protected void handleCouponCall(Request req, Cart cart, Map<String, Object> context) {
		String code = (String) context.get("code");
		Coupon coupon;
		if(context.get("code") == null || (coupon = coupons.read(req.getVirtualHost(), code)) == null) {
			return;
		} else {
			applyCouponToCart(req, cart, coupon);
		}
	}

	public void applyCouponToCart(Request req, Cart cart, Coupon coupon) {
		List<String> skus = coupon.getApplicableSkus();
		for(String sku : skus) {
			LineItem item;
			if((item = cart.getItems().get(sku)) != null) {
				if(coupon.getPercentage() > 0) {
					item.setPrice(item.getPrice() * (1 - coupon.getPercentage()/100));
				} else if(coupon.getAmount() > 0) {
					LinkedLineItem linked;
					if(item instanceof LinkedLineItem) {
						linked = (LinkedLineItem) item;
					} else {
						linked = new LinkedLineItem(item, new ArrayList<String>());
						cart.getItems().put(linked.getId(), linked);
					}
					linked.getLinkedLineItems().add(coupon.getId());
					cart.getItems().put(coupon.getId(), new CouponLineItem(coupon.getId(), -1 * coupon.getAmount(), "Coupon", null, null));
				}
				coupon.decrement();
				coupons.update(req.getVirtualHost(), coupon.getId(), coupon);
			}
		}
		helper.saveCart(req, cart);
	}

	/**
	 * Get a List of SKUS for the cart items
	 * @param cart
	 * @return
	 */
	public List<String> getCartItemIDs(Cart cart) {
		return new ArrayList<String>(cart.getItems().keySet());
	}
	
	/**
	 * Return the available Coupons for a list of SKUs
	 * @param vhost
	 * @param skus
	 * @return
	 */
	public List<Coupon> getCoupons(VirtualHost vhost, List<String> skus) {
		Map<String,Object> query = new HashMap<String, Object>(2);
		Map<String,Object> array = new HashMap<String, Object>(1);
		array.put("$in", skus);
		query.put("applicableSkus", array);
		query.put("active", true);
		List<Coupon> foundCoupons = coupons.query(vhost, query);
		if(foundCoupons != null) {
			return foundCoupons;
		} else {
			return new ArrayList<Coupon>();
		}
	}

}
