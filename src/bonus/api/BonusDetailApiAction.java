package com.enation.app.shop.component.bonus.api;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.IMemberBonusManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
@ParentPackage("shop_default")
@Namespace("/api/shop")
@Action("bonusdetail")
@Results({
		@Result(name="edit_detail", type="freemarker",location="/themes/b2b2cv2/store/bonustype/bonus_detail_edit.html")
		})
public class BonusDetailApiAction extends WWAction{
	private IOrderManager orderManager;
	private IMemberBonusManager memberBonusManager;
	private String order_sn;
	private Order ord;
	private MemberBonus bonus;
	private String order_status;
	public MemberBonus getBonus() {
		return bonus;
	}
	public void setBonus(MemberBonus bonus) {
		this.bonus = bonus;
	}
	public String listBonusDetail(){
		this.webpage=this.memberBonusManager.listBonusDetail(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String  edit_bonus_detail() {
		try{
		bonus=this.memberBonusManager.find(order_sn);
		ord = this.orderManager.get(bonus.getOrder_id());
		order_status=OrderStatus.getOrderStatusText(ord.getStatus());
		}catch(Exception e){
			e.printStackTrace();
		}
		return "edit_detail";
	}
	public String  saveEdit() {
		
		try{
			this.memberBonusManager.update(bonus);
			this.showSuccessJson("修改成功");
		}
		catch(Exception e){
			this.showErrorJson("修改失败");
		}
		return this.JSON_MESSAGE;
	}
	
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String bonus_id) {
		this.order_sn = bonus_id;
	}
	public IMemberBonusManager getMemberBonusManager() {
		return memberBonusManager;
	}
	public void setMemberBonusManager(IMemberBonusManager memberBonusManager) {
		this.memberBonusManager = memberBonusManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public Order getOrd() {
		return ord;
	}
	public void setOrd(Order ord) {
		this.ord = ord;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

}
