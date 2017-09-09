package com.enation.app.shop.component.bonus.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.RegisterBonus;
import com.enation.framework.database.Page;

/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午2:38:47
 */
public interface IBonusTypeManager {
	
	/**
	 * 添加一个红包
	 * @param bronusType
	 */
	public void add(BonusType bronusType);
	
	
	/**
	 * 修改一个红包
	 * @param bronusType
	 */
	public void update(BonusType bonusType);
	
	
	/**
	 * 删除一个红包
	 * @param bronusTypeId
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer [] bonusTypeId);
	
	
	/**
	 * 分页读取红包类型
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page, int pageSize);
	public Page listRigister(int page, int pageSize);
	public Page listRigisterJson(Integer activeid,int page, int pageSize);
	public Page listRigisterForAllJson(int page, int pageSize,Integer activitid);
	
	
	/**
	 * 获取一个红包类型
	 * @param typeid
	 * @return
	 */
	public BonusType get(int typeid);
	
	/**
	 * 根据红包卢布金额获取一个红包类型
	 * @param type_money_ru
	 * @return
	 */
	public BonusType getBonusType(Double type_money_ru);

	/**
	 * 分页读取店铺红包类型
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listStoreBouns(int page, int pageSize);


	public List<BonusType> queryForBonustype(int i);
	public void addRigisterBonus(RegisterBonus registerBonus);
	public void updateRegister(Integer id,RegisterBonus registerBonus);
	public void deleteRegisterBonus(Integer[] id);
	public void deleteRegister(Integer id);
	public int findRegisterBonus(Integer[] id);
	public int findBonus(Integer[] type_id);
	public RegisterBonus lookRegisterBonus(Integer id);
	public void addRegisterRel(Integer id,Integer activid);
	//检测优惠券识别码是否重复
	public Integer checkRecognition(String recognition);
	/**
	 * 查询代金券数量
	 * @param store_id
	 * @return
	 */
	public Integer checkBonusCount(Integer store_id);
	/**
	 * 根据店铺id查
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listStore(int page, int pageSize);
	/**
	 * 平台优惠券
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page listPlatform(int page, int pageSize);
	/**
	 * 取消发布
	 * @param type_id
	 * @return
	 */
	public boolean unPublish(Integer type_id);
	/**
	 * 后台查看优惠券
	 * @param page
	 * @param pageSize
	 * @return
	 */
	Page bonusList(int page, int pageSize);

	/**
	 * 后台对店铺发放的代金券置为不可见
	 * @param type_id
	 */
	public void disabledbonus(Integer[] type_id);
	/**
	 * 后台对店铺发放的代金券置为可见
	 * @param type_id
	 */
	public void enablebonus(Integer[] type_id);
	/**
	 * 删除关联的店铺
	 * @param id
	 */
	public void unlinkStore(Integer[] store_id, Integer type_id);
	/**
     * 批量关联店铺
     */
	public void linkStore(Integer[] store_id, Integer type_id);
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
	 * @param gcrid BonusTypeStoreRelation的主键id
	 */
	public void saveIndex(Integer[] the_index,Integer[] srid);
}
