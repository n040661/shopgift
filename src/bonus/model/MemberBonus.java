package com.enation.app.shop.component.bonus.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员红包
 * @author kingapex
 *2013-8-15下午10:05:37
 */
public class MemberBonus {
	private int bonus_id;
	private int bonus_type_id;//活动id
	private String bonus_sn;//代金券编号
	private Integer member_id;
	private Long used_time;//代金券使用日期
	private Long create_time;//发放领取日期
	private Integer order_id;//在使用时写入 订单id
	private String order_sn; //在使用时写入 订单编号
	private String member_name; //会员用户名在使时写入，如果是按用户发放，同在发放时写入，其他領取再放入買家名稱
	private String bonus_name;//优惠券名称
	private double bonus_money;//领取红包金额
	private double min_goods_amount; //最小商品金额
	private Long use_start_date;//使用开始时间
	private Long use_end_date;//使用截至时间
	private Long send_start_date;//发放开始时间
	private Long send_end_date;//发放结束时间
	private Integer sendplat;//使用地区馆  -1 所有  0俄罗斯馆  1龙江物产 2澳大利亚馆 3新西兰馆 4韩国馆 5德国馆 7融信大宗 8美洲馆
	private Integer tax_system;// 选择店铺   税制分为 100保税   101完税     103直邮  选择平台(后台发放的) 默认为 102全部 (无税制要求，所有皆可以) 
	private Integer used;//0 未使用 1 已使用 2.已过期
	private String reason;//领取理由，来源   店铺为：店铺优惠券  平台为：平台优惠券 
	private Integer store_id;//发放条件1：平台为0           条件2：平台+移动端 为 -2     条件3：其他为店铺            条件4：移动端 -1
	private Integer  send_type;//发放类型 1.按店铺发放    0.平台发放      3.店铺注册发放      4.平台注册发放        -1.移动端
	private String relatived_store_id;//如果发放条件为店铺,默认store_id,如果为平台。取允许领取店铺的store_id,用逗号分离，拼接存入，可能一个或者多个店铺store_id，如果没有关联，默认全平台。为-1000;
	private String relatived_bonus_name;//非数据字段
	private Integer use_status;//非数据库字段
	@PrimaryKeyField
	public int getBonus_id() {
		return bonus_id;
	}
	public void setBonus_id(int bonus_id) {
		this.bonus_id = bonus_id;
	}
	public int getBonus_type_id() {
		return bonus_type_id;
	}
	public void setBonus_type_id(int bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
	}
	public String getBonus_sn() {
		return bonus_sn;
	}
	public void setBonus_sn(String bonus_sn) {
		this.bonus_sn = bonus_sn;
	}
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
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public double getBonus_money() {
		return bonus_money;
	}
	public void setBonus_money(double bonus_money) {
		this.bonus_money = bonus_money;
	}
	public double getMin_goods_amount() {
		return min_goods_amount;
	}
	public void setMin_goods_amount(double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
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
	public String getBonus_name() {
		return bonus_name;
	}
	public void setBonus_name(String bonus_name) {
		this.bonus_name = bonus_name;
	}
	public String getRelatived_store_id() {
		return relatived_store_id;
	}
	public void setRelatived_store_id(String relatived_store_id) {
		this.relatived_store_id = relatived_store_id;
	}
	public Long getSend_end_date() {
		return send_end_date;
	}
	public void setSend_end_date(Long send_end_date) {
		this.send_end_date = send_end_date;
	}
	public Long getSend_start_date() {
		return send_start_date;
	}
	public void setSend_start_date(Long send_start_date) {
		this.send_start_date = send_start_date;
	}
	@NotDbField
	public String getRelatived_bonus_name() {
		return relatived_bonus_name;
	}
	public void setRelatived_bonus_name(String relatived_bonus_name) {
		this.relatived_bonus_name = relatived_bonus_name;
	}
	@NotDbField
	public Integer getUse_status() {
		return use_status;
	}
	public void setUse_status(Integer use_status) {
		this.use_status = use_status;
	}
}
