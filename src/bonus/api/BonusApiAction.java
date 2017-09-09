package com.enation.app.shop.component.bonus.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.DictStatus;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.StoreActiveItem;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.tradeease.core.util.StoreBonusCompatator;
import com.enation.app.tradeease.core.util.StoreCompatator;
import com.enation.app.tradeease.core.util.StoreCountCompatator;
import com.enation.app.tradeease.core.util.StoreItemCompatator;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StoreUtil;
import com.enation.framework.util.StringUtil;

import edu.emory.mathcs.backport.java.util.Collections;

@ParentPackage("shop_default")
@Namespace("/api/shop")
@Scope("prototype")
@Action("bonus")
public class BonusApiAction extends WWAction {
	
	private IBonusManager bonusManager;
	private ICartManager cartManager;
	private IStoreCartManager storeCartManager;
	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	private int bonusid;
	private String sn;
	private Integer shop_cart_id;
	
	
	/**
	 * 获取会员可用红包列表
	 * @param 无
	 * @return 红包列表，List<Map>型
	 * map内容
	 * type_name:红包名称
	 * type_money:红包金额
	 * send_type：红包类型 (0会员发放，1:按商品发放,2:按订单发放,3:线下发放的红包)
	 */
	 
	public String getMemberBonus(){
		try {
			Member member = UserConext.getCurrentMember();
			if(member ==null){
				this.showErrorJson("未登录，不能使用此api");
				return this.JSON_MESSAGE;
			}
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			List bonusList  = bonusManager.getMemberBonusList(member.getMember_id(), goodsprice,1);
			this.json = JsonMessageUtil.getListJson(bonusList);
		} catch (Exception e) {
			this.logger.error("调用获取会员红包api出错",e);
			this.showErrorJson(e.getMessage());
		}
	
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 使用一个红包
	 * @param bonusid:红包id
	 * @return json字串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String useOne(){
		try {
			//清除使用的红包
			if(bonusid==0){
				BonusSession.clean();
				this.showSuccessJson("清除红包成功");
				return this.JSON_MESSAGE;
			}
			MemberBonus bonus  =bonusManager.getBonus(bonusid);
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
            String sessionid = request.getSession().getId();
			List<CartItem> cartItemList = cartManager.listGoods(sessionid);
			//获取购物车中的第一件商品的信息
			CartItem cartItem=cartItemList.get(0);
			String currency = cartItem.getCurrency();
			double minGoodsAmount = 0.0;
			
			Double goodsprice = cartManager.countGoodsTotalForRussion(this.getRequest().getSession().getId(),bonus.getSendplat());
			if(CurrencyUtil.mul(goodsprice,10000)<= minGoodsAmount){				
				if(currency.equals("RUB")){
					this.showErrorJson("Заказная сумма товаров недостаточная["+minGoodsAmount+"p.],Вы не можете использовать этот купон");
					return this.JSON_MESSAGE;
				}else if (currency.equals("CNYW")) {
                     if(bonus.getSendplat()==0){
                    	 this.showErrorJson("您购买的俄罗斯馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
                     }else if(bonus.getSendplat()==1){
                    	 this.showErrorJson("您购买的龙江物产馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
                     }else if(bonus.getSendplat()==2){
                    	 this.showErrorJson("您购买的澳大利亚馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
					}else if(bonus.getSendplat()==3){
						this.showErrorJson("您购买的新西兰馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
					}else if(bonus.getSendplat()==4){
					    this.showErrorJson("您购买的韩国馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
				    }else if(bonus.getSendplat()==5){
					  this.showErrorJson("您购买的德国馆商品的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
				    }else if(bonus.getSendplat()==-1){
				    	 this.showErrorJson("您购买的订单商品总金额不足[￥"+minGoodsAmount+"],不能使用此优惠券");
				    }
					return this.JSON_MESSAGE;
				}				
			}
			
			BonusSession.useOne(bonus);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	
	public String useSn(){
		try {
			
			 
			if(StringUtil.isEmpty(sn)){
				this.showErrorJson("红包编号不能为空");
				return this.JSON_MESSAGE;
			}
			
			MemberBonus bonus  =bonusManager.getBonus(sn);
			if(bonus==null){
				this.showErrorJson("您输入的红包编号不正确");
				return this.JSON_MESSAGE;
			}
			
			
			if(bonus.getUsed_time()!=null){
				
				this.showErrorJson("此红包已被使用过");
				return this.JSON_MESSAGE;
			}
	
	
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			if(goodsprice<= bonus.getMin_goods_amount()){
				
				this.showErrorJson("订单的商品金额不足["+bonus.getMin_goods_amount()+"],不能使用此红包");
				return this.JSON_MESSAGE;
				
			}
			
			long now = DateUtil.getDateline();
			if(bonus.getUse_start_date() > now){
				long l=Long.valueOf(bonus.getUse_start_date())*1000;
				this.showErrorJson("此红包还未到使用期，开始使用时间为["+DateUtil.toString(new Date(l), "yyyy年MM月dd日")+"]");
				return this.JSON_MESSAGE;
			}
			
			if(bonus.getUse_end_date() < now){
				long l=Long.valueOf(bonus.getUse_end_date())*1000;
				this.showErrorJson("此红包已过期，使用截至时间为["+DateUtil.toString(new Date(l), "yyyy年MM月dd日")+"]");
				return this.JSON_MESSAGE;
			}
			
			BonusSession.use(bonus);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	public String useOneGiftCart(){
		try {
			//清除使用的红包
			if(shop_cart_id==0){
				BonusSession.cleanGiftCart();
				this.showSuccessJson("清除购物卡成功");
				return this.JSON_MESSAGE;
			}
			MemberShop shopgift  =bonusManager.getMemeberShop(shop_cart_id);
			BonusSession.useOneGiftCart(shopgift);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * 用sn取消一个红包的使用
	 * @return
	 */
	public String cancelSn(){
		
		try {
			if(StringUtil.isEmpty(sn)){
				this.showErrorJson("编号不能为空");
				return this.JSON_MESSAGE;
			}
			BonusSession.cancel(sn);
			this.showSuccessJson("取消成功");
		} catch (Exception e) {
			this.showErrorJson("取消红包发生错误["+e.getMessage()+"]");
			this.logger.error("取消红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 获取使用的红包的金额
	 * @param 获取正在使用的红包金额
	 * @return json字串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 如果错误，为提示信息
	 * money: 如果调用成功，为使用红包的总金额
	 */
	public String getUseBonusMoney(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		//加入红包国际化的判断
		List<CartItem> cartItemList = cartManager.listGoods(sessionid);
		//获取购物车中的第一件商品的信息
		CartItem cartItem=cartItemList.get(0);
		try {
			double moneyCount = BonusSession.getUseMoney(cartItem.getCurrency());
			this.json = JsonMessageUtil.getNumberJson("money", moneyCount);
		
		} catch (Exception e) {
			this.logger.error("获取红包金额出错", e);
			this.showErrorJson("获取红包金额出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public int getBonusid() {
		return bonusid;
	}

	public void setBonusid(int bonusid) {
		this.bonusid = bonusid;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getShop_cart_id() {
		return shop_cart_id;
	}

	public void setShop_cart_id(Integer shop_cart_id) {
		this.shop_cart_id = shop_cart_id;
	}
	
	
}
