package com.longhan.huang.homeinter.utls;

/**
 * Created by 龙汗 on 2016/3/4.
 */
public class ErrorCode {

    /**
     * 成功
     */
    public static final int SUCCESS =0;

    /**
     * 其他错误
     */
    public static final int OTHER =10037;

    /**
     * 密码错误或账户错误
     */
    public static final int LOGIN_ERROR =10038;

    /**
     * 选项有误
     */
    public static final int OPT =10039;

    /**
     * 时间戳有误
     */
    public static final int TIMESTAMP =10040;

    /**
     * 数据不存在
     */
    public static final int NONE =10041;

    /**
     * 数据不唯一，系统错误
     */
    public static final int NON_UNIQUE =10042;

    /**
     * 该账号不可用
     */
    public static final int ALREADY_REGISTRY =10043;

    /**
     * 没有执行连接操作
     */
    public static final int NOT_ONLINE =1044;
}
