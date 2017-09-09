package com.enation.app.shop.component.bonus.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.DictStatus;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.StoreActiveItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.tradeease.core.util.StoreBonusCompatator;
import com.enation.app.tradeease.core.util.StoreCompatator;
import com.enation.app.tradeease.core.util.StoreCountCompatator;
import com.enation.app.tradeease.core.util.StoreItemCompatator;
import com.enation.eop.processor.core.Request;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StoreUtil;
import com.enation.framework.util.StringUtil;

import edu.emory.mathcs.backport.java.util.Collections;
import freemarker.template.TemplateModelException;

/**
 * 会员红包列表标签
 * @author kingapex
 *2013-9-18下午10:20:40
 */
@Component
@Scope("prototype")
public class MemberBonusListTag extends BaseFreeMarkerTag {
	private IStoreCartManager storeCartManager;
	private IBonusManager bonusManager;
	private ICartManager cartManager;
	/**
	 * 获取会员可用红包列表
	 * @param 无
	 * @return 红包列表，List<Map>型
	 * map内容
	 * type_name:红包名称
	 * type_money:红包金额
	 * send_type：红包类型 (0会员发放，1:按商品发放,2:按订单发放,3:线下发放的红包)
	 * used:是否已使用，0否，1是
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String pointprice=params.get("integratedprice").toString();
			Member member = UserConext.getCurrentMember();
			Map result = new HashMap();
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
	         List<Map> storeGoodsList=storeCartManager.storeListGoodsCheckOut(ThreadContextHolder.getHttpRequest().getSession().getId());
	         Double dismoney=getDismoney(storeGoodsList);
	         List<MemberBonus> newbonus=new ArrayList<MemberBonus>();
	         if(StringUtil.equals(String.valueOf(dismoney).trim(),"0.0") && StringUtil.equals(pointprice.trim(),"0.0")){
	        	 List<MemberBonus> list=this.storeCartManager.getBonusPlatFormList(member.getMember_id());
		         Double money=0.00;
		         for(Map m : storeGoodsList){
		        	 List<StoreCartItem> goodslist = (List<StoreCartItem>)m.get("goodslist");
		        	 for (StoreCartItem storeCartItem : goodslist) {
		        		 money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
		        	 }
		         }
		         if(list.size()>0){
		        	 for (MemberBonus memberBonus : list) {
						if(memberBonus.getRelatived_store_id().equalsIgnoreCase("-1000")){
							 if(memberBonus.getMin_goods_amount()<=money){
								 newbonus.add(memberBonus);
							}
						}else{
							Double totalmoney=0.00;
							 for(Map m : storeGoodsList){
								 String[] result_store_ids=memberBonus.getRelatived_store_id().split(",");
								 for (String string : result_store_ids) {
									if(string.equalsIgnoreCase(string.valueOf((Integer)m.get("store_id")))){
										 List<StoreCartItem> goodslist = (List<StoreCartItem>)m.get("goodslist");
							        	 for (StoreCartItem storeCartItem : goodslist) {
							        		 totalmoney+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
							        	 }
									}
								}
							 }
							 if(memberBonus.getMin_goods_amount()<=totalmoney){
								 newbonus.add(memberBonus);
							 }
						}
					}
		         }
		         MemberBonus useBonus = BonusSession.getOne();
					if(newbonus.size()>0){
						for (MemberBonus bonus : newbonus) {
							bonus.setUse_status(0);
							bonus.setRelatived_bonus_name(StoreUtil.ACTIVE_BONUS_ALL+""+bonus.getMin_goods_amount()+""+StoreUtil.ACTIVE_BONUS_REDUCE+""+bonus.getBonus_money()+"_"+com.enation.framework.util.DateUtil.toString(bonus.getUse_end_date(),"yyyy-MM-dd")+""+StoreUtil.ACTIVE_BONUS_USED);
						}
					}
					if(useBonus!=null && newbonus.size()>0){
						   for (MemberBonus bonus : newbonus) {
							Integer bonusid=bonus.getBonus_id();
							if(bonusid== useBonus.getBonus_id()){
								bonus.setUse_status(1);
							}else{
								bonus.setUse_status(0);
							}
						}
					}
	         }
			Page webpage  = bonusManager.getMemberregisterBonusLists(member.getMember_id(), 0.00,12,Integer.valueOf(page),pageSize);
			
			Long totalCount = webpage.getTotalCount();
			List<Map> bonusList = (List) webpage.getResult();
			bonusList = bonusList == null ? new ArrayList() : bonusList;
			
			result.put("bonusList", newbonus);
			result.put("totalCount", totalCount);
			result.put("pageSize", pageSize);
			result.put("page", page);
			
			
			return  result;
			
		} catch (Exception e) {
			throw new 	TemplateModelException(e);	 
		}
	}
	public Double getDismoney(List<Map> storeGoodsList){
		Double dismoney=0d;
		Double bonusmoney=0d;
		Member member = UserConext.getCurrentMember();
		for(Map m : storeGoodsList){
       	 Integer store_id=(Integer) m.get("store_id");
            List<MemberBonus> listbonus=null;
            if(member!=null){
               listbonus=this.storeCartManager.getBonusList(store_id,member.getMember_id());
            }
       	    List<StoreCartItem> Activeone = new ArrayList();
            List<StoreCartItem> Activetwo= new ArrayList();
            List<StoreCartItem> Activethree= new ArrayList();
            List<StoreCartItem> customsData = new ArrayList();//完税集合
            List<StoreCartItem> bondedData = new ArrayList();//保税集合
            List<StoreCartItem> directData = new ArrayList();//直邮集合
            List<StoreCartItem> goodslist = (List<StoreCartItem>)m.get("goodslist");
            for (StoreCartItem storeCartItem : goodslist) {
           	 if(this.storeCartManager.getStore(storeCartItem.getActive_id())!=null){
        			if(Integer.valueOf(this.storeCartManager.getStore(storeCartItem.getActive_id()).getActive_type())==StoreUtil.ACTIVE_ONE){
        				Activeone.add(storeCartItem);
        			}else if(Integer.valueOf(this.storeCartManager.getStore(storeCartItem.getActive_id()).getActive_type())==StoreUtil.ACTIVE_TWO){
        				Activetwo.add(storeCartItem);
        			}else if(Integer.valueOf(this.storeCartManager.getStore(storeCartItem.getActive_id()).getActive_type())==StoreUtil.ACTIVE_THREE){
        				Activethree.add(storeCartItem);
        			}
        		}	
           	 if(storeCartItem.getTax_system().equals(DictStatus.TAXSYSTEM_CUSTOMS)){
           		 if(storeCartItem.getWholesale_volume()!=null){
    					if(storeCartItem.getWholesale_volume()!=0){
    						if(storeCartItem.getNum()<storeCartItem.getWholesale_volume() && storeCartItem.getWholesale_volume()>0){
        						customsData.add(storeCartItem);
        					  }
    					}else{
    						customsData.add(storeCartItem);
    					}
    				   }else{
    						customsData.add(storeCartItem);
	            	  }
           	 }
           	 if(storeCartItem.getTax_system().equals(DictStatus.TAXSYSTEM_BONDED)){
           		 if(storeCartItem.getWholesale_volume()!=null){
           			 if(storeCartItem.getWholesale_volume()!=0){
           				 if(storeCartItem.getNum()<storeCartItem.getWholesale_volume() && storeCartItem.getWholesale_volume()>0){
           					 bondedData.add(storeCartItem);
           				 }
           			 }else{
           				 bondedData.add(storeCartItem);
           			 }
           		 }else{
           			 bondedData.add(storeCartItem);
           		 }
           	 }
           	 if(storeCartItem.getTax_system().equals(DictStatus.TAXSYSTEM_DIRECT)){
           		 if(storeCartItem.getWholesale_volume()!=null){
           			 if(storeCartItem.getWholesale_volume()!=0){
           				 if(storeCartItem.getNum()<storeCartItem.getWholesale_volume() && storeCartItem.getWholesale_volume()>0){
           					 directData.add(storeCartItem);
           				 }
           			 }else{
           				 directData.add(storeCartItem);
           			 }
           		 }else{
           			 directData.add(storeCartItem);
           		 }
           	 }
			}
            if(listbonus!=null){
           	 if(listbonus.size()>0){
           		 if(customsData.size()>0 || directData.size()>0 || bondedData.size()>0){
           			 if(customsData.size()>0){
           				 Double money=0.0;
           				 for (StoreCartItem storeCartItem :customsData) {
                  				money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
    						}
           				 List<MemberBonus> listmemberbonus=new ArrayList<MemberBonus>();
               			 for (MemberBonus memberBonus : listbonus) {
               				 if(memberBonus.getTax_system()==DictStatus.TAXSYSTEM_CUSTOMS){
               					 if(memberBonus.getMin_goods_amount()<=money){
        								listmemberbonus.add(memberBonus);
        							}
               				 }
   						}
               			 if(listmemberbonus.size()>0){
               				 StoreBonusCompatator bonusCompatator=new StoreBonusCompatator();
               				 Collections.sort(listmemberbonus,bonusCompatator);
               				 bonusmoney+=listmemberbonus.get(listmemberbonus.size()-1).getBonus_money();
               			 }
           			 }
           			 if(directData.size()>0){
           				 Double money=0.0;
           				 for (StoreCartItem storeCartItem :directData) {
               				 money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
               			 }
           				 List<MemberBonus> listmemberbonus=new ArrayList<MemberBonus>();
               			 for (MemberBonus memberBonus : listbonus) {
               				 if(memberBonus.getTax_system()==DictStatus.TAXSYSTEM_DIRECT){
               					 if(memberBonus.getMin_goods_amount()<=money){
        								listmemberbonus.add(memberBonus);
        							}
               				 }
   						}
               			 if(listmemberbonus.size()>0){
               				 StoreBonusCompatator bonusCompatator=new StoreBonusCompatator();
               				 Collections.sort(listmemberbonus,bonusCompatator);
               				 bonusmoney+=listmemberbonus.get(listmemberbonus.size()-1).getBonus_money();
               			 }
           			 }
           			 if(bondedData.size()>0){
           				 Double money=0.0;
           				 for (StoreCartItem storeCartItem :bondedData) {
               				 money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
               			 }
           				 List<MemberBonus> listmemberbonus=new ArrayList<MemberBonus>();
               			 for (MemberBonus memberBonus : listbonus) {
               				 if(memberBonus.getTax_system()==DictStatus.TAXSYSTEM_BONDED){
               					 if(memberBonus.getMin_goods_amount()<=money){
        								listmemberbonus.add(memberBonus);
        							}
               				 }
   						}
               			 if(listmemberbonus.size()>0){
               				 StoreBonusCompatator bonusCompatator=new StoreBonusCompatator();
               				 Collections.sort(listmemberbonus,bonusCompatator);
               				 bonusmoney+=listmemberbonus.get(listmemberbonus.size()-1).getBonus_money();
               			 }
           			 }
           		 }
           	 }
            }
            if(Activeone.size()>0){
    			Double money=0.0;
    			for (StoreCartItem storeCartItem : Activeone) {
    				money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
    			}
    			List<StoreActiveItem> listitem=new ArrayList<StoreActiveItem>();
    			for (StoreCartItem storeCartItem : Activeone) {
    				List<StoreActiveItem> list=this.storeCartManager.getItemStore(storeCartItem.getActive_id());
    				if(list.size()>0){
    					for (StoreActiveItem storeActiveItem : list) {
    						Double have_price=storeActiveItem.getHave_price();
    						if(money>=have_price){
    							listitem.add(storeActiveItem);
    						}
    					}
    				}
    			}
    			if(listitem.size()>0){
    				StoreCompatator compatator=new StoreCompatator();
    				 Collections.sort(listitem,compatator);
    				 dismoney+=listitem.get(listitem.size()-1).getDismoney();	
    			}
    		}
    		if(Activetwo.size()>0){
    			Double money=0.0;
    			for (StoreCartItem storeCartItem : Activetwo) {
    				money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
    			}
    			Integer count=0;
    			for (StoreCartItem storeCartItem : Activetwo) {
    				count+=storeCartItem.getNum();
    			}
    			List<StoreActiveItem> listitem=new ArrayList<StoreActiveItem>();
    			for (StoreCartItem storeCartItem : Activetwo) {
    				List<StoreActiveItem> list=this.storeCartManager.getItemStore(storeCartItem.getActive_id());
    				if(list.size()>0){
    					for (StoreActiveItem storeActiveItem : list) {
    						Integer have_count=storeActiveItem.getHave_count();
    						if(count>=have_count){
    							listitem.add(storeActiveItem);
    						}
    					}
    				}
    			}
    			if(listitem.size()>0){
    				Integer newcount=0;
    				Double newmoney=0.0;
    				StoreCountCompatator compatator=new StoreCountCompatator();
    				 Collections.sort(listitem,compatator);
    				 if(count>listitem.get(listitem.size()-1).getHave_count()){
    					 StoreItemCompatator storeItemCompatator=new StoreItemCompatator();
    					 Collections.sort(Activetwo, storeItemCompatator);
    					 if(Activetwo.size()==1){
    						 newmoney=CurrencyUtil.mul(listitem.get(listitem.size()-1).getHave_count(),Activetwo.get(0).getPrice());
    						dismoney+=CurrencyUtil.sub(newmoney,listitem.get(listitem.size()-1).getHave_price());
    					 }else{
    						 for (int i =Activetwo.size()-1; i >=0; i--) {
        						 newcount+=Activetwo.get(i).getNum();
       						 newmoney+=CurrencyUtil.mul(Activetwo.get(i).getNum(),Activetwo.get(i).getPrice());
         						if(newcount-listitem.get(listitem.size()-1).getHave_count()>=0){
         							if(newcount-listitem.get(listitem.size()-1).getHave_count()==0){
         								dismoney+=CurrencyUtil.sub(newmoney,listitem.get(listitem.size()-1).getHave_price());
         								break;
         							}else{
         								Integer betweencount=newcount-listitem.get(listitem.size()-1).getHave_count();
         								newmoney=CurrencyUtil.sub(newmoney,CurrencyUtil.mul(betweencount,Activetwo.get(i).getPrice()));
         								dismoney+=CurrencyUtil.sub(newmoney,listitem.get(listitem.size()-1).getHave_price());
         								break;
         							}
         						}
   						}
    					 }
    				 }else{
    					dismoney+=CurrencyUtil.sub(money,listitem.get(listitem.size()-1).getHave_price());
    				 }
    			}
    		}
    		if(Activethree.size()>0){
    			Double money=0.0;
    			for (StoreCartItem storeCartItem : Activethree) {
    				money+=CurrencyUtil.mul(storeCartItem.getPrice(), storeCartItem.getNum());
    			}
    			Integer count=0;
    			for (StoreCartItem storeCartItem : Activethree) {
    				count+=storeCartItem.getNum();
    			}
    			List<StoreActiveItem> listitem=new ArrayList<StoreActiveItem>();
    			for (StoreCartItem storeCartItem : Activethree) {
    				List<StoreActiveItem> list=this.storeCartManager.getItemStore(storeCartItem.getActive_id());
    				if(list.size()>0){
    					for (StoreActiveItem storeActiveItem : list) {
    						Integer have_count=storeActiveItem.getHave_count();
    						if(count>=have_count){
    							listitem.add(storeActiveItem);
    						}
    					}
    				}
    			}
    			if(listitem.size()>0){
    				StoreCountCompatator compatator=new StoreCountCompatator();
    				 Collections.sort(listitem,compatator);
    				 dismoney+=CurrencyUtil.mul(money,(1-CurrencyUtil.div(Double.valueOf(listitem.get(listitem.size()-1).getDiscount()),10)));
    			}
    		}   
        }
		return CurrencyUtil.add(bonusmoney, dismoney);
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

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}
	
}
