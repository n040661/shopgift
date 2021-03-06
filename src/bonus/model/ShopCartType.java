package com.enation.app.shop.component.bonus.model;

/**
 * 购物卡活动实体
 * 
 * @author Administrator
 * 
 */
public class ShopCartType {
	private Integer shop_id;// 购物卡id
	private String shop_name;// 购物卡名称
	private int send_type;// 发放类型 1.按店铺发放 0.平台发放 
	private Long send_start_date;// 发放开始时间
	private Long send_end_date;// 发放结束时间
	private Long use_start_date;// 开始使用日期
	private Long use_end_date;// 开始结束日期
	private Double shop_money;//价值金额
	private Integer sendplat;//使用地区馆  -1 所有  0俄罗斯馆  1龙江物产 2澳大利亚馆 3新西兰馆 4韩国馆 5德国馆 7融信大宗 8美洲馆
	private Integer store_id;//(使用平台)发放条件1：平台为0       条件2：平台+移动端 为 -1     条件3：其他为店铺 
	private Integer is_published;//是否启用 1.是  0否 
	private String create_time;//活动创建时间
	private int create_num;//发放总数量
	private int use_num;//使用总数量
	private String recognition;//购物卡识别码
	private Integer tax_system;// 选择店铺   税制分为 100保税   101完税     103直邮  选择平台(后台发放的) 默认为 102全部 (无税制要求，所有皆可以) 
	public Integer getShop_id() {
		return shop_id;
	}
	public void setShop_id(Integer shop_id) {
		this.shop_id = shop_id;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public int getSend_type() {
		return send_type;
	}
	public void setSend_type(int send_type) {
		this.send_type = send_type;
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
	public Double getShop_money() {
		return shop_money;
	}
	public void setShop_money(Double shop_money) {
		this.shop_money = shop_money;
	}
	public Integer getSendplat() {
		return sendplat;
	}
	public void setSendplat(Integer sendplat) {
		this.sendplat = sendplat;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Integer getIs_published() {
		return is_published;
	}
	public void setIs_published(Integer is_published) {
		this.is_published = is_published;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getCreate_num() {
		return create_num;
	}
	public void setCreate_num(int create_num) {
		this.create_num = create_num;
	}
	public int getUse_num() {
		return use_num;
	}
	public void setUse_num(int use_num) {
		this.use_num = use_num;
	}
	public String getRecognition() {
		return recognition;
	}
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	public Integer getTax_system() {
		return tax_system;
	}
	public void setTax_system(Integer tax_system) {
		this.tax_system = tax_system;
	}
}
