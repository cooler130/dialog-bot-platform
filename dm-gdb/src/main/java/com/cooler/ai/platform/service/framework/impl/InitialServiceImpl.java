package com.cooler.ai.platform.service.framework.impl;

import com.cooler.ai.platform.service.framework.InitialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    }

    @Override
    public void initBizDatas() {
        logger.info("0.0.-----------------InitialServiceImpl，JSON数据加载入全局jsonMap中。（系统启动时加载）（level0）");
    }

}
