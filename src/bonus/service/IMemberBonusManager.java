package com.enation.app.shop.component.bonus.service;

import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.framework.database.Page;

public interface IMemberBonusManager {
	
	/*
	 * 查找单个MemberBonus对象
	 * @Param no 订单号
	 */
	MemberBonus find(String no);
	/*
	 * 更新MemberBonus实体
	 * @Param bonus  MemberBonus实体
	 */
	void update(MemberBonus bonus);
	/*
	 * 添加一个MemberBonus实体
	 */
	void add(MemberBonus bonus);
	
	Page listBonusDetail(int page,int pageSize);
}
