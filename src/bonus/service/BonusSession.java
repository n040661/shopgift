package com.enation.app.shop.component.bonus.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

public final class BonusSession {
	private static final String list_sessionkey ="bonus_list_session_key";
	private static final String one_sessionkey ="bonus_one_session_key";
	private static final String one_giftcart="giftcart_one_session_key";
	
	//不可实例化
	private BonusSession(){
		
	}

	/**
	 * 使用红包
	 * @param bonus
	 */
	public static void use(MemberBonus bonus){
		
		//如果存在跳过
		if(isExists(bonus)){
			return;
		}
		
		//添加到session
		List<MemberBonus> bounsList= (List)ThreadContextHolder.getSessionContext().getAttribute(list_sessionkey);
		if(bounsList==null){
			bounsList= new ArrayList<MemberBonus>();
		}
		
		bounsList.add(bonus);
		
		ThreadContextHolder.getSessionContext().setAttribute(list_sessionkey, bounsList);
		 
	}
	
	
	/**
	 * 用编号取消一个优惠卷的使用
	 * @param sn
	 */
	public static void cancel(String sn){
		
		if(StringUtil.isEmpty(sn)){
			
			return;
		}
		
		List<MemberBonus> bounsList= (List)ThreadContextHolder.getSessionContext().getAttribute(list_sessionkey);
		if(bounsList==null){
			return;
		}
		
		List<MemberBonus> newBounsList = new ArrayList<MemberBonus>();
		for (MemberBonus memberBonus : bounsList) {
			if(sn.equals( memberBonus.getBonus_sn() )){
				continue;
			}
			newBounsList.add(memberBonus);
		}
		
		ThreadContextHolder.getSessionContext().setAttribute(list_sessionkey,newBounsList);
		
	}
	
	
	
	/**
	 * 使用红包
	 * @param bonus
	 */
	public static void useOne(MemberBonus bonus){
		
		ThreadContextHolder.getSessionContext().setAttribute(one_sessionkey, bonus);
		 
	}
	public static void useOneGiftCart(MemberShop shop){
		
		ThreadContextHolder.getSessionContext().setAttribute(one_giftcart, shop);
		
	}
	
	
	
	
	/**
	 * 获取已使用的红包
	 * @return
	 */
	public static List<MemberBonus> get(){
		
		return  (List)ThreadContextHolder.getSessionContext().getAttribute(list_sessionkey);
	}
	public static MemberShop getShopOne(){
		
		return  (MemberShop)ThreadContextHolder.getSessionContext().getAttribute(one_giftcart);	}
	
	/**
	 * 获取只能使用一个的红包
	 * @return
	 */
	public static MemberBonus getOne(){
		
		return  (MemberBonus)ThreadContextHolder.getSessionContext().getAttribute(one_sessionkey);
	}
	
//	RIPERIPERIPE
   public static MemberBonus getOneBonus(String currency){
	   MemberBonus memberBonus = getOne();
	   if(memberBonus==null){
		   memberBonus=new MemberBonus();
	   }
		return  memberBonus;
   }
	/**
	 * 获取已使用红包的金额
	 * @return
	 */
	public static double getUseMoney(String currency){
		List<MemberBonus> bonusList =get();
		double moneyCount = 0;

		if(bonusList!=null){
						
			for (MemberBonus memberBonus : bonusList) {
				double bonusMoney = memberBonus.getBonus_money(); //红包金额
				moneyCount= CurrencyUtil.add(moneyCount,bonusMoney);//累加所有的红包金额
				
			}
			

		}
		MemberBonus memberBonus = getOne();
		if(memberBonus!=null){
			//在这里判断国际化
			//System.out.println("卢布金额"+memberBonus.getBonus_money_ru());
			double bonusMoney = 0.0;
			//System.out.println(currency);
			if(currency.equals("RUB")){
			/*	bonusMoney = memberBonus.getBonus_money_ru();//红包卢布的金额
*/			}else if (currency.equals("CNY")) {
				bonusMoney = memberBonus.getBonus_money(); //红包人民币金额
			}
			moneyCount= CurrencyUtil.add(moneyCount,bonusMoney);//累加所有的红包金额
		}
		return  moneyCount;
	}
	
	public static void clean(){
		ThreadContextHolder.getSessionContext().removeAttribute(one_sessionkey);
	}
	public static void cleanGiftCart(){
		ThreadContextHolder.getSessionContext().removeAttribute(one_giftcart);
	}
	public static void cleanAll(){
		ThreadContextHolder.getSessionContext().removeAttribute(one_sessionkey);
		ThreadContextHolder.getSessionContext().removeAttribute(list_sessionkey);
		ThreadContextHolder.getSessionContext().removeAttribute(one_giftcart);
	}
	public static boolean isExists(MemberBonus bonus){
		 List<MemberBonus> bounsList= (List)ThreadContextHolder.getSessionContext().getAttribute(list_sessionkey);
		 if(bounsList==null) return false;
		 for (MemberBonus memberBonus : bounsList) {
			if(memberBonus.getBonus_id() == bonus.getBonus_id()){
				return true;
			}
		}
		return false;
	}


	
}
