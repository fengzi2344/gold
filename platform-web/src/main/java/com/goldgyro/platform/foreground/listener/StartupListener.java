package com.goldgyro.platform.foreground.listener;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.goldgyro.platform.base.context.AppContext;
import com.goldgyro.platform.base.lang.Consts;
import com.goldgyro.platform.base.utils.PropertiesLoader;
import com.goldgyro.platform.core.sys.service.ConfigService;
import com.goldgyro.platform.core.sys.service.GroupService;


/**
 * @author langhsu
 *
 */
@Component
public class StartupListener implements InitializingBean, ServletContextAware {
	Logger logger = Logger.getLogger(StartupListener.class);
	
	@Autowired
	private ConfigService configService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private AppContext appContext;
	
	private ServletContext servletContext;

	/**
	 * 加载参数到系统
	 *
	 */
	private void loadParams() {
		logger.info("加载配置文件...");
		// 初始化配置文件
		try {
			PropertiesLoader p = new PropertiesLoader(Consts.GOLDGYRO_CONFIG);
			System.setProperty(Consts.SYSTEM_VERSION, p.getProperty(Consts.SYSTEM_VERSION));

		} catch (Exception e) {
			logger.error("说实话, 我也不知道啥错, 你自己看吧", e);
			System.exit(0);
		}

		logger.info("加载配置文件 OK !");
	}

	/**
	 * 加载配置信息到系统
	 * 
	 */
	/*private void loadConfig() {
        Timer timer = new Timer("loadConfig", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
				logger.info("站点信息初始化...");
            	try {
	            	List<Config> configs = configService.findAll();
	            	Map<String, String> configMap = new HashMap<>();
	
	            	if (configs.isEmpty()) {
						logger.error("配置信息加载失败,我猜,可能是没有导入初始化数据(db_init.sql)导致的");
						System.exit(1);
	            	} else {
	
						if (configs.size() < 13) {
							logger.warn("嗯哼,系统检测到'系统配置'有更新,而你好像错过了什么, 赶紧去后台'系统配置'里检查下吧!");
						}
	            		configs.forEach(conf -> {
	//						servletContext.setAttribute(conf.getKey(), conf.getValue());
							configMap.put(conf.getKey(), conf.getValue());
						});
	            	}
	
					appContext.setConfig(configMap);
	            	
	            	servletContext.setAttribute("groups", groupService.findAll(Consts.STATUS_NORMAL));
	
					logger.info("OK, mblog 加载完了");
            	}catch(Exception ex) {
            		logger.error(MessageConstant.SYS_BOOT_001,ex);
            	}
            }
        }, 3 * Consts.TIME_MIN);
    }
*/
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		loadParams();
//		loadConfig();
	}

}
