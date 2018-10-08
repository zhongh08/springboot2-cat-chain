package com.dachen.cat;

public class CatMsgConstants {

    public static final String CROSS_CONSUMER = "RibbonCall";

    /**
     * Cross报表中的数据标识
     */
    public static final String CROSS_SERVER = "RibbonService";

    public static final String PROVIDER_APPLICATION_NAME = "serverApplicationName";

    public static final String CONSUMER_CALL_SERVER = "RibbonCall.server";

    public static final String CONSUMER_CALL_APP = "RibbonCall.app";

    public static final String CONSUMER_CALL_PORT = "RibbonCall.port";

    public static final String PROVIDER_CALL_SERVER = "RibbonService.client";

    /**
     * 客户端调用标识
     */
    public static final String PROVIDER_CALL_APP = "RibbonService.app";

    public static final String FORK_MESSAGE_ID = "m_forkedMessageId";

    public static final String FORK_ROOT_MESSAGE_ID = "m_rootMessageId";

    public static final String FORK_PARENT_MESSAGE_ID = "m_parentMessageId";

    public static final String INTERFACE_NAME = "interfaceName";

    /**
     * 客户端调用的服务名称 -> 最好是Cat.getManager().getDomain()获取
     */
    public static final String APPLICATION_KEY = "application.name";
}
