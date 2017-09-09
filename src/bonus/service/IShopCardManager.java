package com.enation.app.shop.component.bonus.service;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.component.bonus.model.ShopCartType;
import com.enation.framework.database.Page;

/**
 * 购物卡方法
 * @author Chang
 */
public interface IShopCardManager {
	
	/**
	 * 获取用户的购物卡
	 * @return
	 */
	public MemberShop getMemberShop(String shop_member_id);
	/**
	 * 更新用户的购物卡
	 * @return
	 */
	public void updateMemberShop(MemberShop memberShop);
	/**
	 * 分页读取购物卡下的用户
	 * @param page
	 * @param pageSize
	 * @param shop_id
	 * @return
	 */
	public Page listMemberPage(int page ,int pageSize,int shop_id);
	/**
	 * 后台购物卡列表
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public Page shopCardPage(Map map, Integer page, Integer pageSize, String sort, String order);
	
	/**
	 * 添加一个购物卡
	 * @param storeActive
	 */
	public void add(ShopCartType shopCartType);  
	/**
	 * 根据id获得购物卡
	 * @param id
	 * @return
	 */
	public ShopCartType getShopCard(Integer shop_id);
	/**
	 * 修改一个购物卡
	 * @param storeActive
	 */
	public void edit(ShopCartType shopCartType);
	/**
	 * 发布
	 * @param type_id
	 * @return
	 */
	public boolean nowPublish(Integer shop_id); 
	/**
	 * 取消发布
	 * @param type_id
	 * @return
	 */
	public boolean unPublish(Integer shop_id); 
	/**
	 * 删除购物卡
	 * @param shop_id
	 */
	public void deleteShopCard(Integer shop_id);
	
	/**
	 * 删除关联的店铺
	 * @param id
	 */
	public void unlinkStore(Integer[] store_id, Integer shop_id);
	/**
     * 批量关联店铺
     */
	public void linkStore(Integer[] store_id, Integer shop_id);
	/**
	 * 显示可供关联的店铺
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public Page unlinkedListJson(Map map, Integer page, Integer pageSize, String sort, String order);
	/**
	 * 显示已经关联了的店铺
	 * @param map
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public Page linkedListJson(Map map, Integer page, Integer pageSize, String sort, String order);
	/**
	 * 保存索引，排序
	 * @param the_index 索引值
	 * @param gcrid es_shop_cart_relation_goods的主键id
	 */
	public void saveIndex(Integer[] the_index,Integer[] srid);
	/**
	 * 检查名称缩写是否重复
	 * @param name 名称缩写
	 */
	public int checkName(String name);
	/**
	 * 按会员级别发送购物卡
	 * @param shop_id
	 * @param lvid
	 * @param onlyEmailChecked
	 * @return 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForMemberLv(int shop_id,int lvid,int onlyEmailChecked);
	/**
	 * 按会员发送购物卡
	 * @param memberids
	 * @return 发放红包的数量
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int sendForMember(int shop_id,Integer[] memberids);
	/**
	 * 后台输入，发放会员
	 * @param shop_id
	 * @param membernames
	 * @return
	 */
	public int sendForMemberInput(int shop_id, String[] membernames);
	
	/**
	 * 后台购物卡获取使用人数和记录数
	 */
	public Integer listShopDetailCount(Integer i);
	public Integer listShopTotalCount(Integer i);
	
	/**
	 * 后台购物卡详情
	 * @param orderMap
	 * @return
	 */
	public Page listCheckShop(Map orderMap, int page, int pageSize);
}
