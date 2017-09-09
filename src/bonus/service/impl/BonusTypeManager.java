package com.enation.app.shop.component.bonus.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.BonusTypeStoreRelation;
import com.enation.app.shop.component.bonus.model.RegisterBonus;
import com.enation.app.shop.component.bonus.model.RegisterBonusRel;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午3:10:21
 */
@Component
public class BonusTypeManager extends BaseSupport  implements IBonusTypeManager {

	@Override
	public void add(BonusType bronusType) {
		this.baseDaoSupport.insert("bonus_type", bronusType);

	}

	@Override
	public void update(BonusType bronusType) {
		this.baseDaoSupport.update("bonus_type", bronusType," type_id="+bronusType.getType_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] bonusTypeId) {
		
		for(int typeid:bonusTypeId){
			this.baseDaoSupport.execute("delete from member_bonus where bonus_type_id=?", typeid);
			this.baseDaoSupport.execute("delete from bonus_type where type_id=?",typeid);
		}
	}

	@Override
	public Page list(int page, int pageSize) {
//		Integer store_id = UserConext.getCurrentMember().getStore_id();
		String sql ="select * from bonus_type order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}
	
	@Override
	public Page listStore(int page, int pageSize) {
		Integer store_id = UserConext.getCurrentMember().getStore_id();
		String sql ="select * from bonus_type where store_id ="+store_id+" order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}
	
	@Override
	public Page listPlatform(int page, int pageSize) {
		String sql ="select * from bonus_type where store_id<1 and send_type<>1 order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}
	
	@Override
	public BonusType get(int typeid) {
		String sql ="select * from bonus_type  where type_id =?";
		return (BonusType) this.baseDaoSupport.queryForObject(sql, BonusType.class, typeid);
	}

	@Override
	public BonusType getBonusType(Double type_money_ru) {
		String sql = "SELECT * FROM es_bonus_type WHERE type_money_ru = ?";
		return (BonusType) this.baseDaoSupport.queryForObject(sql, BonusType.class, type_money_ru);
	}

	@Override
	public Page listStoreBouns(int page, int pageSize) {
		String sql ="select b.* ,s.store_name from es_bonus_type b LEFT JOIN es_store s ON b.store_id = s.store_id WHERE b.store_id IS NOT NULL order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize);
	}

	/**
	 * 根据发放类型查询优惠券
	 */
	@Override
	public List<BonusType> queryForBonustype(int sentype) {
		List<BonusType> btlist=null;
		String str="select * from es_register_bonus";
		List<RegisterBonus> list=this.baseDaoSupport.queryForList(str, RegisterBonus.class);
		if(list.size()>0){
			RegisterBonus registerBonus=list.get(0);
		    List<RegisterBonusRel> bonusRels=this.baseDaoSupport.queryForList("select * from es_register_bonus_rel where registerid=?", RegisterBonusRel.class,registerBonus.getId());
		    if(bonusRels.size()>0){
		    	btlist=new ArrayList<BonusType>();
		    	for (RegisterBonusRel registerBonusRel : bonusRels) {
		    		Long cometime=DateUtil.getDateline();
				    BonusType bonusType=(BonusType) this.baseDaoSupport.queryForObject("select * from bonus_type  where send_type =? and send_start_date<=? and send_end_date>=? and type_id=?", BonusType.class,sentype,cometime,cometime,registerBonusRel.getType_id());
				    if(bonusType!=null){
				    	 btlist.add(bonusType);
				    }
				}
		    }else{
		    	btlist=new ArrayList<BonusType>();
		    }
		}else{
			btlist=new ArrayList<BonusType>();
		}
		return btlist;
	}

	@Override
	public void addRigisterBonus(RegisterBonus registerBonus) {
	    this.baseDaoSupport.insert("es_register_bonus", registerBonus);
	}

	@Override
	public Page listRigister(int page, int pageSize) {
		String sql="SELECT *,(SELECT COUNT(1) from es_register_bonus_rel WHERE registerid=b.id) AS number from es_register_bonus AS b ";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize);
	}

	@Override
	public void deleteRegisterBonus(Integer[] id) {
		String sql="delete from es_register_bonus where id=?";
		String sql1="delete from es_register_bonus_rel where registerid=?";
		for (Integer integer : id) {
			this.baseDaoSupport.execute(sql1, integer);
			this.baseDaoSupport.execute(sql, integer);
		}
		
	}

	@Override
	public int findRegisterBonus(Integer[] id) {
		Integer count=0;
		Long nowtime=DateUtil.getDateline();
		String sql="select * from es_register_bonus where id in("+StringUtil.arrayToString(id, ",")+")";
		List<RegisterBonus> list=this.baseDaoSupport.queryForList(sql, RegisterBonus.class);
		for (RegisterBonus registerBonus : list) {
			if(registerBonus.getActive_start_time()<nowtime && nowtime<registerBonus.getActive_end_time()){
				count++;
			}
		}
		return count;
	}
	@Override
	public int findBonus(Integer[] type_id) {
		Integer count=0;
		Long nowdata=DateUtil.getDateline();
		String sql="select * from bonus_type where type_id in("+StringUtil.arrayToString(type_id, ",")+")";
		List<BonusType> list=this.baseDaoSupport.queryForList(sql, BonusType.class);
		for (BonusType BonusType : list) {
//			if(BonusType.getUse_start_date()<nowdata && nowdata<BonusType.getUse_end_date()){
//				count++;
//			}
			if(BonusType.getCreate_num()>0){//如果发放数量>0不允许删除
				count++;
			}
		}
		return count;
	}

	@Override
	public RegisterBonus lookRegisterBonus(Integer id) {
	   String sql="select * from es_register_bonus where id=?";
		return (RegisterBonus) this.baseDaoSupport.queryForObject(sql, RegisterBonus.class, id);
	}

	@Override
	public void updateRegister(Integer id,RegisterBonus registerBonus) {
		String sql="update es_register_bonus set active_start_time=?,active_end_time=?,active_now_time=?,is_true=?,name=? where id=?";
		this.baseDaoSupport.execute(sql,registerBonus.getActive_start_time(),registerBonus.getActive_end_time(),registerBonus.getActive_now_time(),registerBonus.getIs_true(),registerBonus.getName(),registerBonus.getId());
	}

	@Override
	public Page listRigisterJson(Integer activeid,int page, int pageSize) {
		String sql="select * from es_register_bonus_rel AS l LEFT JOIN es_bonus_type AS e ON l.type_id=e.type_id WHERE l.registerid=? ";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize,activeid);
	}

	@Override
	public Page listRigisterForAllJson(int page, int pageSize,Integer activitid) {
		String sql="select * from es_bonus_type where type_id not in (select type_id from es_register_bonus_rel where registerid=?)";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize,activitid);
	}

	@Override
	public void addRegisterRel(Integer id, Integer activid) {
		RegisterBonusRel bonusRel=new RegisterBonusRel();
		bonusRel.setRegisterid(activid);
		bonusRel.setType_id(id);
		this.baseDaoSupport.insert("es_register_bonus_rel", bonusRel);
	}

	@Override
	public void deleteRegister(Integer id) {
		String sql="delete from es_register_bonus_rel where rel_id=?";
		this.baseDaoSupport.execute(sql, id);
	}

	@Override
	public Integer checkRecognition(String recognition) {
		String sql ="select count(1) from es_bonus_type where recognition=?";
		Integer count = daoSupport.queryForInt(sql, recognition);
		return count;
	}

	@Override
	public Integer checkBonusCount(Integer store_id) {
		Long nowdata=DateUtil.getDateline();
		String sql = "select count(1) from es_bonus_type where store_id = "+ store_id + " and use_end_date > " + nowdata;
		Integer count = daoSupport.queryForInt(sql);
		return count;
	}

	@Override
	public boolean unPublish(Integer type_id) {
		String sql = "UPDATE es_bonus_type SET is_published=0 WHERE type_id =?";
		this.daoSupport.execute(sql, type_id);
		return true;
	}
	/**
	 * 后台查看优惠券
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Override
	public Page bonusList(int page, int pageSize) {
//		String sql ="SELECT t.*,s.store_name from es_bonus_type t LEFT JOIN es_store s on t.store_id=s.store_id ORDER BY t.type_id DESC";
		//改为只显示店铺优惠券
		String sql ="SELECT t.*,s.store_name from es_bonus_type t LEFT JOIN es_store s on t.store_id=s.store_id WHERE t.send_type=1 ORDER BY t.type_id DESC";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize);
	}

	/**
	 * 后台置代金券不可见
	 */
	@Override
	public void disabledbonus(Integer[] type_id) {
		String sql = " update es_bonus_type set is_published=0 where type_id in("+StringUtil.arrayToString(type_id, ",")+")";
		this.baseDaoSupport.execute(sql);
		
	}
	/**
	 * 后台置代金券可见
	 */
	@Override
	public void enablebonus(Integer[] type_id) {
		String sql = " update es_bonus_type set is_published=1 where type_id in("+StringUtil.arrayToString(type_id, ",")+")";
		this.baseDaoSupport.execute(sql);
		
	}
	//回写Store_ids到memberBonus
	public void updateStoreIdsInMemberBonus(Integer type_id){
		String sql = "select store_id from es_bonustype_store_relation where bonus_type_id=?";
		List<Integer> list = this.daoSupport.queryForList(sql, new IntegerMapper(), type_id);
		Collections.sort(list);
		String store_ids=(list!=null && list.size()>0)?StringUtil.listToString(list, ","):"-1000";//没有关联店铺时默认为全平台。为-1000
		String updateSql = "update es_member_bonus set relatived_store_id=? where bonus_type_id=?";
		this.daoSupport.execute(updateSql, store_ids, type_id);
	}
	@Override
	public void unlinkStore(Integer[] store_id, Integer type_id) {
		for (int i = 0; i < store_id.length; i++) {
			Integer storeid = store_id[i];
			String sql = "delete from es_bonustype_store_relation WHERE bonus_type_id =? and store_id=?";
			this.daoSupport.execute(sql, type_id, storeid);
		}
		updateStoreIdsInMemberBonus(type_id);
	}

	@Override
	public void linkStore(Integer[] store_id, Integer type_id) {
		for (int i = 0; i < store_id.length; i++) {
			Integer storeid = store_id[i];
			BonusTypeStoreRelation bonusTypeStoreRelation = new BonusTypeStoreRelation();
			bonusTypeStoreRelation.setBonus_type_id(type_id);
			bonusTypeStoreRelation.setStore_id(storeid.toString());
			bonusTypeStoreRelation.setThe_index(0);
			this.daoSupport.insert("es_bonustype_store_relation", bonusTypeStoreRelation);
		}
		updateStoreIdsInMemberBonus(type_id);
	}

	@Override
	public Page unlinkedListJson(Map map, Integer page, Integer pageSize, String sort, String order) {
		Integer type_id = (Integer) map.get("type_id");
		String keyword = (String) map.get("storename");
		List<Integer> list = this.daoSupport.queryForList("select store_id from es_bonustype_store_relation where bonus_type_id=?", new IntegerMapper(), type_id);
		String ids = StringUtil.listToString(list, ",");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.* FROM es_store s ");
//		sql.append(" LEFT JOIN es_store s on s.store_id=sr.store_id ");
		sql.append(" WHERE 1=1");
//		sql.append(" and bonus_type_id="+type_id+" ");
		//disabled;	//店铺状态  0待审核  1审核通过  -1审核未通过 -2所有  2关闭中   实体店，1启用 2未启用,3已删除
		sql.append(" and s.disabled=1 ");
		//store_type;//店铺类型，1个人，2公司,0实体店
		sql.append(" AND s.store_type<>0 ");
		if(!StringUtil.isEmpty(ids)){
			sql.append(" and s.store_id not in ("+ids+")  ");
		}
		if(!StringUtil.isEmpty(keyword)&&!StringUtil.equals(keyword, "null")){
			sql.append(" and s.store_name like '%"+keyword+"%' ");
		}
		if(sort!=null&&order!=null){
            sql.append(" ORDER BY "+sort+" "+order);
        }
		Page webpage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		return webpage;
	}

	@Override
	public Page linkedListJson(Map map, Integer page, Integer pageSize, String sort, String order) {
		Integer type_id = (Integer) map.get("type_id");
		String keyword = (String) map.get("storename");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT sr.*,s.store_name FROM es_bonustype_store_relation sr ");
		sql.append(" LEFT JOIN es_store s on s.store_id=sr.store_id ");
		sql.append(" WHERE 1=1 ");
		sql.append(" and bonus_type_id="+type_id+" ");
		if(!StringUtil.isEmpty(keyword)&&!StringUtil.equals(keyword, "null")){
			sql.append(" and s.store_name like '%"+keyword+"%' ");
		}
		if(sort!=null&&order!=null){
            sql.append(" ORDER BY "+sort+" "+order);
        }
		Page webpage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		return webpage;
	}

	@Override
	public void saveIndex(Integer[] the_index, Integer[] srid) {
		if (srid != null && srid.length > 0) {
			for (int i = 0; i < srid.length; i++) {
				if (the_index[i] != null && srid[i] != null) {
					try {
						this.baseDaoSupport.execute(
										"UPDATE es_bonustype_store_relation set the_index=? WHERE id=?",
										the_index[i], srid[i]);
					} catch (Exception ex) {
					}
				}
			}
		}
	}
}
