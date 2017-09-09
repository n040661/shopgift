package com.enation.app.shop.component.bonus.model;
/**
 * 購物卡指定使用店铺
 * @author Administrator
 *
 */
public class ShopCartTypeStoreRelation {
	private Integer id;
	private Integer bonus_type_id;// 优惠券活动id
	private String store_id;// 店铺id
	private Integer the_index;//索引
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBonus_type_id() {
		return bonus_type_id;
	}
	public void setBonus_type_id(Integer bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public Integer getThe_index() {
		return the_index;
	}
	public void setThe_index(Integer the_index) {
		this.the_index = the_index;
	}
}
