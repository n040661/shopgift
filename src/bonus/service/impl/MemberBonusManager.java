package com.enation.app.shop.component.bonus.service.impl;

import java.util.HashMap;
import java.util.Map;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.IMemberBonusManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

public class MemberBonusManager extends BaseSupport<MemberBonus> implements IMemberBonusManager{
	
	private IStoreMemberManager storeMemberManager;
	
	@Override
	public MemberBonus find(String no) {
		String sql="select * from es_member_bonus where order_sn=?";
		MemberBonus bonus=this.daoSupport.queryForObject(sql, MemberBonus.class, no);
		return bonus;
	}

	@Override
	public void update(MemberBonus bonus) {
		Map where = new HashMap();
		where.put("order_sn", bonus.getOrder_sn());
		this.baseDaoSupport.update("es_member_bonus", bonus, where);
	}

	@Override
	public void add(MemberBonus bonus) {
		this.baseDaoSupport.insert("es_member_bonus", bonus);
		
	}

	@Override
	public Page listBonusDetail(int page, int pageSize) {
		StoreMember member= this.storeMemberManager.getStoreMember();
		String sql="select o.status,o.order_amount,m.* from es_order o JOIN es_member_bonus m on o.order_id=m.order_id where m.used = 1 and m.store_id = "+member.getStore_id();
		return this.daoSupport.queryForPage(sql, page, pageSize);
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}