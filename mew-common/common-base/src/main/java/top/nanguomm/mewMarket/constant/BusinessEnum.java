package top.nanguomm.mewMarket.constant;

import lombok.Getter;

/**
 * 业务响应状态码枚举类
 */
@Getter
public enum BusinessEnum {


    OPERATION_FAIL(-1,"操作失败"),
    SERVER_INNER_ERROR(9999,"服务内部异常"),
    UN_AUTHORIZATION(401,"未授权"),
    ACCESS_DENY_FAIL(403,"权限不足，请联系管理员")
    ;

    BusinessEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;

    private final String desc;

}
