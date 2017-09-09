package com.enation.app.shop.component.bonus.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StoreUtil;

import freemarker.template.TemplateModelException;
@Component
@Scope("prototype")
public class MemberShopListTag extends BaseFreeMarkerTag{
	private ICartManager cartManager;
	private IStoreCartManager storeCartManager;
	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap();
   	    List<MemberShop> list=checkShopType(storeCartManager.storeListGoodsCheckOut(ThreadContextHolder.getHttpRequest().getSession().getId()),this.storeCartManager.getShopMemberFormList(UserConext.getCurrentMember().getMember_id()));
   	    result.put("shoplist", list); 
   	    return result;
	}
	public List<MemberShop> checkShopType(List<Map> storeGoodsList,
			List<MemberShop> list) {
		List<MemberShop> membershop=new ArrayList<MemberShop>();
		if(list.size()>0){
			for (MemberShop memberShop2 : list) {
				List<Integer> listsize=this.getShopCount(storeGoodsList, memberShop2);
				if(listsize.size()>0){
					membershop.add(memberShop2);
				}
			}
		}
		MemberShop shop=BonusSession.getShopOne();
		if(membershop.size()>0){
			for (MemberShop memberShop2 : membershop) {
				memberShop2.setUse_status(0);
				memberShop2.setRelatived_shop_name(StoreUtil.ACTIVE_GIFT_CART_REDMAIN+""+memberShop2.getShop_remiain_money()+""+StoreUtil.ACTIVE_BONUS_MONEY+"_"+com.enation.framework.util.DateUtil.toString(memberShop2.getUse_end_date(),"yyyy-MM-dd")+""+StoreUtil.ACTIVE_BONUS_USED);
			}
		}
		if(shop!=null && membershop.size()>0){
			   for (MemberShop memberShop2 : membershop) {
				Integer shop_id=memberShop2.getShop_member_id();
				if(shop_id== shop.getShop_member_id()){
					memberShop2.setUse_status(1);
				}else{
					memberShop2.setUse_status(0);
				}
			}
		}
		return membershop;
	}
	public List<Integer> getShopCount(List<Map> storeGoodsList,
			MemberShop memberShop){
		List<Integer> list=new ArrayList<Integer>();
		if(memberShop.getRelatived_store_id().equalsIgnoreCase("-1000")){
			for(Map map : storeGoodsList){
				list.add((Integer) map.get("store_id"));
			}
		}else{
			 String[] result_store_ids=memberShop.getRelatived_store_id().split(",");
			 for(Map map : storeGoodsList){
				 for (String string : result_store_ids) {
						if(string.equalsIgnoreCase(string.valueOf((Integer)map.get("store_id")))){
							list.add((Integer)map.get("store_id"));
						}
					}
			 }
		}
		return list;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

}
