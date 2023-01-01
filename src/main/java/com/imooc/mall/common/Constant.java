package com.imooc.mall.common;

import com.google.common.collect.Sets;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;

/**
 * 常量
 */
@Component
public class Constant {
    public static final String IMOOC_MALL_USER = "imooc_mall_user";
    public static final String SALT = "e!@$u&)+8,sd?2*1";
    public static final Integer CACHING_SECONDS = 300;

    public static String FILE_UPLOAD_DIR;

    @Value("${file.upload.dir}")
    public void setFileDirUpload(String fileDirUpload){
        FILE_UPLOAD_DIR = fileDirUpload;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    /**
     * 商品上架状态
     */
    public interface SALE_STATUS{
        int NOT_SALE = 0;
        int SALE = 1;
    }

    /**
     * 商品选中状态
     */
    public interface CHOICE{
        int CHECKED = 1;
        int UN_CHECKED = 0;
    }
    /**
     * User Role
     */
    public interface USER_ROLE{
        int ADMIN = 2;
        int CUSTOMER = 1;
    }
    /**
     * 订单状态
     */
    public enum ORDER_STATUS_ENUM{
        CANCELED("订单取消", 0),
        NOT_PAID("未付款", 10),
        PAID("已付款", 20),
        DELIVERED("已发货", 30),
        FINISH("交易完成", 40);
        private String value;
        private Integer code;

        ORDER_STATUS_ENUM(String value, Integer code) {
            this.value = value;
            this.code = code;
        }

        public static ORDER_STATUS_ENUM codeBy(Integer code){
            for (ORDER_STATUS_ENUM value : values()) {
                if (Objects.equals(value.getCode(), code))
                    return value;
            }
            throw new ImoocMallException(ImoocMallExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }
}
