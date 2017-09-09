package com.enation.app.shop.component.bonus.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.RegisterBonus;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 平台优惠卷管理
 * @author changyunqing
 * 2017年9月4日08:53:21
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("bonus-typec")
@Results({
	 @Result(name="send_for_member", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_send.html"),
	 @Result(name="list_member", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_member.html"),
	 @Result(name="list_detail", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_detaillist.html"), 
	 @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_list.html"),
	 @Result(name="add", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_add.html"),
	 @Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_edit.html"),
	 @Result(name="listLinked", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_linkedstore.html"),
	 @Result(name="listToLink", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_typec_unlinkedstore.html"),
})
public class BonusTypecAction extends WWAction {
	private IBonusManager bonusManager;
	private IBonusTypeManager bonusTypeManager;
	private IMemberLvManager memberLvManager;
	private BonusType bonusType;
	private String useTimeStart;
	private String useTimeEnd;
	private String sendTimeStart;
	private String sendTimeEnd;
	private RegisterBonus registerBonus;
	private String name;
	private Integer sendplat;
	private Integer typeid;
	private Integer[] type_id;
	private String status_Json;
	private Map statusMap;
	private Map orderMap;
	private String payStatus_Json;
	private Map payStatusMap;
	private Map shipMap;
	private String ship_Json;
	private String keyword;
	private Integer status;
	private String start_time;
	private String end_time;
	private Integer tax_system;
	private Integer is_published;
	private Integer limitcount;
	private Integer counttotal;
	private String recognition;
	private int send_type;
	private List lvList;
	private Integer[] store_ids;
	private Integer[] the_index;
	private Integer[] srid;
	private Integer[] memberids;
	private String[] membernames;
	private String storename;

	public String list() {
		return "list";
	}
	public String listMember() {
		return "list_member";
	}
	public String add() {
		return "add";
	}
	public String edit() {
		this.bonusType = this.bonusTypeManager.get(typeid);
		return "edit";
	}
	/**
	 * 跳转到发放页
	 * @return
	 */
	public String send(){
		this.setLvList(this.memberLvManager.list());
		return "send_for_member";
	}
	public String listLink(){
		return "listLinked";
	}
	public String listToLink(){
		return "listToLink";
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
		this.limitcount=this.bonusManager.listBonusDetailCount(0);
		this.counttotal=this.bonusManager.listBonusTotalCount(0);
		return "list_detail";
	}
	public String linkedListJson() {
		Map map = new HashMap();
		map.put("type_id", typeid);
		map.put("storename", storename);
		this.webpage = this.bonusTypeManager.linkedListJson(map, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	public String unlinkedListJson() {
		Map map = new HashMap();
		map.put("type_id", typeid);
		map.put("storename", storename);
		this.webpage = this.bonusTypeManager.unlinkedListJson(map, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	//索引 排序
	public String saveIndex() {
		try {
			this.bonusTypeManager.saveIndex(the_index, srid);
			this.showSuccessJson("保存排序成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("保存排序失败");
		}
		return this.JSON_MESSAGE;
	}
	
	//批量关联店铺
	public String linkStore() {
		try {
			this.bonusTypeManager.linkStore(store_ids, typeid);
			this.showSuccessJson("关联商品成功");
		} catch (Throwable e) {
			this.logger.error("关联商品失败", e);
			this.showErrorJson("关联商品失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	//批量删除关联
	public String unlinkStore() {
		try {
			this.bonusTypeManager.unlinkStore(store_ids, typeid);
			this.showSuccessJson("删除关联成功");
		} catch (Throwable e) {
			this.logger.error("删除关联失败", e);
			this.showErrorJson("删除关联失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 
	 * @return
	 */
	public String sendForMemberLv(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		int lvid  = StringUtil.toInt(request.getParameter("lvid"),0);
		
		if(lvid==0){
			this.showErrorJson("必须选择会员级别");
			return this.JSON_MESSAGE;
		}
		
		int onlyEmailChecked= StringUtil.toInt(request.getParameter("onlyEmailChecked"),0);
		
		try {
			int count =bonusManager.sendForMemberLv(typeid, lvid, onlyEmailChecked);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String sendForMember(){
		try {
			int count=0;
			count =this.bonusManager.sendForMember(typeid, memberids);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	
	public String sendInputMember(){
		try {
			int count=0;
			count =this.bonusManager.sendForMemberInput(typeid, membernames);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	
	public String checkRecognition() {
		if(typeid!=null && StringUtil.equals(this.bonusTypeManager.get(typeid).getRecognition(), recognition)){
			this.showSuccessJson("该名称可用");
		}else{
			int num=this.bonusTypeManager.checkRecognition(recognition);
			if(num!=0)
				this.showErrorJson("该代金券编号已存在，请尝试其他编号");
			else this.showSuccessJson("改名称可用");
		}
		return this.JSON_MESSAGE;
	}
	
	public String unPublish() {
		boolean flag = this.bonusTypeManager.unPublish(typeid);
		if(flag){
			this.showSuccessJson("取消发布成功");
		}else{
			this.showErrorJson("取消发布出错");
		}
		return JSON_MESSAGE;
	}
	
	public String listPlatform() {
		this.webpage = this.bonusTypeManager.listPlatform(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String listMemberJson(){
		this.webpage =this.bonusManager.list(this.getPage(), this.getPageSize(), typeid);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	//平台优惠券使用列表
	public String listBonusJson() {
		orderMap = new HashMap();
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("status", status);
		orderMap.put("send_type", send_type);
		this.webpage = this.bonusManager.listBonusDetail(orderMap, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}

	public String saveAdd() {
		if (StringUtil.isEmpty(bonusType.getRecognition())) {
			this.showErrorJson("请输入代金券识别码");
			return this.JSON_MESSAGE;
		}
		Integer count = this.bonusTypeManager.checkRecognition(bonusType.getRecognition());
		if(count>0){
			this.showErrorJson("代金券编号重复");
			return JSON_MESSAGE;
		}
		
		if (StringUtil.isEmpty(bonusType.getType_name())) {
			this.showErrorJson("请输入类型中文名称");
			return this.JSON_MESSAGE;
		}

		if (bonusType.getType_money() == null) {
			this.showErrorJson("请输入人民币金额");
			return this.JSON_MESSAGE;
		}
		
		if (bonusType.getType_money().compareTo(bonusType.getMin_goods_amount())>0) {
			this.showErrorJson("优惠金额不能大于订单金额");
			return this.JSON_MESSAGE;
		}
		
		if ( StringUtil.isEmpty(useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date( DateUtil.getDateline(useTimeStart));
	
		if (StringUtil.isEmpty(useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		if (DateUtil.getDateline(useTimeStart)>=DateUtil.getDateline(useTimeEnd)) {
			this.showErrorJson("使用起始日期不能大于使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));

		if (!StringUtil.isEmpty(sendTimeStart)) {
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}

		if (!StringUtil.isEmpty(sendTimeEnd)) {
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		if (DateUtil.getDateline(sendTimeStart)>=DateUtil.getDateline(sendTimeEnd)) {
			this.showErrorJson("使用起始日期不能大于使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setCreate_time(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		bonusType.setSendplat(sendplat);
//		bonusType.setTax_system(tax_system);
		bonusType.setStore_id(bonusType.getSend_type());
		
		try {
			this.bonusTypeManager.add(bonusType);
			this.showSuccessJson("保存代金券类型成功");
		} catch (Throwable e) {
			this.logger.error("保存代金券类型出错", e);
			this.showErrorJson("保存代金券类型出错" + e.getMessage());
		}

		return this.JSON_MESSAGE;
	}

	public String saveEdit() {
		Integer[] bonusid = {bonusType.getType_id()};
		if (this.bonusTypeManager.findBonus(bonusid) > 0) {
			this.showErrorJson("代金券发放数量大于0,不允许再修改!");
			return this.JSON_MESSAGE;
		}
		
		if (StringUtil.isEmpty(bonusType.getRecognition())) {
			this.showErrorJson("请输入代金券识别码");
			return this.JSON_MESSAGE;
		}
		String recognition = this.bonusTypeManager.get(bonusType.getType_id()).getRecognition();
		if(!StringUtil.equals(bonusType.getRecognition(), recognition)){
			Integer count = this.bonusTypeManager.checkRecognition(bonusType.getRecognition());
			if(count>0){
				this.showErrorJson("代金券编号重复");
				return JSON_MESSAGE;
			}
		}
		if (StringUtil.isEmpty(bonusType.getType_name())) {
			this.showErrorJson("请输入类型中文名称");
			return this.JSON_MESSAGE;
		}

		if (bonusType.getType_money() == null) {
			this.showErrorJson("请输入人民币金额");
			return this.JSON_MESSAGE;
		}
		 
		if (bonusType.getType_money().compareTo(bonusType.getMin_goods_amount())>0) {
			this.showErrorJson("优惠金额不能大于订单金额");
			return this.JSON_MESSAGE;
		}
		
		if (StringUtil.isEmpty(this.useTimeStart)) {
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(DateUtil.getDateline(useTimeStart));

		if (StringUtil.isEmpty(this.useTimeEnd)) {
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		if (DateUtil.getDateline(useTimeStart)>=DateUtil.getDateline(useTimeEnd)) {
			this.showErrorJson("使用起始日期不能大于使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date(DateUtil.getDateline(useTimeEnd));

		if (!StringUtil.isEmpty(sendTimeStart)) {
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}

		if (!StringUtil.isEmpty(sendTimeEnd)) {
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		if (DateUtil.getDateline(sendTimeStart)>=DateUtil.getDateline(sendTimeEnd)) {
			this.showErrorJson("使用起始日期不能大于使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setSendplat(sendplat);
//		bonusType.setTax_system(tax_system);
		bonusType.setStore_id(bonusType.getSend_type());
		try {
			bonusTypeManager.update(bonusType);
			this.showSuccessJson("保存代金券类型成功");
		} catch (Throwable e) {
			this.logger.error("保存代金券类型出错", e);
			this.showErrorJson("保存代金券类型出错" + e.getMessage());
		}

		return this.JSON_MESSAGE;
	}

	public String delete() {
		try {
			if (this.bonusTypeManager.findBonus(type_id) > 0) {
//				this.showErrorJson("有的活动正在进行中,请重新选择!");
				this.showErrorJson("代金券发放数量大于0时不能删除,请重新选择!");
			} else {
				this.bonusTypeManager.delete(type_id);
				this.showSuccessJson("删除代金券成功");
			}
		} catch (Throwable e) {
			this.logger.error("删除代金券类型出错", e);
			this.showErrorJson("删除代金券类型出错" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 获取订单状态的json
	 * 
	 * @return
	 */
	private Map getStatusJson() {
		Map orderStatus = new HashMap();

		orderStatus.put("" + OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put("" + OrderStatus.ORDER_NOT_CONFIRM,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
		orderStatus.put("" + OrderStatus.ORDER_PAY_CONFIRM,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
		orderStatus.put("" + OrderStatus.ORDER_ALLOCATION_YES,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		orderStatus.put("" + OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
		orderStatus.put("" + OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put("" + OrderStatus.ORDER_CANCEL_SHIP,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put("" + OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put("" + OrderStatus.ORDER_CANCEL_PAY,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put("" + OrderStatus.ORDER_CANCELLATION,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put("" + OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));
		orderStatus.put("" + OrderStatus.ORDER_CHANGE_APPLY,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));
		orderStatus.put("" + OrderStatus.ORDER_RETURN_APPLY,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));
		orderStatus.put("" + OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		return orderStatus;
	}

	private Map getpPayStatusJson() {
		Map pmap = new HashMap();
		pmap.put("" + OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
		// pmap.put(""+OrderStatus.PAY_YES,OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		pmap.put("" + OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
		pmap.put("" + OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
		pmap.put("" + OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));
		return pmap;
	}

	private Map getShipJson() {
		Map map = new HashMap();
		map.put("" + OrderStatus.SHIP_ALLOCATION_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_NO));
		map.put("" + OrderStatus.SHIP_ALLOCATION_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_YES));
		map.put("" + OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
		map.put("" + OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put("" + OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put("" + OrderStatus.SHIP_PARTIAL_SHIPED, OrderStatus.getShipStatusText(OrderStatus.SHIP_PARTIAL_SHIPED));
		map.put("" + OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put("" + OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put("" + OrderStatus.SHIP_CHANED, OrderStatus.getShipStatusText(OrderStatus.SHIP_CHANED));
		map.put("" + OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
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
	public Integer getTypeid() {
		return typeid;
	}
	public void setTypeid(Integer typeid) {
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

	public Integer getTax_system() {
		return tax_system;
	}

	public void setTax_system(Integer tax_system) {
		this.tax_system = tax_system;
	}

	public Integer getIs_published() {
		return is_published;
	}

	public void setIs_published(Integer is_published) {
		this.is_published = is_published;
	}

	public String getRecognition() {
		return recognition;
	}

	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	public List getLvList() {
		return lvList;
	}
	public void setLvList(List lvList) {
		this.lvList = lvList;
	}
	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer[] getMemberids() {
		return memberids;
	}
	public void setMemberids(Integer[] memberids) {
		this.memberids = memberids;
	}
	public String[] getMembernames() {
		return membernames;
	}
	public void setMembernames(String[] membernames) {
		this.membernames = membernames;
	}
	public Integer[] getStore_ids() {
		return store_ids;
	}
	public void setStore_ids(Integer[] store_ids) {
		this.store_ids = store_ids;
	}
	public Integer[] getThe_index() {
		return the_index;
	}
	public void setThe_index(Integer[] the_index) {
		this.the_index = the_index;
	}
	public Integer[] getSrid() {
		return srid;
	}
	public void setSrid(Integer[] srid) {
		this.srid = srid;
	}
	public String getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename = storename;
	}
	public int getSend_type() {
		return send_type;
	}
	public void setSend_type(int send_type) {
		this.send_type = send_type;
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
}
