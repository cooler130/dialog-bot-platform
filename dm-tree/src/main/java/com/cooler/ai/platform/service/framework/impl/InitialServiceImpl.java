package com.cooler.ai.platform.service.framework.impl;

import com.alibaba.fastjson.JSON;
import com.cooler.ai.platform.model.EntityConstant;
import com.cooler.ai.platform.util.ConfigReaderUtils;
import com.cooler.ai.platform.service.framework.InitialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("initialService")
public class InitialServiceImpl implements InitialService {

    private static Logger logger = LoggerFactory.getLogger(InitialServiceImpl.class);

    InitialServiceImpl(){
        initSysParams();
        initBizDatas();
    }

    @Override
    public void initSysParams() {
        logger.info("0.1.-----------------系统参数初始化（level1）");
        String systemDataJSON = ConfigReaderUtils.readConfig(getClass().getResourceAsStream("/data/SystemData.json"));      //起始以后这个SystemData里面的数据也可以放到数据库中，跟DMData合并
        Map jsonMap = (Map<String, String>)JSON.parse(systemDataJSON);
        EntityConstant.globalMap.putAll(jsonMap);
    }

    @Override
    public void initBizDatas() {
        logger.info("0.0.-----------------InitialServiceImpl，JSON数据加载入全局jsonMap中。（系统启动时加载）（level0）");
        String dmDataJSON = ConfigReaderUtils.readConfig(getClass().getResourceAsStream("/data/DMData.json"));
        Map jsonMap = (Map<String, String>)JSON.parse(dmDataJSON);
        EntityConstant.globalMap.putAll(jsonMap);
    }

}
