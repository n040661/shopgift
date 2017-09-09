package com.enation.app.shop.component.bonus.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.RegisterBonus;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 优惠卷类型管理
 * @author kingapex
 *2013-8-17下午12:02:39
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_list.html") ,
	 @Result(name="add", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_add.html"),
	 @Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_edit.html"),
	 @Result(name="bonusList", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonusList.html"), 
	 @Result(name="detail_reason", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_detail_reason.html"),
	 @Result(name="showBonusList", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/showBonusList.html"), 
	 @Result(name="addBonusList", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/addBonusList.html"), 
	 @Result(name="updateRegisterBonus", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/updateRegisterBonus.html"), 
	 @Result(name="listBonus", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/listBonus.html"),
	 @Result(name="listBonusForAll", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/listBonusForAll.html")
})
public class BonusTypeAction extends WWAction {
	private IBonusManager bonusManager;
	private IBonusTypeManager bonusTypeManager;
	private BonusType bonusType;
	private String useTimeStart;
	private String useTimeEnd;
	private String sendTimeStart;
	private String sendTimeEnd;
	private String active_start_time;
	private String active_end_time;
	private Integer is_true;
	private RegisterBonus registerBonus;
	private String name;
	private Integer sendplat;
	private int typeid;
	private Integer[] type_id;
	private String status_Json;
	private Map statusMap;
	private Map orderMap;
	private String payStatus_Json;
	private Map payStatusMap;
	private Map shipMap;
	private String ship_Json;
	private Integer limitcount;
	private Integer counttotal;
	private String keyword;
	private Integer status;
	private String start_time;
	private String end_time;
	private Integer[] id;
	private Integer activeid;
	private Integer activeid1;
	private Integer rel_id;
	private String reason;
	private Integer send_type;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getActive_start_time() {
		return active_start_time;
	}
	public void setActive_start_time(String active_start_time) {
		this.active_start_time = active_start_time;
	}
	public String getActive_end_time() {
		return active_end_time;
	}
	public void setActive_end_time(String active_end_time) {
		this.active_end_time = active_end_time;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public RegisterBonus getRegisterBonus() {
		return registerBonus;
	}
	public void setRegisterBonus(RegisterBonus registerBonus) {
		this.registerBonus = registerBonus;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String list(){
		return "list";
	}
	public String listBonus(){
		return "listBonus";
	}
	public String listBonusForAll(){
		this.activeid1=this.activeid;
		return "listBonusForAll";
	}
	public Integer getActiveid1() {
		return activeid1;
	}
	public void setActiveid1(Integer activeid1) {
		this.activeid1 = activeid1;
	}
	public String listBonusForALLJSon(){
		this.webpage = this.bonusTypeManager.listRigisterForAllJson(this.getPage(),this.getPageSize(),this.activeid1);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String listBonusJson1(){
		this.webpage = this.bonusTypeManager.listRigisterJson(activeid,this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String deleteRegister(){
		try {
				this.bonusTypeManager.deleteRegister(this.rel_id);
				this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败【"+e.getMessage()+"】");
		}
		return this.JSON_MESSAGE;
	}
	public String detailReason() {
		try {
			this.reason = java.net.URLDecoder.decode(reason, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "detail_reason";
		
	}
	//店铺优惠券使用列表
	public String bonusList(){
		if(statusMap==null){
			statusMap = new HashMap();
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			setStatus_Json(p.replace("[", "").replace("]", ""));
		}
		if(payStatusMap==null){
			payStatusMap = new HashMap();
			payStatusMap = getpPayStatusJson();
			String p= JSONArray.fromObject(payStatusMap).toString();
			payStatus_Json=p.replace("[", "").replace("]", "");
			
		}
		
		if(shipMap ==null){
			shipMap = new HashMap();
			shipMap = getShipJson();
			String p= JSONArray.fromObject(shipMap).toString();
			setShip_Json(p.replace("[", "").replace("]", ""));
			
		}
		this.limitcount=this.bonusManager.listBonusDetailCount(1);
		this.counttotal=this.bonusManager.listBonusTotalCount(1);
		return "bonusList";
	}
	public String listJson(){
		this.webpage = this.bonusTypeManager.bonusList(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String listBonusJson(){
		orderMap = new HashMap();
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("status", status);
		orderMap.put("send_type", send_type);
		this.webpage = this.bonusManager.listBonusDetail(orderMap,this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	public String listRegisterBonusJson(){
		this.webpage = this.bonusTypeManager.listRigister(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String deleteRegisterBonus(){
		try {
			if(this.bonusTypeManager.findRegisterBonus(id)>0){
				this.showErrorJson("有的活动正在进行中,请重新选择!");
			}else{
				this.bonusTypeManager.deleteRegisterBonus(id);
				this.showSuccessJson("删除成功");
			  }
		} catch (Exception e) {
			this.showErrorJson("删除失败【"+e.getMessage()+"】");
		}
		return this.JSON_MESSAGE;
	}
	public String add(){
		
		return "add";
	}
	public String updateRegisterBonus(){
		this.registerBonus=this.bonusTypeManager.lookRegisterBonus(activeid);
		return "updateRegisterBonus";
	}
	public String saveRgister(){
		try {
			this.registerBonus.setActive_now_time(DateUtil.getDateline());
			this.registerBonus.setActive_start_time(DateUtil.getDateline(active_start_time));
			this.registerBonus.setActive_end_time(DateUtil.getDateline(active_end_time));
			this.bonusTypeManager.updateRegister(registerBonus.getId(), registerBonus);
			this.showSuccessJson("保存成功");
		} catch (Throwable e) {
			this.logger.error("保存出错", e);
			this.showErrorJson("保存出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
    public String showBonusList(){
	   return "showBonusList";
    }
	public String addBonusList(){
		return "addBonusList";
	}
	public String saveBonusList(){
		try {
			this.registerBonus.setActive_now_time(DateUtil.getDateline());
			this.registerBonus.setActive_start_time(DateUtil.getDateline(active_start_time));
			this.registerBonus.setActive_end_time(DateUtil.getDateline(active_end_time));
			this.bonusTypeManager.addRigisterBonus(registerBonus);
			this.showSuccessJson("保存成功");
		} catch (Throwable e) {
			this.logger.error("保存出错", e);
			this.showErrorJson("保存出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	public String edit(){
		
		this.bonusType = this.bonusTypeManager.get(typeid);
		return "edit";
	}
	
	public String saveAdd(){
		
		if( StringUtil.isEmpty( bonusType.getRecognition() )){
			this.showErrorJson("请输入优惠卷识别码");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型中文名称");
			return this.JSON_MESSAGE;
		}
		
	/*	if( StringUtil.isEmpty( bonusType.getType_name_ru() )){
			this.showErrorJson("请输入类型俄文名称");
			return this.JSON_MESSAGE;
		}
		*/
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入人民币金额");
			return this.JSON_MESSAGE;
		}
		
		/*if( bonusType.getType_money_ru() ==null  ){
			this.showErrorJson("请输入卢布金额");
			return this.JSON_MESSAGE;
		}*/
		
//		if( StringUtil.isEmpty(useTimeStart)){
//			this.showErrorJson("请输入使用起始日期");
//			return this.JSON_MESSAGE;
//		}
//		bonusType.setUse_start_date(  DateUtil.getDateline(useTimeStart));
//		
//		if( StringUtil.isEmpty(useTimeEnd)){
//			this.showErrorJson("请输入使用结束日期");
//			return this.JSON_MESSAGE;
//		}
//		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));
		
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		bonusType.setSendplat(sendplat);
		
		try {
			this.bonusTypeManager.add(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}

		
		return this.JSON_MESSAGE;
	}
	
	
	public String saveEdit(){
		
		

		if( StringUtil.isEmpty( bonusType.getRecognition() )){
			this.showErrorJson("请输入优惠卷识别码");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型中文名称");
			return this.JSON_MESSAGE;
		}
		
		/*if( StringUtil.isEmpty( bonusType.getType_name_ru() )){
			this.showErrorJson("请输入类型俄文名称");
			return this.JSON_MESSAGE;
		}*/
		
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入人民币金额");
			return this.JSON_MESSAGE;
		}
		
		/*if( bonusType.getType_money_ru() ==null  ){
			this.showErrorJson("请输入卢布金额");
			return this.JSON_MESSAGE;
		}*/
		
		/*if( StringUtil.isEmpty(this.useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(  DateUtil.getDateline(useTimeStart));
		
		if( StringUtil.isEmpty(this.useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));*/
		
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		bonusType.setSendplat(sendplat);
		try {
			bonusTypeManager.update(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}
		
		
		return this.JSON_MESSAGE;
	}
	public String batchAddForRegister(){
		try {
			if (type_id != null && type_id.length > 0) {
					for (Integer goodsId : type_id) {
						this.bonusTypeManager.addRegisterRel(goodsId,activeid1);
					}
				}
				this.showSuccessJson("添加成功");
			} catch (RuntimeException e) {
				this.showErrorJson("添加失败");
			}
		return this.JSON_MESSAGE;
	}
	  /**
		 * 获取订单状态的json
		 * @return
		 */
		private Map getStatusJson(){
			Map orderStatus = new  HashMap();
			
			orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
			orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
			orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
			orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
			orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
			orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
			orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
			orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
			orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
			orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
			orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));
			orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));
			orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));
			orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
			return orderStatus;
		}
	private Map getpPayStatusJson(){
			
			Map pmap = new HashMap();
			pmap.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
		//	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
			pmap.put(""+OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
			pmap.put(""+OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
			pmap.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));

			return pmap;
		}
		
		private Map getShipJson(){
			Map map = new HashMap();
			map.put(""+OrderStatus.SHIP_ALLOCATION_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_NO));
			map.put(""+OrderStatus.SHIP_ALLOCATION_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_YES));
			map.put(""+OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
			map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
			map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
			map.put(""+OrderStatus.SHIP_PARTIAL_SHIPED, OrderStatus.getShipStatusText(OrderStatus.SHIP_PARTIAL_SHIPED));
			map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
			map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
			map.put(""+OrderStatus.SHIP_CHANED, OrderStatus.getShipStatusText(OrderStatus.SHIP_CHANED));
			map.put(""+OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
			return map;
		}
	public IBonusManager getBonusManager() {
			return bonusManager;
		}
		public void setBonusManager(IBonusManager bonusManager) {
			this.bonusManager = bonusManager;
		}
		public String getStatus_Json() {
			return status_Json;
		}
		public void setStatus_Json(String status_Json) {
			this.status_Json = status_Json;
		}
		public Map getStatusMap() {
			return statusMap;
		}
		public void setStatusMap(Map statusMap) {
			this.statusMap = statusMap;
		}
		public Map getOrderMap() {
			return orderMap;
		}
		public void setOrderMap(Map orderMap) {
			this.orderMap = orderMap;
		}
		public String getPayStatus_Json() {
			return payStatus_Json;
		}
		public void setPayStatus_Json(String payStatus_Json) {
			this.payStatus_Json = payStatus_Json;
		}
		public Map getPayStatusMap() {
			return payStatusMap;
		}
		public void setPayStatusMap(Map payStatusMap) {
			this.payStatusMap = payStatusMap;
		}
		public Map getShipMap() {
			return shipMap;
		}
		public void setShipMap(Map shipMap) {
			this.shipMap = shipMap;
		}
		public String getShip_Json() {
			return ship_Json;
		}
		public void setShip_Json(String ship_Json) {
			this.ship_Json = ship_Json;
		}
		public Integer getLimitcount() {
			return limitcount;
		}
		public void setLimitcount(Integer limitcount) {
			this.limitcount = limitcount;
		}
		public Integer getCounttotal() {
			return counttotal;
		}
		public void setCounttotal(Integer counttotal) {
			this.counttotal = counttotal;
		}
	public String delete(){
		
		try {
			if(this.bonusTypeManager.findBonus(type_id)>0){
				this.showErrorJson("有的活动正在进行中,请重新选择!");
			}else{
			this.bonusTypeManager.delete(type_id);
			this.showSuccessJson("删除优惠卷类型成功");
			}
		} catch (Throwable e) {
			this.logger.error("删除优惠卷类型出错", e);
			this.showErrorJson("删除优惠卷类型出错"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}
	public String disabledbonus(){
		try {
			this.bonusTypeManager.disabledbonus(type_id);
			this.showSuccessJson("置代金卷不可见成功");
		} catch (Exception e) {
			this.showErrorJson("置代金卷不可见出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	public String enablebonus(){
		try {
			this.bonusTypeManager.enablebonus(type_id);
			this.showSuccessJson("置代金卷可见成功");
		} catch (Exception e) {
			this.showErrorJson("置代金卷可见出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}


	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}


	public String getUseTimeStart() {
		return useTimeStart;
	}


	public void setUseTimeStart(String useTimeStart) {
		this.useTimeStart = useTimeStart;
	}


	public String getUseTimeEnd() {
		return useTimeEnd;
	}


	public void setUseTimeEnd(String useTimeEnd) {
		this.useTimeEnd = useTimeEnd;
	}


	public String getSendTimeStart() {
		return sendTimeStart;
	}


	public void setSendTimeStart(String sendTimeStart) {
		this.sendTimeStart = sendTimeStart;
	}


	public String getSendTimeEnd() {
		return sendTimeEnd;
	}


	public void setSendTimeEnd(String sendTimeEnd) {
		this.sendTimeEnd = sendTimeEnd;
	}


	public int getTypeid() {
		return typeid;
	}


	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}


	public BonusType getBonusType() {
		return bonusType;
	}


	public void setBonusType(BonusType bonusType) {
		this.bonusType = bonusType;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
	}

	public Integer getSendplat() {
		return sendplat;
	}

	public void setSendplat(Integer sendplat) {
		this.sendplat = sendplat;
	}
	public Integer getIs_true() {
		return is_true;
	}
	public void setIs_true(Integer is_true) {
		this.is_true = is_true;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer[] getId() {
		return id;
	}
	public void setId(Integer[] id) {
		this.id = id;
	}
	public Integer getActiveid() {
		return activeid;
	}
	public void setActiveid(Integer activeid) {
		this.activeid = activeid;
	}
	public Integer getRel_id() {
		return rel_id;
	}
	public void setRel_id(Integer rel_id) {
		this.rel_id = rel_id;
	}
	public Integer getSend_type() {
		return send_type;
	}
	public void setSend_type(Integer send_type) {
		this.send_type = send_type;
	}



}
