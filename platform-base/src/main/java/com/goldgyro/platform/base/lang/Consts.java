package com.goldgyro.platform.base.lang;


/**
 * 常用常量
 * @author wg2993
 *
 */
public interface Consts {
	String GOLDGYRO_CONFIG = "/goldgyro.properties";

	/**
	 * 默认头像
	 */
	String AVATAR = "/assets/images/ava/default.png";
	
	/**
	 * 分隔符
	 */
	String SEPARATOR = ",";

	String ROLE_ADMIN = "admin";

	int IDENTITY_STEP = 1; // 自增步进

	int TIME_MIN = 1000; // 最小时间单位, 1秒

	// 忽略值
	int IGNORE = -1;

	int ZERO = 0;

	/**
	 * REG：注册，AUTH：实名认证，FORBID：禁用，DEL：删除
	 */
	// 禁用状态
	String STATUS_CLOSED = "FORBID";

	// 删除状态
	String STATUS_REMOVED = "DEL";

	/* 状态-初始 */
	String STATUS_NORMAL = "REG";
	
	//实名认证
	String STATUS_AUTHENTICATION = "AUTH";


	/**
	 * 排序
	 */
	interface order {
		String FEATURED = "featured";
		String NEWEST = "newest";
		String HOTTEST = "hottest";
	}

	/**
	 * 附件-存储-本地
	 */
	int ATTACH_STORE_LOCAL = 0;

	/**
	 * 附件-存储-网络
	 */
	int ATTACH_STORE_NETWORK = 1;

	String SYSTEM_VERSION = "system.version";

	int VERIFY_BIND = 1;   // bind email
	int VERIFY_FORGOT = 2; // forgot password

	int VERIFY_STATUS_INIT = 0;      // 验证码-初始
	int VERIFY_STATUS_TOKEN = 1;     // 验证码-已生成token
	int VERIFY_STATUS_CERTIFIED = 2; // 验证码-已使用

	int ACTIVE_EMAIL = 1; // 邮箱激活

	int FEEDS_TYPE_POST = 1; // 动态类型 - 发布文章

	int FEATURED_COLD = -1;   // 推荐状态-不显示
	int FEATURED_DEFAULT = 0; // 推荐状态-默认
	int FEATURED_ACTIVE = 1;  // 推荐状态-推荐


	/**
	 * 未读
	 */
	int UNREAD = 0;

	/**
	 * 已读
	 */
	int READED = 1;

	int NOTIFY_EVENT_FAVOR_POST = 1; // 有人喜欢了你的文章

	int NOTIFY_EVENT_FOLLOW = 2; // 有人关注了你

	int NOTIFY_EVENT_COMMENT = 3; // 有人评论了你

	int NOTIFY_EVENT_COMMENT_REPLY = 4; // 有人回复了你

	String EMAIL_TEMPLATE_FORGOT = "forgot.vm";
	String EMAIL_TEMPLATE_BIND = "bind.vm";
}
