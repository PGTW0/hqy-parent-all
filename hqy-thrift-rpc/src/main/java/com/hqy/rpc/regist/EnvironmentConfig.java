package com.hqy.rpc.regist;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.util.MathUtil;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统初始化的时候 记录下当前环境信息<br>
 * @author qy
 * @date 2021-08-16 14:23
 */
@Slf4j
@Component
public class EnvironmentConfig implements InitializingBean {

    /**
     * 开发环境
     */
    public static final String ENV_DEV = "dev";

    /**
     * 测试环境
     */
    public static final String ENV_TEST = "test";

    /**
     * UAT 预生产环境
     */
    public static final String ENV_UAT_PRE_PROD = "uat";

    /**
     * 生产环境
     */
    public static final String ENV_PROD = "prod";

    /**
     * 标记 rpc 功能弱化的节点，不对外提供rpc服务的独立的节点(可以少一些rpc的io线程)。
     * 由框架自身自动维护，业务代码不用设置这个的值....
     */
    public static boolean FLAG_RPC_REDUCED_SERVICE = false;

    /**
     * IO 密集型rpc服务？默认false; 如果true，rpc节点处理线程再翻倍...
     */
    public static boolean FLAG_IO_INTENSIVE_RPC_SERVICE = false;

    @Value("${spring.profiles.active}")
    private String env;

    private static final EnvironmentConfig NONE_SPRING_BEAN_INSTANCE = new EnvironmentConfig();


    public static EnvironmentConfig getInstance() {
        try {
            EnvironmentConfig environmentConfig = SpringContextHolder.getBean(EnvironmentConfig.class);
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn() && MathUtil.mathIf(1000, 8)) {
                log.debug("getInstance >> env = {}", environmentConfig.env);
            }
            return environmentConfig;
        } catch (Exception e) {
            if (MathUtil.mathIf(1, 100)) {
                log.warn("### 当前节点上下文没有配置EnvironmentConfig 作为Spring Bean:{}", e.getMessage());
                if (StringUtils.isBlank(NONE_SPRING_BEAN_INSTANCE.env)) {
                    NONE_SPRING_BEAN_INSTANCE.env = ENV_DEV;
                }
            }
            return NONE_SPRING_BEAN_INSTANCE;
        }
    }

    public String getEnvironment() {
        if (StringUtils.isBlank(env)) {
            env = ENV_DEV;
        } else if ("${env}".equalsIgnoreCase(env)) {
            env = ENV_DEV;
        }
        //兼容大小写
        return env.toLowerCase();
    }

    public void setEnvironment(String env) {
        if (StringUtils.isBlank(env)) {
            env = ENV_DEV;
        }
        this.env = env;
    }

    /**
     * 是否是开发或者测试环境
     * @return
     */
    public boolean isDevTestEnvironment() {
        return ENV_TEST.equalsIgnoreCase(env) || ENV_DEV.equalsIgnoreCase(env);
    }


    /**
     * 是否开发环境？？
     * @return
     */
    public boolean isDevEnvironment() {
        return ENV_DEV.equalsIgnoreCase(env);
    }

    /**
     * 是否是测试环境
     * @return
     */
    public boolean isTestEnvironment() {
        return ENV_TEST.equalsIgnoreCase(env);
    }

    /**
     * 是否是UAT预生产环境
     * @return
     */
    public boolean isUatEnvironment() {
        return ENV_UAT_PRE_PROD.equalsIgnoreCase(env);
    }

    /**
     * 是否是生产环境
     * @return
     */
    public boolean isProdEnvironment() {
        return ENV_PROD.equalsIgnoreCase(env);
    }


    @Override
    public void afterPropertiesSet() {
        if("${env}".equalsIgnoreCase(env) || ENV_DEV.equalsIgnoreCase(env) || StringUtils.isEmpty(env)){
            //兼容dev
            env = ENV_DEV;
        }
    }

    /**
     * @return 是否允许RPC直连
     */
    public boolean enableRpcDirect() {
        return false;
    }


    /**
     * 是否采用rpc接口采集
     * @return
     */
    public boolean isRPCCollection() {
        return CommonSwitcher.ENABLE_THRIFT_RPC_COLLECTION.isOn();
    }

    /**
     * 是否采用调用链持久化
     * @return
     */
    public boolean isRPCCallChainPersistence() {
        return CommonSwitcher.ENABLE_THRIFT_RPC_CALL_CHAIN_PERSISTENCE.isOn();
    }
}
