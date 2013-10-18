/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weibo.net;


/**
 * An AcessToken class contains accesstoken and tokensecret.Child class of com.weibo.net.Token.
 * 
 * @author  ZhangJie (zhangjie2@staff.sina.com.cn)
 */
public class AccessToken extends Token {
	
	public AccessToken(String rlt){
		super(rlt);
	}
	
	public AccessToken(String token , String secret){
		super(token, secret);
	}
	/*
	 * 
	 * 一、公司简介
　　大唐移动通信设备有限公司(以下简称“大唐移动”)是国务院国资委所属的大型高科技央企——大唐电信科技产业集团的核心企业，是我国拥有自主知识产权的第三代移动通信国际标准td-scdma的提出者、核心技术的开发者及产业化的推动者。大唐移动以打造“无限沟通引擎”为企业发展理念，倡导“创新、市场、诚信、责任”的企业价值观，始终致力于tdd无线通信技术(及后续技术)与应用的开发，专注于tdd无线通信解决方案与物联网、移动互联网多网协调发展的融合。大唐移动面向国内和国际市场，全力推动td-scdma及其后续演进td-lte产业化进程。

　　二、招聘职位
　　北京总部
　　数字硬件工程师
　　功放工程师
　　收发信机工程师
　　EDA工程师
　　器件可靠性工程师
　　硬件研发测试工程师
　　产品可靠性工程师
　　FPGA工程师
　　物理层协议软件工程师
　　高层协议软件工程师
　　应用软件开发工程师
　　嵌入式软件工程师
　　解决方案测试工程师
　　产品测试工程师
　　网络与关键技术测试工程师
　　软件测试工程师(测试前置与设计)
　　项目管理工程师
　　技术支持工程师
　　软件开发工程师(行业)
　　产品工程师(视频监控/网络规划)
　　产品工程师(网络软件)
　　产品工程师(软件工程)
　　解决方案经理
　　生产计划工程师
　　订单履行工程师
　　专员(培训讲师)

　　西安分公司
　　网管软件工程师
　　平台软件工程师
　　电路设计工程师
　　驱动工程师
　　协议软件工程师
　　核心网协议软件工程师
　　无线协议软件工程师
　　产品测试工程师
　　软件开发工程师(行业)

　　上海子公司
　　测试工艺开发工程师
　　软件开发工程师
　　网络优化工程师

　　大唐联仪公司
　　技术支持工程师
	 */
}