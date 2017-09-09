package com.enation.app.shop.component.bonus.plugin;

import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderMeta;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.ICountPriceEvent;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

/**
 * 红包订单插件
 * 
 * @author kingapex
 *2013-9-20上午10:44:21
 */
@Component
@Scope("prototype")
public class BonusOrderDiscountPlugin extends AutoRegisterPlugin  implements ICountPriceEvent, IAfterOrderCreateEvent,IOrderCanelEvent {
	private IOrderMetaManager orderMetaManager;
	private  final String discount_key ="bonusdiscount";
	private IBonusManager bonusManager;
	private IStoreCartManager storeCartManager;
	private ICartManager cartManager;
	
	
	/**
	 * 记录使用的红包
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onAfterOrderCreate(Order order, List<CartItem> arg1, String arg2) {
		//读取列表式的
		List<MemberBonus> bonusList =BonusSession.get();
		if(bonusList==null || bonusList.isEmpty()){
			bonusList=new ArrayList<MemberBonus>();	
		}
		
		//读取单个的
		MemberBonus bonus = BonusSession.getOne();
		if(bonus!=null){
			bonusList.add(bonus);
		}
		
		for (MemberBonus memberBonus : bonusList) {
			int bonusid =memberBonus.getBonus_id();
			int bonusTypeid= memberBonus.getBonus_type_id();
			/*bonusManager.use(bonusid, order.getMember_id(), order.getOrder_id(),order.getSn(),bonusTypeid);*/
		}
		
		OrderPrice orderPrice  = order.getOrderprice();
		Map disItems = orderPrice.getDiscountItem();
		
		Double bonusdiscount =(Double) disItems.get(discount_key);
		
		OrderMeta orderMeta = new OrderMeta();
		orderMeta.setOrderid(order.getOrder_id());
		
		if(bonusdiscount!=null){
			orderMeta.setMeta_key(discount_key);
			orderMeta.setMeta_value( String.valueOf( bonusdiscount) );
			this.orderMetaManager.add(orderMeta);
			
		}
		/*BonusSession.cleanAll();*/
	}

	@Override
	public OrderPrice countPrice(Integer store_id,OrderPrice orderprice,Map map) {
		//获取购物车列表数量
		List<Map> storeGoodsList= new ArrayList<Map>();
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		storeGoodsList=storeCartManager.storeListGoodsCheckOut(sessionid);
		Integer count = storeGoodsList.size();
		if(count==0){
			count=1;
		}
		//订单优惠项
		Map<String,Object> disItems  = orderprice.getDiscountItem();
		//加入红包国际化的判断
		List<CartItem> cartItemList = cartManager.listGoods(sessionid);
		//获取购物车中的第一件商品的信息
		CartItem cartItem=cartItemList.get(0);
		String url=request.getHeader("Referer");
		if(url!=null&&url.indexOf("cart.html")>0){
			BonusSession.cleanAll();
		}
		MemberBonus memberbonus = BonusSession.getOneBonus(cartItem.getCurrency());
		double moneyCount=memberbonus.getBonus_money();
		if(moneyCount==0.0){
			moneyCount=0;
		}
		Integer bonusnumber=0;
		if(memberbonus.getSendplat()!=null && memberbonus.getBonus_id()==-1000){
			for(Map map1 : storeGoodsList){
				Integer store_id2=  (Integer) map1.get("store_id");
				 Integer datacount=cartManager.getCartCountForGoods(sessionid, store_id2,memberbonus.getSendplat());
				 List<StoreCartItem> list = (List<StoreCartItem>) map1.get("goodslist");
				 if(list.size()!=datacount){
					 bonusnumber++;
				}
			}
		}
		//有多少订单，对金额平均分配
		disItems.put(discount_key, moneyCount);//记录红包优惠金额
		
		//处理订单金额
		double needPay =orderprice.getNeedPayMoney();
		String  pointprice= request.getParameter("pointprice");
		Double integralprice=(double) 0;
		if(pointprice!=null || !StringUtil.isEmpty(pointprice)){
			integralprice = Double.parseDouble(pointprice);
		}
		
		if(memberbonus.getSendplat()!=null && memberbonus.getBonus_id()==-1000){
			    Integer datacount=cartManager.getCartCountForGoods(sessionid, store_id,memberbonus.getSendplat());
				for(Map map2 : storeGoodsList){
					Integer store_id2=  (Integer) map2.get("store_id");
					if(store_id2==store_id){
						 List<StoreCartItem> list = (List<StoreCartItem>) map2.get("goodslist");
						 if(datacount==list.size()){
						    	if(bonusnumber!=0){
						        Integer newcount=count-bonusnumber;
						    	needPay= CurrencyUtil.sub(needPay, moneyCount/newcount);
						    	needPay= CurrencyUtil.sub(needPay, integralprice/count);
						    	}else{
						    	 needPay= CurrencyUtil.sub(needPay, moneyCount/count);
								 needPay= CurrencyUtil.sub(needPay, integralprice/count);
						    	}
						    }else{
						    	needPay= CurrencyUtil.sub(needPay, integralprice/count);
						   }
					}
				}
		}
		//orderprice.setGoodsPrice(CurrencyUtil.sub(orderprice.getGoodsPrice(),moneyCount)); //商品应付
		////System.out.println(orderprice.getDiscountPrice()+"-----"+moneyCount+"---相加="+CurrencyUtil.add(orderprice.getDiscountPrice(),moneyCount));
		orderprice.setDiscountPrice(0.00);//优惠总金额
		if(needPay<0){
			orderprice.setNeedPayMoney(0.0);
		}else{
			////System.out.println("需要支付的金额"+needPay);
			orderprice.setNeedPayMoney(needPay);
		}
		
		return orderprice;
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void canel(Order order) {
		//退回红包
		this.bonusManager.returned(order.getOrder_id());
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	

	

}
