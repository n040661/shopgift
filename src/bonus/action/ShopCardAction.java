package com.enation.app.shop.component.bonus.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.component.bonus.model.ShopCartType;
import com.enation.app.shop.component.bonus.service.IShopCardManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("shopCard")
@Results({
	@Result(name="send_for_member", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_send.html") ,
	@Result(name="list_member", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_member.html") ,
	@Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_list.html"),
	@Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_edit.html"),
	@Result(name="add", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_add.html"),
	@Result(name="listLink", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_linked_store.html"),
	@Result(name="listToLink", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/shopcard_unlinked_store.html"),
	@Result(name="check_shop_list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/check_shop_list.html")
})

/**
 * 购物卡action
 * @author ChangYunQing
 * 2017年9月6日
 */
public class ShopCardAction extends WWAction{
	private IShopCardManager shopCardManager;
	private ShopCartType shopCartType;
	private IMemberLvManager memberLvManager;
	private String use_start_date;
	private String use_end_date;
	private String storename;
	private String shop_name;
	private String name_abbreviate;
	private Integer shop_id;
	private Integer num;
	private Integer[] store_ids;
	private Integer[] the_index;
	private Integer[] srid;
	private Integer[] memberids;
	private String[] membernames;
	private List lvList;
	private Map statusMap;
	private Map payStatusMap;
	private Map shipMap;
	private String status_Json;
	private String payStatus_Json;
	private String ship_Json;
	private Map orderMap;
	private String keyword;
	private Integer status;
	private String start_time;
	private String end_time;
	private int send_type;
	private Integer limitcount;
	private Integer counttotal;
	
	public String add(){
		return "add";
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
	
	public String checkShop(){
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
		this.limitcount=this.shopCardManager.listShopDetailCount(0);
		this.counttotal=this.shopCardManager.listShopTotalCount(0);
		return "check_shop_list";
	}
	
	/**
	 * 跳转到购物卡详情
	 * @return
	 */
	public String listCheckShopJson(){
		orderMap = new HashMap();
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("status", status);
		orderMap.put("send_type", send_type);
		this.webpage = this.shopCardManager.listCheckShop(orderMap, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	public String edit(){
		this.shopCartType = this.shopCardManager.getShopCard(shop_id);
		return "edit";
	}
	public String list(){
		return "list";
	}
	public String listMember() {
		return "list_member";
	}
	public String listLink(){
		return "listLink";
	}
	public String listToLink(){
		return "listToLink";
	}
	/**
	 * 跳转到发放页
	 * @return
	 */
	public String send(){
		this.setLvList(this.memberLvManager.list());
		return "send_for_member";
	}
	public String checkName(){
		if(shop_id!=null && StringUtil.equals(this.shopCardManager.getShopCard(shop_id).getRecognition(), name_abbreviate)){
			this.showSuccessJson("该名称可用");
		}else{
			int num=this.shopCardManager.checkName(name_abbreviate);
			if(num!=0)
				this.showErrorJson("该名称缩写已存在，请尝试其他名称");
			else this.showSuccessJson("改名称可用");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 发放购物卡
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
			int count =shopCardManager.sendForMemberLv(shop_id, lvid, onlyEmailChecked);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 发放购物卡
	 * @return
	 */
	public String sendForMember(){
		try {
			int count=0;
			count =this.shopCardManager.sendForMember(shop_id, memberids);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 发放购物卡
	 * @return
	 */
	public String sendInputMember(){
		try {
			int count=0;
			count =this.shopCardManager.sendForMemberInput(shop_id, membernames);
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (Exception e) {
			this.logger.error("发放红包出错", e);
			this.showErrorJson("发放红包出错["+e.getMessage()+"]");
		}
		return this.JSON_MESSAGE;
	}
	public String listMemberJson(){
		this.webpage =this.shopCardManager.listMemberPage(this.getPage(), this.getPageSize(), shop_id);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	public String saveAdd(){
		
		if( StringUtil.isEmpty( shopCartType.getShop_name() )){
			this.showErrorJson("请输入名称");
			return this.JSON_MESSAGE;
		}
		if(!StringUtil.isEmpty(use_start_date)){
			shopCartType.setSend_start_date(DateUtil.getTimeline(use_start_date));
			shopCartType.setUse_start_date(DateUtil.getTimeline(use_start_date));
		}
		
		if(!StringUtil.isEmpty(use_end_date)){
			if (DateUtil.getTimeline(use_start_date)>=DateUtil.getTimeline(use_end_date)) {
				this.showErrorJson("使用起始日期不能大于使用结束日期");
				return this.JSON_MESSAGE;
			}
			shopCartType.setSend_end_date(DateUtil.getTimeline(use_end_date));
			shopCartType.setUse_end_date(DateUtil.getTimeline(use_end_date));
		}
		shopCartType.setCreate_time(DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		shopCartType.setIs_published(0);
		shopCartType.setStore_id(0);
		try {
			this.shopCardManager.add(shopCartType);
			this.showSuccessJson("保存购物卡成功");
		} catch (Throwable e) {
			this.logger.error("保存购物卡出错", e);
			this.showErrorJson("保存购物卡出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	public String saveEdit(){
		
		if( StringUtil.isEmpty( shopCartType.getShop_name() )){
			this.showErrorJson("请输入名称");
			return this.JSON_MESSAGE;
		}
		if(!StringUtil.isEmpty(use_start_date)){
			shopCartType.setSend_start_date(DateUtil.getTimeline(use_start_date));
			shopCartType.setUse_start_date(DateUtil.getTimeline(use_start_date));
		}
		
		if(!StringUtil.isEmpty(use_end_date)){
			if (DateUtil.getTimeline(use_start_date)>=DateUtil.getTimeline(use_end_date)) {
				this.showErrorJson("使用起始日期不能大于使用结束日期");
				return this.JSON_MESSAGE;
			}
			shopCartType.setSend_end_date(DateUtil.getTimeline(use_end_date));
			shopCartType.setUse_end_date(DateUtil.getTimeline(use_end_date));
		}
		try {
			this.shopCardManager.edit(shopCartType);
			this.showSuccessJson("保存购物卡成功");
		} catch (Throwable e) {
			this.logger.error("保存购物卡出错", e);
			this.showErrorJson("保存购物卡出错"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	public String listJson() {
		Map map = new HashMap();
		map.put("shop_name", shop_name);
		this.webpage = this.shopCardManager.shopCardPage(map, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	public String nowPublish() {
		boolean flag = this.shopCardManager.nowPublish(shop_id);
		if(flag){
			this.showSuccessJson("发布成功");
		}else{
			this.showErrorJson("发布出错");
		}
		return this.JSON_MESSAGE;
	}
	public String unPublish() {
		boolean flag = this.shopCardManager.unPublish(shop_id);
		if(flag){
			this.showSuccessJson("取消发布成功");
		}else{
			this.showErrorJson("取消发布出错");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 移除一个购物卡
	 * @return
	 */
	public String deleteShopCard() {
		this.shopCardManager.deleteShopCard(shop_id);
		this.showSuccessJson("删除成功");
		return this.JSON_MESSAGE;
	}
	/**
	 * 移除一张购物卡
	 * @return
	 */
//	public String deleteMemberShop(){
//		try {
//			int count=0;
//			count = this.bonusManager.queryMemberBonus(bonus_id);
//			if(count==0){
//				this.showSuccessJson("删除失败，此红包已使用");
//			}else{
//				this.bonusManager.deleteMemberbyBonus(bonus_id);
//				this.showSuccessJson("删除成功");
//			}
//		} catch (Exception e) {
//			this.showErrorJson("删除失败【"+e.getMessage()+"】");
//		}
//		return this.JSON_MESSAGE;
//	}
	
	public String linkedListJson() {
		Map map = new HashMap();
		map.put("shop_id", shop_id);
		map.put("storename", storename);
		this.webpage = this.shopCardManager.linkedListJson(map, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	public String unlinkedListJson() {
		Map map = new HashMap();
		map.put("shop_id", shop_id);
		map.put("storename", storename);
//		map.put("auditStatus", auditStatus);
		this.webpage = this.shopCardManager.unlinkedListJson(map, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	//索引 排序
	public String saveIndex() {
		try {
			this.shopCardManager.saveIndex(the_index, srid);
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
			this.shopCardManager.linkStore(store_ids, shop_id);
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
			this.shopCardManager.unlinkStore(store_ids, shop_id);
			this.showSuccessJson("删除关联成功");
		} catch (Throwable e) {
			this.logger.error("删除关联失败", e);
			this.showErrorJson("删除关联失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	public IShopCardManager getShopCardManager() {
		return shopCardManager;
	}
	public void setShopCardManager(IShopCardManager shopCardManager) {
		this.shopCardManager = shopCardManager;
	}
	public ShopCartType getShopCartType() {
		return shopCartType;
	}
	public void setShopCartType(ShopCartType shopCartType) {
		this.shopCartType = shopCartType;
	}
	public String getUse_start_date() {
		return use_start_date;
	}
	public void setUse_start_date(String use_start_date) {
		this.use_start_date = use_start_date;
	}
	public String getUse_end_date() {
		return use_end_date;
	}
	public void setUse_end_date(String use_end_date) {
		this.use_end_date = use_end_date;
	}
	public String getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename = storename;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public Integer getShop_id() {
		return shop_id;
	}
	public void setShop_id(Integer shop_id) {
		this.shop_id = shop_id;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
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
	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
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
	public List getLvList() {
		return lvList;
	}
	public void setLvList(List lvList) {
		this.lvList = lvList;
	}
	public String getName_abbreviate() {
		return name_abbreviate;
	}
	public void setName_abbreviate(String name_abbreviate) {
		this.name_abbreviate = name_abbreviate;
	}

	public Map getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map statusMap) {
		this.statusMap = statusMap;
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

	public String getStatus_Json() {
		return status_Json;
	}

	public void setStatus_Json(String status_Json) {
		this.status_Json = status_Json;
	}

	public String getPayStatus_Json() {
		return payStatus_Json;
	}

	public void setPayStatus_Json(String payStatus_Json) {
		this.payStatus_Json = payStatus_Json;
	}

	public String getShip_Json() {
		return ship_Json;
	}

	public void setShip_Json(String ship_Json) {
		this.ship_Json = ship_Json;
	}

	public Map getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
