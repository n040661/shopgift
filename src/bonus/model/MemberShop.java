package com.enation.app.shop.component.bonus.model;

import com.enation.framework.database.NotDbField;

/**
 * 发放购物卡會員表
 * @author Administrator
 *
 */
public class MemberShop {
	private int shop_member_id;
	private int shop_type_id;//活动id
	private String shop_sn;//购物卡识别码
	private Double shop_money;//价值金额
	private Double shop_remiain_money;//剩余金额
	private Integer member_id;
	private Long used_time;//购物卡使用日期
	private Long create_time;//购物卡发放领取日期
	private Long send_start_date;// 发放开始时间
	private Long send_end_date;// 发放结束时间
	private Long use_start_date;//使用开始时间
	private Long use_end_date;//使用截至时间
	/*private Integer order_id;//购物卡在使用时写入 订单id
	private String order_sn; //购物卡在使用时写入 订单编号
*/	private String member_name; //会员用户名在使时写入，如果是按用户发放，同在发放时写入，其他領取再放入買家名稱
	private String shop_name;//购物卡名称
	private Integer sendplat;//使用地区馆  -1 所有  0俄罗斯馆  1龙江物产 2澳大利亚馆 3新西兰馆 4韩国馆 5德国馆 7融信大宗 8美洲馆
	private Integer tax_system;// 选择店铺   税制分为 100保税   101完税     103直邮  选择平台(后台发放的) 默认为 102全部 (无税制要求，所有皆可以) 
	private Integer used;//0  默认 未使用 ，1 剩余金额大于0 已使用 ，3  剩余金额为0  已用完， 2.已过期
	private String reason;//领取理由，来源    店铺为：店铺购物卡  平台为：平台购物卡
	private Integer store_id;//发放条件1：平台为0           条件2：平台+移动端 为 -1     条件3：其他为店铺 
	private Integer  send_type;//1.按店铺发放     0.平台发放 
	private String relatived_store_id;//如果发放条件为店铺,默认store_id,如果为平台。取允许领取店铺的store_id,用逗号分离，拼接存入，可能一个或者多个店铺store_id，如果没有关联，默认全平台。为-1000;
	private String relatived_shop_name;//非数据字段
	private Integer use_status;//非数据库字段
	private Double shopmoney;//非数据库字段
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Long getUsed_time() {
		return used_time;
	}
	public void setUsed_time(Long used_time) {
		this.used_time = used_time;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public Long getUse_start_date() {
		return use_start_date;
	}
	public void setUse_start_date(Long use_start_date) {
		this.use_start_date = use_start_date;
	}
	public Long getUse_end_date() {
		return use_end_date;
	}
	public void setUse_end_date(Long use_end_date) {
		this.use_end_date = use_end_date;
	}
	public Integer getSendplat() {
		return sendplat;
	}
	public void setSendplat(Integer sendplat) {
		this.sendplat = sendplat;
	}
	public Integer getTax_system() {
		return tax_system;
	}
	public void setTax_system(Integer tax_system) {
		this.tax_system = tax_system;
	}
	public Integer getUsed() {
		return used;
	}
	public void setUsed(Integer used) {
		this.used = used;
	}
	public String getReason() {
		return reason;
	}
	public String getShop_sn() {
		return shop_sn;
	}
	public void setShop_sn(String shop_sn) {
		this.shop_sn = shop_sn;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Integer getSend_type() {
		return send_type;
	}
	public void setSend_type(Integer send_type) {
		this.send_type = send_type;
	}
	public String getRelatived_store_id() {
		return relatived_store_id;
	}
	public void setRelatived_store_id(String relatived_store_id) {
		this.relatived_store_id = relatived_store_id;
	}
	public int getShop_type_id() {
		return shop_type_id;
	}
	public void setShop_type_id(int shop_type_id) {
		this.shop_type_id = shop_type_id;
	}
	public int getShop_member_id() {
		return shop_member_id;
	}
	public void setShop_member_id(int shop_member_id) {
		this.shop_member_id = shop_member_id;
	}
	public Long getSend_start_date() {
		return send_start_date;
	}
	public void setSend_start_date(Long send_start_date) {
		this.send_start_date = send_start_date;
	}
	public Long getSend_end_date() {
		return send_end_date;
	}
	public void setSend_end_date(Long send_end_date) {
		this.send_end_date = send_end_date;
	}
	public Double getShop_remiain_money() {
		return shop_remiain_money;
	}
	public void setShop_remiain_money(Double shop_remiain_money) {
		this.shop_remiain_money = shop_remiain_money;
	}
	public Double getShop_money() {
		return shop_money;
	}
	public void setShop_money(Double shop_money) {
		this.shop_money = shop_money;
	}
	@NotDbField
	public String getRelatived_shop_name() {
		return relatived_shop_name;
	}
	public void setRelatived_shop_name(String relatived_shop_name) {
		this.relatived_shop_name = relatived_shop_name;
	}
	@NotDbField
	public Integer getUse_status() {
		return use_status;
	}
	public void setUse_status(Integer use_status) {
		this.use_status = use_status;
	}
	@NotDbField
	public Double getShopmoney() {
		return shopmoney;
	}
	public void setShopmoney(Double shopmoney) {
		this.shopmoney = shopmoney;
	}
}
