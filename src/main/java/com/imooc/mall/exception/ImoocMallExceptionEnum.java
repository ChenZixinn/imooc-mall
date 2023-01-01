package com.imooc.mall.exception;

/**
 * 异常枚举
 */
public enum ImoocMallExceptionEnum {

    SYSTEM_EXCEPTION(20001, "系统异常"),
    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码长度小于8"),
    NAME_EXISTED(10004, "不允许重名"),
    INSERT_FAILED(10005, "插入失败，请重试"),
    WRONG_PASSWORD(10006, "账号或密码错误"),
    NEED_LOGIN(10007, "请登陆后再进行操作"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "没有管理员权限"),
    PARA_NOT_NULL(10010, "参数不能为空"),
    CREATE_FAILED(10011, "新增失败"),
    REQUEST_PARAM_ERROR(10012, "参数异常"),
    NOT_FOUNT(10013, "没有找到数据"),
    DELETE_FAILED(10014, "删除失败"),
    MKDIR_FAILED(10015, "创建目录失败"),
    UPLOAD_FAILED(10016, "文件上传失败"),
    NOT_SALE(10017, "商品状态异常"),
    NOT_ENOUGH(10018, "商品库存不足"),
    NOT_SELECT(10019, "未选择商品"),
    ORDER_STATUS_NOT_FOUND(10020, "订单状态未找到"),
    NOT_ORDER(10021, "订单不存在"),
    NOT_YOUR_ORDER(10022, "订单不属于你"),
    ORDER_CANCEL(10023, "订单已取消"),
    QRCODE_ENCODE_FAILED(10024, "二维码生成失败"),
    ORDER_STATUS_ERROR(10025, "订单状态异常"),
    NOT_PAID(10026, "订单未付款");

    Integer code;
    String msg;

    ImoocMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
