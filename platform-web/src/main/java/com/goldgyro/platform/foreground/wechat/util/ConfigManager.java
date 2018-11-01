package com.goldgyro.platform.foreground.wechat.util;
import java.io.InputStream;
import java.util.*;

/**
 * 读取配置文件的类 单例类
 * @author Administrator
 *
 */
public class ConfigManager {
    // 属性文件命名
    private Properties m_props = null;
    private static  Map<String,String> configMap;
    private static  ConfigManager m_instance = null;
    private static   Properties props = null;
    private ConfigManager() {
        m_props = new Properties();
        configMap = new HashMap<String,String>();
        try {
            props = System.getProperties(); //获取系统属性
            m_props.load(getInputStream());
            getSysConfigMsg();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized static ConfigManager getInstance() {
        if(m_instance == null){
            m_instance = new ConfigManager();
        }
        return m_instance;
    }

    public InputStream getInputStream() {
        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream("global.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    public Map<String,String> getSysConfigMsg(){
        Set<Object> keyset = m_props.keySet();
        Iterator<Object> it = keyset.iterator();
        while(it.hasNext()){
            String nextkey = it.next().toString();
            configMap.put(nextkey,getConfigItem(nextkey));
        }
        return configMap;
    }

    public String getConfigItem(String name) {
        String val = m_props.getProperty(name).trim();
        if("fileSavePath".equals(name)){
            if(props.getProperty("os.name").startsWith("Windows")){
                val  =  val.split("#")[0].toString().trim();
            }else{
                val  =  val.split("#")[1].toString().trim();
            }
        }

        return val;

    }
    public Map<String,String> getConfigMap(){
        return configMap;
    }

}
