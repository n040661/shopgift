package com.enation.app.shop.component.bonus.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.model.MemberShop;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.model.CheckBonus;
import com.enation.app.shop.core.model.CheckMemberLogin;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.StringUtil;

/**
 * 红包管理
 * @author kingapex
 *2013-8-13下午3:21:18
 */
@Component
public class BonusManager extends BaseSupport implements IBonusManager {
	private IBonusTypeManager bonusTypeManager;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private IMemberManager memberManager; 
	
	
	/**
	 * 更新某个红包类型的生成数量
	 * @param typeid
	 * @param count
	 */
	private void updateNum(int typeid,int count){
		this.baseDaoSupport.execute("update bonus_type set create_num=? where type_id=?", count,typeid);
	}
	private void increaseNum(int typeid,int count){
		this.baseDaoSupport.execute("update bonus_type set create_num=create_num+? where type_id=?", count,typeid);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMemberLv(int typeid,int lvid, int onlyEmailChecked) {
		
		String sql ="select * from member where lv_id=? ";
		if(onlyEmailChecked==1){
			sql+=" and is_checked=1";
		}
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class, lvid);
		
		int count=0;
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		//插入会员红包表
//		count =this.send(mebmerList, typeid,bonusType.getType_name(),bonusType.getType_name_ru(),bonusType.getSend_type());
		count =this.sendTypec(mebmerList, typeid);
		this.increaseNum(typeid, count);
		return count;
	} 

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int send(List<Member> mebmerList,int typeid,String type_name,String type_name_ru,int bonus_type){
		int count=0;
		//插入会员红包表
		for(Member member:mebmerList){
			Map memberBonus=new HashMap();
			memberBonus.put("bonus_type_id", typeid);
			memberBonus.put("bonus_sn", count);
			memberBonus.put("member_id",member.getMember_id() );
			memberBonus.put("type_name",type_name );
			memberBonus.put("bonus_type",bonus_type );
			memberBonus.put("member_name",member.getUname()+"["+member.getName()+"]" );
			memberBonus.put("emailed",0 );
			memberBonus.put("create_time", DateUtil.getDateline());
			memberBonus.put("type_name_ru",type_name_ru );
			this.baseDaoSupport.insert("member_bonus",memberBonus);
			count++;
		}
		return count;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int sendTypec(List<Member> mebmerList, int typeid) {
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		String store_ids = this.getLinkedStoreids(typeid);
		int count = 0;
		// 插入会员红包表
		for (Member member : mebmerList) {
			Map memberBonus = new HashMap();
			memberBonus.put("bonus_type_id", typeid);
			memberBonus.put("bonus_sn", bonusType.getRecognition());
			memberBonus.put("member_id", member.getMember_id());
			memberBonus.put("create_time", DateUtil.getDateline());
			memberBonus.put("member_name", member.getUname() + "[" + member.getName() + "]");
			memberBonus.put("bonus_name", bonusType.getType_name());
			memberBonus.put("bonus_money", bonusType.getType_money());
			memberBonus.put("min_goods_amount", bonusType.getMin_goods_amount());
			memberBonus.put("use_start_date", bonusType.getUse_start_date());
			memberBonus.put("use_end_date", bonusType.getUse_end_date());
			memberBonus.put("send_start_date", bonusType.getSend_start_date());
			memberBonus.put("send_end_date", bonusType.getSend_end_date());
			memberBonus.put("sendplat", bonusType.getSendplat());
			memberBonus.put("tax_system", bonusType.getTax_system());
			memberBonus.put("used", 0);
			memberBonus.put("reason", "来源：平台优惠券 ");
			memberBonus.put("store_id", bonusType.getStore_id());
			memberBonus.put("send_type", bonusType.getSend_type());
			memberBonus.put("relatived_store_id", store_ids);
			this.daoSupport.insert("es_member_bonus", memberBonus);
			count++;
		}
		return count;
	}
	public String getLinkedStoreids(Integer typeid){
		String sql = "select store_id from es_bonustype_store_relation where bonus_type_id=? ";
		List<Integer> list = daoSupport.queryForList(sql, new IntegerMapper(), typeid);
		if(list!=null && list.size()>0){
			return StringUtil.listToString(list, ","); 
		}else{
			//没有关联，默认全平台。为-1000
			return "-1000";
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBronusManager#sendForMember(int, java.lang.Integer[])
	 */
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForMember(int typeid,Integer[] memberids) {
		if(memberids==null || memberids.length==0) return 0;
		int count =0;
		BonusType bonusType = this.bonusTypeManager.get(typeid);
//		if(bonusType.getSend_type()==1){
//			count =this.sendForMemberBystore(typeid, memberids);
//		}else{
			String sql ="select * from member where member_id in("+StringUtil.arrayToString(memberids, ",")+") ";
			List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
//		    count  = this.send(mebmerList, typeid,bonusType.getType_name(),bonusType.getType_name_ru(),bonusType.getSend_type());
			count =this.sendTypec(mebmerList, typeid);
			this.increaseNum(typeid, count);
//		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBronusManager#sendForGoods(int, java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForGoods(int typeid,Integer[] goodsids) {
		
		if(goodsids==null || goodsids.length==0) return 0;
		this.baseDaoSupport.execute("delete from bonus_goods where bonus_type_id=?", typeid);
		String sql ="select * from goods where goods_id in("+StringUtil.arrayToString(goodsids, ",")+")";
		List<Goods> goodsList = this.baseDaoSupport.queryForList(sql,Goods.class);
		for(Goods goods:goodsList){
			this.baseDaoSupport.execute("insert into bonus_goods(bonus_type_id,goods_id)values(?,?)", typeid,goods.getGoods_id());
		}
		int count = goodsList.size();
		this.updateNum(typeid, count);
		return count;
	}

	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int sendForOffLine(int typeid,int count) {
		Set<String> cardNoSet = new HashSet();
		int successCount = 0;
		int i=0;
		try {
			BonusType bronusType =	this.bonusTypeManager.get(typeid);
			String prefix = bronusType.getRecognition();
			
			int step =10;
			if(count>1000){
				step=100;
			}
			
			Set<MemberBonus> bronusSet = new HashSet();
			
			while(cardNoSet.size() < count){
				String sn =this.createSn(prefix);
				int c= this.baseDaoSupport.queryForInt("select count(0) from member_bonus where bonus_sn=?", sn);
				if( cardNoSet.contains(sn) ||c>0){
					continue;
				}
				cardNoSet.add(sn);//压入sn
				
				MemberBonus bronus = new MemberBonus();
				bronus.setBonus_type_id(typeid);
				bronus.setBonus_sn(sn);
				bronusSet.add(bronus);
				
				if(bronusSet.size() >= step) {
					this.batchCreate(bronusSet,bronusType);//批量生成
					int size = bronusSet.size();
					successCount += size;
					bronusSet.clear();
				}
				i++;
			}
			
			if(bronusSet.size() >= 0) {
				this.batchCreate(bronusSet,bronusType);//批量生成
				int size = bronusSet.size();
				successCount += size;
				bronusSet.clear();
			}
			 
		} catch (Throwable e) {
			this.logger.error("生成个优惠卷第["+i+"]出错，已生成["+successCount+"]个",e);
		}
		
		this.increaseNum(typeid, successCount);

		return successCount;
	}
	
	
	/**
	 * 批量生成优惠卷码
	 * @param bronusSet
	 */
	private void batchCreate(Set<MemberBonus> bronusSet,BonusType bronusType){
		List list =new ArrayList();
		Iterator<MemberBonus> itor=bronusSet.iterator();
		
		while(itor.hasNext()){
			MemberBonus bronus=itor.next();
			Object[] params = new Object[5];
			params[0]=bronus.getBonus_type_id();
			params[1]= bronus.getBonus_sn();
			params[2]= bronusType.getType_name();
			params[3]=bronusType.getSend_type();
			params[4]=DateUtil.getDateline();
			list.add(params);
		}
		simpleJdbcTemplate.batchUpdate("insert into es_member_bonus(bonus_type_id,bonus_sn,type_name,bonus_type,create_time,emailed)values(?,?,?,?,?,0)", list);
	}
	
	
	private String createSn(String prefix){
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append( DateUtil.toString(new Date(), "yyMM"));
		sb.append( createRandom() );
		
		return sb.toString();
	}
	
	private String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}
	
	public static void main(String[] args) {
		long i=1380211200;
			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Page list(int page, int pageSize, int typeid) {
		String sql="select * from es_member_bonus where bonus_type_id=? order by bonus_id asc";
		Page webPage = baseDaoSupport.queryForPage(sql, page,pageSize, MemberBonus.class,typeid);
		return webPage;
	}

	@Override
	public List<Map> getMemberBonusList(int memberid, Double goodsprice,Integer type) {
		String sql="select mb.bonus_id,bt.*,mb.order_sn as order_sn from es_bonus_type bt,es_member_bonus mb where "
				+" bt.type_id=mb.bonus_type_id and mb.member_id=?  and bt.min_goods_amount<=?";
		if(type==0){
			sql+="and  ISNULL(order_sn)";
		}
		long now =DateUtil.getDateline();
		sql+=" and bt.use_start_date<="+now;
		sql+=" and bt.use_end_date>="+now;
		sql+=" order by bonus_id asc";
		
		return this.daoSupport.queryForList(sql, memberid,goodsprice);
	}
	
	
	@Override
	public void delete(int bronusid) {
		this.baseDaoSupport.execute("delete from member_bonus where bonus_id=?", bronusid);
	}
	
	@Override
	public List<Map> getGoodsList(int typeid) {
		String sql="select g.goods_id,g.name from "+ this.getTableName("goods")+" g ,"+this.getTableName("bonus_goods")+" bg where bg.goods_id =g.goods_id and bg.bonus_type_id=?";
		return this.daoSupport.queryForList(sql, typeid);
	}

	
	@Override
	public String exportToExcel(int typeid) {
		try {
			
	
		BonusType  bonusType =this.bonusTypeManager.get(typeid);
		String sql="select * from member_bonus where bonus_type_id=? order by bonus_id asc";
		List<MemberBonus> list = this.baseDaoSupport.queryForList(sql,MemberBonus.class, typeid);
		
		ExcelUtil excelUtil = new ExcelUtil(); 
		
		String app_apth = StringUtil.getRootPath();

		InputStream in = new FileInputStream(new File(app_apth+"/excel/bonus.xls")) ;// FileUtil.getResourceAsStream("com/enation/app/shop/component/bonus/service/impl/bonus_list.xls");
		
		excelUtil.openModal( in );
		int i=1;
		for (MemberBonus memberBonus : list) {
			excelUtil.writeStringToCell(i, 0,memberBonus.getBonus_sn() ); //号码
			excelUtil.writeStringToCell(i, 1,bonusType.getType_money().toString()); //金额
			excelUtil.writeStringToCell(i, 2,bonusType.getType_name()); //类型名称
			long time = bonusType.getUse_end_date();
			excelUtil.writeStringToCell(i, 3, DateUtil.toString( new Date(time*1000),"yyyy年MM月dd")   ); //类型名称
			i++;
		}
		String static_server_path= SystemSetting.getStatic_server_path();
		String filePath =static_server_path+"/bouns_excel/"+typeid+".xls";
		excelUtil.writeToFile(filePath);
		return filePath;
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		}
	}
	
	@Override
	public Page pageList(int page, int pageSize, int memberid) {
	   Page pages=this.daoSupport.queryForPage("select bo.*,botype.type_id,botype.min_amount,botype.type_money,botype.use_end_date from es_member_bonus bo ,es_bonus_type botype where  bo.bonus_type_id =botype.type_id and member_id=? ", page, pageSize, memberid);
		return pages;
	}

	
	@Override
	public MemberBonus getBonus(int bounusid) {
		

		/*String sql="select mb.*,bt.type_money bonus_money,bt.type_money_ru bonus_money_ru,bt.min_goods_amount_ru min_goods_amount_ru,bt.min_goods_amount,bt.use_start_date,bt.use_end_date,bt.sendplat  from "+this.getTableName("member_bonus")+" mb , "+this.getTableName("bonus_type")
				+" bt where  bt.type_id=mb.bonus_type_id and mb.bonus_id=? ";*/
		
		MemberBonus memberBonus=new MemberBonus();
		String sql="select mb.*,bt.type_money bonus_money,bt.type_money_ru bonus_money_ru,bt.min_goods_amount_ru min_goods_amount_ru,bt.min_goods_amount,bt.use_start_date,bt.use_end_date,bt.sendplat  from "+this.getTableName("member_bonus")+" mb , "+this.getTableName("bonus_type")
				+" bt where  bt.type_id=mb.bonus_type_id and mb.bonus_id=? ";
		
		List<Map> list=new ArrayList<Map>();
		if(list.size()>0){
		   Map map=list.get(0);
		   Double min_goods_amount=(Double) map.get("min_goods_amount");
		   memberBonus.setMin_goods_amount(min_goods_amount);
		   String order_sn =(String) map.get("order_sn");
		   memberBonus.setOrder_sn(order_sn);
		   int bonus_id=(Integer) map.get("bonus_id");
		   memberBonus.setBonus_id(bonus_id);
		   int bonus_type_id=(Integer) map.get("bonus_type_id");
		   memberBonus.setBonus_type_id(bonus_type_id);
		   String bonus_sn=(String) map.get("bonus_sn");
		   memberBonus.setBonus_sn(bonus_sn);
		   Integer member_id=(Integer) map.get("menber_id");
		   memberBonus.setMember_id(member_id);
		   Long used_time=(Long) map.get("used_time");
		   memberBonus.setUsed_time(used_time);
		   Integer order_id=(Integer) map.get("order_id");
		   memberBonus.setOrder_id(order_id);
		   String member_name=(String) map.get("member_name");
		   memberBonus.setMember_name(member_name);
				    
		   String type_name=(String) map.get("ematype_nameiled");
/*		   memberBonus.setType_name(type_name);
*/		   int   bonus_type=(Integer) map.get("bonus_type");
		   /*memberBonus.setBonus_type(bonus_type);*/
		   Long  create_time=(Long) map.get("create_time");
		   memberBonus.setCreate_time(create_time);
		   Integer used=(Integer) map.get("used");
		   memberBonus.setUsed(used);
		   String type_name_ru=(String) map.get("type_name_ru");
		  /* memberBonus.setType_name_ru(type_name_ru);*/
		   double bonus_money=(Double) map.get("bonus_money");
		   memberBonus.setBonus_money(bonus_money);
		   double  bonus_money_ru=(Double) map.get("bonus_money_ru");
		 /*  memberBonus.setBonus_money_ru(bonus_money_ru);*/
		   double min_goods_amount_ru=(Double) map.get("min_goods_amount_ru");
		   /*memberBonus.setMin_goods_amount_ru(min_goods_amount_ru);*/
		   Integer sendplat=(Integer) map.get("sendplat");
		   memberBonus.setSendplat(sendplat);
		}
		return (MemberBonus) this.baseDaoSupport.queryForObject("select * from es_member_bonus where bonus_id=?", MemberBonus.class, bounusid);
	}
	
	@Override
	public MemberBonus getBonus(String sn) {
		String sql="select mb.*,bt.type_money bonus_money,bt.type_money_ru bonus_money_ru,bt.min_goods_amount,bt.use_start_date,bt.use_end_date  from "+this.getTableName("member_bonus")+" mb , "+this.getTableName("bonus_type")
				+" bt where  bt.type_id=mb.bonus_type_id and mb.bonus_sn=? ";
		
		return (MemberBonus)this.daoSupport.queryForObject(sql, MemberBonus.class, sn);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void use(int bonusid, int memberid, int orderid,String ordersn,int bonus_type_id) {
		Member member  = this.memberManager.get(memberid);
		
		String sql="update member_bonus set order_id=?,order_sn=?,member_id=?,used_time=?,member_name=?,used=1 where bonus_id=?";
		this.baseDaoSupport.execute(sql, orderid,ordersn,memberid,DateUtil.getDateline(),member.getUname()+"-"+member.getName(),bonusid);
		
		this.baseDaoSupport.execute("update es_bonus_type set use_num=use_num+1 where type_id=?",bonus_type_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void returned(int orderid) {
		String sql="update member_bonus set order_sn=null,used_time=null,order_id=null,member_name=null  where order_id=?";
		this.baseDaoSupport.execute(sql, orderid);
		
	}
	
	
	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}

	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	
	
	@Override
	public Page getMemberBonusList(int memberid, Double goodsprice,
			Integer type, int pageNo, int pageSize) {
		String sql="select mb.bonus_id,bt.*,mb.order_sn as order_sn from es_bonus_type bt,es_member_bonus mb where "
				+" bt.type_id=mb.bonus_type_id and mb.member_id=?  and bt.min_goods_amount<=? and bt.store_id is null ";
		if(type==0){
			sql+="and  ISNULL(order_sn)";
		}
		long now =DateUtil.getDateline();
		sql+=" and bt.use_start_date<="+now;
		sql+=" and bt.use_end_date>="+now;
		sql+=" order by bonus_id asc";
		
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, memberid,goodsprice);
	}
	@Override
	public Page getMemberregisterBonusLists(int memberid, Double goodsprice,
			Integer type, int pageNo, int pageSize) {
		String sql="select mb.bonus_id,rt.*,bt.*,mb.order_sn as order_sn from es_register_bonus rt,es_member_bonus mb,es_bonus_type bt,es_register_bonus_rel rbr where"
				+" rt.id=rbr.registerid and rbr.type_id=bt.type_id  and bt.type_id=mb.bonus_type_id and mb.member_id=? and bt.min_goods_amount<=? and bt.store_id is null";
		if(type==0){
			sql+=" and  ISNULL(order_sn)";
		}
		long now =DateUtil.getDateline();
		sql+=" and rt.active_start_time<="+now;
		sql+=" and rt.active_end_time>="+now;
		sql+=" order by bonus_id asc";
		Page page=new Page();
		return page;
	}
	
	/**
	 * 取消订单退还优惠券
	 */
	@Override
	public void returnmember(Member member, String sn,Integer order_id) {
		sn = sn.substring(0, sn.length()-1);
		String sql2=" select * from  es_member_bonus where member_id=? and order_sn like '"+sn+"%'";
		List<MemberBonus> membonlist = this.baseDaoSupport.queryForList(sql2, MemberBonus.class, member.getMember_id());
		if(membonlist!=null && membonlist.size()>0){
			String sql3=" update es_bonus_type set use_num=use_num-1 where type_id=? ";
			this.baseDaoSupport.execute(sql3, membonlist.get(0).getBonus_type_id());
			String sql1="update member_bonus set order_sn=null,used_time=null,order_id=null,member_name=?,used=0  where member_id=? and order_sn like '"+sn+"%'";
			this.baseDaoSupport.execute(sql1, member.getUname()+"["+member.getName()+"]",member.getMember_id());
		}	
		String str="update es_check_bonus set reason=?,cancletime=?,is_cancle=1 where order_id=?";
		this.baseDaoSupport.execute(str,"订单取消,归还优惠券",DateUtil.getDateline(),order_id);
	}
	@Override
	public int listBonusDetailCount(Integer send_type) {
		String sql="SELECT count(DISTINCT(member_id))from es_check_bonus where send_type=?";
		Integer count=this.baseDaoSupport.queryForInt(sql, send_type);
		return count;
	}
	@Override
	public int listBonusTotalCount(Integer send_type) {
		String sql="SELECT * from es_check_bonus where send_type=?";
		List<CheckBonus> list=this.baseDaoSupport.queryForList(sql, CheckBonus.class, send_type);
		return list.size();
	}
	@Override
	public Page listBonusDetail(Map map, int page, int pageSize) {
		String sql = createTempSql(map);
		List list1 = this.daoSupport.queryForList(sql);
		List <Object>list2 = this.daoSupport.queryForListPage(sql.toString(), page, pageSize);
		Page webpage = new Page();
		webpage.setParam(0, list1.size(), 1, list2);
		return webpage;
	}

	private String createTempSql(Map map) {
		String keyword = (String) map.get("keyword");
		String orderstate = (String) map.get("order_state");// 订单状态
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		Integer send_type = (Integer) map.get("send_type");
		StringBuffer sql = new StringBuffer();
		sql.append("select e.*,m.uname,o.`status`,o.pay_status,o.ship_status,o.create_time,o.sn,o.need_pay_money,o.currency,m.member_id from es_check_bonus AS e LEFT JOIN es_order AS o ON e.order_id=o.order_id LEFT JOIN es_member AS m ON m.member_id=e.member_id LEFT JOIN es_member_bonus AS b ON b.bonus_id=e.bonus_id WHERE o.parent_id IS NOT NULL AND o.disabled=0 ");
		if (!StringUtil.isEmpty(orderstate)) {
			if (orderstate.equals("wait_ship")) { // 对待发货的处理
				sql.append(" and ( ( payment_type!='cod' and payment_id!=8  and  status="
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
				sql.append(" or m.uname like '%" + keyword + "%' or e.bonus_name like '%" + keyword + "%')");
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
		return sql.toString();
	}
	/**
	 * 后台单独发放店铺优惠券给指定用户
	 */
	@Override
	public int sendForMemberBystore(int typeid, Integer[] memberids) {
		if(memberids==null || memberids.length==0) return 0;
		String sql ="select * from member where member_id in("+StringUtil.arrayToString(memberids, ",")+") ";
		List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
		int count =0;
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		count = this.sendMember(bonusType,mebmerList);
		return count;
	}
	/**
	 * 发放优惠券给用户
	 * @param bonus
	 * @param mlist
	 * @return
	 */
	public int sendMember(BonusType bonus,List<Member> mlist){
		int count=0;
		for(Member member:mlist){
			int count1 = this.baseDaoSupport.queryForInt("select count(0) from member_bonus where bonus_type_id=? and member_id=? and store_id=?", bonus.getType_id(),member.getMember_id(),bonus.getStore_id());
			if(count1==0){
				this.baseDaoSupport.execute("INSERT INTO es_member_bonus ( bonus_type_id, bonus_sn, member_id, create_time, bonus_name, bonus_money, min_goods_amount, use_start_date, use_end_date, sendplat, tax_system, reason, store_id,send_type,member_name) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					bonus.getType_id(),bonus.getRecognition(),member.getMember_id(),DateUtil.getDateline(),bonus.getType_name(),bonus.getType_money(),bonus.getMin_goods_amount(),bonus.getUse_start_date(),bonus.getUse_end_date(),bonus.getSendplat(),bonus.getTax_system(),"店铺优惠券",bonus.getStore_id(),bonus.getSend_type(),member.getUname());
				String sql = "UPDATE es_bonus_type SET create_num=create_num+1 WHERE store_id="+bonus.getStore_id()+" AND type_id="+bonus.getType_id();
				this.baseDaoSupport.execute(sql);
				count++;
			}
		}
		return count;
	}
	@Override
	public int sendForMemberInput(int typeid, String[] membernames) {
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
	
		BonusType bonusType = this.bonusTypeManager.get(typeid);
//		if(bonusType.getSend_type()==1){
			String sql ="select * from member where uname in("+memnames+")";
			List<Member> mebmerList  =this.baseDaoSupport.queryForList(sql,Member.class);
//			count = this.sendMember(bonusType,mebmerList);
			count =this.sendTypec(mebmerList, bonusType.getType_id());
//		}
		return count;
	}
	/**
	 * 后台删除已发放优惠券且未下订单的会员
	 */
	@Override
	public void deleteMemberbyBonus(Integer[] bonus_id) {
		String bounsid = StringUtil.arrayToString(bonus_id, ",");
		StringBuffer sql1 = new StringBuffer();
		sql1.append(" select * from es_member_bonus where bonus_id in("+bounsid+")");
		sql1.append(" and order_sn IS NULL ");
		List<MemberBonus> mblist = this.baseDaoSupport.queryForList(sql1.toString(), MemberBonus.class);
		if(mblist.size()>0){
			for(MemberBonus mb : mblist){
				StringBuffer sql = new StringBuffer();
				sql.append(" delete from es_member_bonus where bonus_id="+mb.getBonus_id());
				sql.append(" and order_sn IS NULL ");
				this.baseDaoSupport.execute(sql.toString());
				StringBuffer sql2 = new StringBuffer();
				sql2.append(" UPDATE es_bonus_type SET create_num=create_num-1 ");
				sql2.append(" where type_id=? ");
				this.baseDaoSupport.execute(sql2.toString(),mb.getBonus_type_id());
			}
		}
	}
	@Override
	public int queryMemberBonus(Integer[] bonus_id) {
		String bounsid = StringUtil.arrayToString(bonus_id, ",");
		StringBuffer sql1 = new StringBuffer();
		sql1.append(" select * from es_member_bonus where bonus_id in("+bounsid+")");
		sql1.append(" and order_sn IS NULL ");
		List<MemberBonus> mblist = this.baseDaoSupport.queryForList(sql1.toString(), MemberBonus.class);
		if(mblist!=null){
			return mblist.size();
		}else{
			return 0;
		}
	}
	@Override
	public MemberShop getMemeberShop(Integer shop_id) {
		return (MemberShop) this.baseDaoSupport.queryForObject("select * from es_member_shop where shop_member_id=?", MemberShop.class, shop_id);

	}
	
}
