package com.enation.app.shop.component.bonus.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.component.bonus.model.ShopCartType;
import com.enation.app.shop.component.bonus.model.ShopCartTypeStoreRelation;
import com.enation.app.shop.component.bonus.service.IShopCardManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

public class ShopCardManager extends BaseSupport implements IShopCardManager {

	@Override
	public MemberShop getMemberShop(String shop_member_id) {
		String sql = "select * from es_member_shop ms where sm.shop_member_id=?";
		MemberShop memberShop = (MemberShop) this.daoSupport.queryForObject(sql, MemberShop.class, shop_member_id);
		return memberShop;
	}
	
	@Override
	public void updateMemberShop(MemberShop memberShop) {
		String where = "sm.shop_member_id="+memberShop.getShop_member_id();
		this.daoSupport.update("es_member_shop", memberShop, where);;
	}
	
	@Override
	public Page listMemberPage(int page, int pageSize, int shop_id) {
		String sql="select * from es_member_shop where shop_type_id=? order by shop_member_id asc";
		Page webPage = baseDaoSupport.queryForPage(sql, page,pageSize, MemberShop.class,shop_id);
		return webPage;
	}
	
	@Override
	public Page shopCardPage(Map map, Integer page, Integer pageSize, String sort, String order) {
		String sql = createTempSql(map, sort, order);
		List list = this.daoSupport.queryForListPage(sql, page, pageSize);
		Integer count = this.daoSupport.queryForInt(" SELECT COUNT(*) FROM ("+sql+") A");
		return new Page(0, count, pageSize, list);
	}
	public String createTempSql(Map map, String sort, String order){
		String shop_name = (String) map.get("shop_name");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * from es_shop_cart_type sc ");
		sql.append(" where 1=1 ");
		if (shop_name != null && !StringUtil.isEmpty(shop_name)) {
			sql.append(" and sc.shop_name like '%" + shop_name + "%'");
		}
		sql.append(" ORDER BY " +sort + " " + order);
		return sql.toString();
	}
	
	@Override
	public void add(ShopCartType shopCartType) {
		this.daoSupport.insert("es_shop_cart_type", shopCartType);
	}

	@Override
	public ShopCartType getShopCard(Integer shop_id) {
		String sql = "SELECT * FROM es_shop_cart_type WHERE shop_id =?";
		ShopCartType getShopCard = (ShopCartType) this.daoSupport.queryForObject(sql, ShopCartType.class, shop_id);
		return getShopCard;
	}

	@Override
	public void edit(ShopCartType shopCartType) {
		String where = "shop_id = "+shopCartType.getShop_id();
		this.daoSupport.update("es_shop_cart_type", shopCartType, where);
	}

	@Override
	public boolean nowPublish(Integer shop_id) {
		String sql = "UPDATE es_shop_cart_type SET is_published=1 WHERE shop_id =?";
		this.daoSupport.execute(sql, shop_id);
		return true;
	}

	@Override
	public boolean unPublish(Integer shop_id) {
		String sql = "UPDATE es_shop_cart_type SET is_published=0 WHERE shop_id =?";
		this.daoSupport.execute(sql, shop_id);
		return true;
	}

	@Override
	public void deleteShopCard(Integer shop_id) {
		String sql = "delete from es_shop_cart_type WHERE shop_id =?";
		this.daoSupport.execute(sql, shop_id);
	}

	@Override
	public void unlinkStore(Integer[] store_id, Integer shop_id) {
		for (int i = 0; i < store_id.length; i++) {
			Integer storeid = store_id[i];
			String sql = "delete from es_shopcarttype_store_relation WHERE bonus_type_id =? and store_id=?";
			this.daoSupport.execute(sql, shop_id, storeid);
		}
		updateStoreIdsInMemberShop(shop_id);
	}

	@Override
	public void linkStore(Integer[] store_id, Integer shop_id) {
		for (int i = 0; i < store_id.length; i++) {
			Integer storeid = store_id[i];
			ShopCartTypeStoreRelation shopCartTypeStoreRelation = new ShopCartTypeStoreRelation();
			shopCartTypeStoreRelation.setBonus_type_id(shop_id);
			shopCartTypeStoreRelation.setStore_id(storeid.toString());
			shopCartTypeStoreRelation.setThe_index(0);
			this.daoSupport.insert("es_shopcarttype_store_relation", shopCartTypeStoreRelation);
		}
		updateStoreIdsInMemberShop(shop_id);
	}
	//回写Store_ids到memberShop
	public void updateStoreIdsInMemberShop (Integer shop_id){
		String sql = "select store_id from es_shopcarttype_store_relation where bonus_type_id=?";
		List<Integer> list = this.daoSupport.queryForList(sql, new IntegerMapper(), shop_id);
		Collections.sort(list);
		String store_ids=(list!=null && list.size()>0)?StringUtil.listToString(list, ","):"-1000";//没有关联店铺时默认为全平台。为-1000
		String updateSql = "update es_member_shop set relatived_store_id=? where shop_type_id=?";
		this.daoSupport.execute(updateSql, store_ids, shop_id);
	}
	@Override
	public Page unlinkedListJson(Map map, Integer page, Integer pageSize, String sort, String order) {
		Integer shop_id = (Integer) map.get("shop_id");
		String keyword = (String) map.get("storename");
		List<Integer> list = this.daoSupport.queryForList("select store_id from es_shopcarttype_store_relation where bonus_type_id=?", new IntegerMapper(), shop_id);
		String ids = StringUtil.listToString(list, ",");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.* FROM es_store s ");
//		sql.append(" LEFT JOIN es_store s on s.store_id=sr.store_id ");
		sql.append(" WHERE 1=1");
//		sql.append(" and bonus_type_id="+shop_id+" ");
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
		Integer shop_id = (Integer) map.get("shop_id");
		String keyword = (String) map.get("storename");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT sr.*,s.store_name FROM es_shopcarttype_store_relation sr ");
		sql.append(" LEFT JOIN es_store s on s.store_id=sr.store_id ");
		sql.append(" WHERE 1=1 ");
		sql.append(" and bonus_type_id="+shop_id+" ");
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
										"UPDATE es_shopcarttype_store_relation set the_index=? WHERE id=?",
										the_index[i], srid[i]);
					} catch (Exception ex) {
					}
				}
			}
		}
	}

	@Override
	public int checkName(String name) {
		String sql="select count(shop_id) as num from es_shop_cart_type where recognition='"+name+"'";
		return this.baseDaoSupport.queryForInt(sql);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int sendTypec(List<Member> mebmerList, int shop_id) {
		ShopCartType shopCartType = this.getShopCard(shop_id);
		String store_ids = this.getLinkedStoreids(shop_id);
		int count = 0;
		// 插入会员购物卡
		for (Member member : mebmerList) {
			Map memberShop = new HashMap();
			memberShop.put("shop_type_id", shop_id);
			memberShop.put("shop_sn", shopCartType.getRecognition());
			memberShop.put("shop_money", shopCartType.getShop_money());
			memberShop.put("shop_remiain_money", shopCartType.getShop_money());
			memberShop.put("member_id", member.getMember_id());
			memberShop.put("used_time", null);
			memberShop.put("create_time", DateUtil.getDateline());
			memberShop.put("use_start_date", shopCartType.getUse_start_date());
			memberShop.put("use_end_date", shopCartType.getUse_end_date());
			memberShop.put("send_start_date", shopCartType.getSend_start_date());
			memberShop.put("send_end_date", shopCartType.getSend_end_date());
			memberShop.put("member_name", member.getUname() + "[" + member.getName() + "]");
			memberShop.put("shop_name", shopCartType.getShop_name());
			memberShop.put("sendplat", shopCartType.getSendplat());
			memberShop.put("tax_system", shopCartType.getTax_system());
			memberShop.put("used", 0);
			memberShop.put("reason", "来源：平台购物卡 ");
			memberShop.put("store_id", 0);
			memberShop.put("send_type", 0);
			memberShop.put("relatived_store_id", store_ids);
			this.daoSupport.insert("es_member_shop", memberShop);
			count++;
		}
		return count;
	}
	private void increaseNum(int shop_id,int count){
		this.baseDaoSupport.execute("update es_shop_cart_type set create_num=create_num+? where shop_id=?", count, shop_id);
	}
	private String getLinkedStoreids(Integer shop_id){
		String sql = "select store_id from es_shopcarttype_store_relation where bonus_type_id=? ";
		List<Integer> list = daoSupport.queryForList(sql, new IntegerMapper(), shop_id);
		if(list!=null && list.size()>0){
			return StringUtil.listToString(list, ","); 
		}else{
			//没有关联，默认全平台。为-1000
			return "-1000";
		}
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMemberLv(int shop_id,int lvid, int onlyEmailChecked) {
		
		String sql ="select * from member where lv_id=? ";
		if(onlyEmailChecked==1){
			sql+=" and is_checked=1";
		}
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class, lvid);
		
		int count=0;
		count =this.sendTypec(mebmerList, shop_id);
		this.increaseNum(shop_id, count);
		return count;
	} 
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMember(int shop_id,Integer[] memberids) {
		if(memberids==null || memberids.length==0) return 0;
		int count =0;
		String sql ="select * from member where member_id in("+StringUtil.arrayToString(memberids, ",")+") ";
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
		count =this.sendTypec(mebmerList, shop_id);
		this.increaseNum(shop_id, count);
		return count;
	}
	@Override
	public int sendForMemberInput(int shop_id, String[] membernames) {
		if(membernames==null || membernames.length==0) return 0;
		int count =0;
		String mems = StringUtil.arrayToString(membernames,",");
		mems=mems.replaceAll("\n", "");
		mems=mems.replaceAll("\t", "");
		mems=mems.replaceAll(" ", "");
		List<String>memlist = StringUtil.stringToList(mems,",");
		StringBuffer str = new StringBuffer();
		for(String s : memlist){
			str.append("'"+s+"',");
		}
		String memnames ="";
		if(str.toString().length()>0){
			memnames = str.toString().substring(0,str.toString().length()-1);
		}
	
		ShopCartType  shopCartType = this.getShopCard(shop_id);
		String sql ="select * from member where uname in("+memnames+")";
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
		count =this.sendTypec(mebmerList, shopCartType.getShop_id());
		return count;
	}

	@Override
	public Integer listShopDetailCount(Integer send_type) {
		String sql="SELECT count(DISTINCT(member_id))from es_check_shop where send_type=?";
		Integer count=this.baseDaoSupport.queryForInt(sql, send_type);
		return count;
	}

	@Override
	public Integer listShopTotalCount(Integer send_type) {
		String sql="SELECT count(DISTINCT(member_id))from es_check_shop where send_type=?";
		Integer count=this.baseDaoSupport.queryForInt(sql, send_type);
		return count;
	}

	@Override
	public Page listCheckShop(Map map, int page, int pageSize) {
		String keyword = (String) map.get("keyword");
		String orderstate = (String) map.get("order_state");// 订单状态
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		Integer send_type = (Integer) map.get("send_type");
		StringBuffer sql = new StringBuffer();
		sql.append("select e.*,m.uname,o.`status`,o.pay_status,o.ship_status,o.create_time,o.sn,o.need_pay_money,o.currency,m.member_id from es_check_shop AS e LEFT JOIN es_order AS o ON e.order_id=o.order_id LEFT JOIN es_member AS m ON m.member_id=e.member_id LEFT JOIN es_member_shop AS b ON b.shop_member_id=e.shop_id WHERE o.parent_id IS NOT NULL AND o.disabled=0 ");
		if (!StringUtil.isEmpty(orderstate)) {
			if (orderstate.equals("wait_ship")) { // 对待发货的处理
				sql.append(" and ( ( payment_type!='cod' and payment_id!=8 and status="
						+ OrderStatus.ORDER_PAY_CONFIRM + ") ");// 非货到付款的，要已结算才能发货
				sql.append(" or ( payment_type='cod' and  status="
						+ OrderStatus.ORDER_NOT_PAY + ")) ");// 货到付款的，新订单（已确认的）就可以发货
			} else if (orderstate.equals("wait_pay")) {
				sql.append(" and ( ( payment_type!='cod' and  status="
						+ OrderStatus.ORDER_NOT_PAY + ") ");// 非货到付款的，未付款状态的可以结算
				sql.append(" or ( payment_id=8 and status!="
						+ OrderStatus.ORDER_NOT_PAY + "  and  pay_status!="
						+ OrderStatus.PAY_CONFIRM + ")");
				sql.append(" or ( payment_type='cod' and  (status="
						+ OrderStatus.ORDER_SHIP + " or status="
						+ OrderStatus.ORDER_ROG + " )  ) )");// 货到付款的要发货或收货后才能结算

			} else if (orderstate.equals("wait_rog")) {
				sql.append(" and status=" + OrderStatus.ORDER_SHIP);
			} else {
				sql.append(" and status=" + orderstate);
			}

		}
		if (!StringUtil.isEmpty(keyword)) {
				sql.append(" and (o.sn like '%" + keyword + "%'");
				sql.append(" or m.uname like '%" + keyword + "%' or e.shop_name like '%" + keyword + "%')");
		}
		if (status != null) {
			sql.append(" and o.`status`=" + status);
		}
		if (send_type != null) {
			if (send_type == 1) {//店铺发放
				sql.append(" and e.send_type=" + send_type);
			}else{//平台发放（包括pc与移动端）
				sql.append(" and e.send_type<>1");
			}
		}
		if (start_time != null && !StringUtil.isEmpty(start_time)) {
			long stime = com.enation.framework.util.DateUtil
					.getDateline(start_time + " 00:00:00");
			sql.append(" and o.create_time>" + stime);
		}
		if (end_time != null && !StringUtil.isEmpty(end_time)) {
			long etime = com.enation.framework.util.DateUtil
					.getDateline(end_time + " 23:59:59");
			sql.append(" and o.create_time<" + etime);
		}
		sql.append(" ORDER BY e.usetime DESC");
		List list1 = this.daoSupport.queryForList(sql+"");
		List <Object>list2 = this.daoSupport.queryForListPage(sql.toString(), page, pageSize);
		Page webpage = new Page();
		webpage.setParam(0, list1.size(), 1, list2);
		return webpage;
	}
}
