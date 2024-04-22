package com.javaproject.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object obj;

//    public RespBean(long code, String message, Object obj) {
//        this.code = code;
//        this.message = message;
//        this.obj = obj;
//    }

    //成功后-同时携带数据
    public static RespBean success(Object data) {
        return new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), data);

    }

    //成功后-不携带数据
    public static RespBean success() {
        return
                new RespBean(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);

    }

    //失败各有不同-返回失败信息时，不携带数据
    public static RespBean error(RespBeanEnum respBeanEnum) {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }

    //失败各有不同-返回失败信息时，同时携带数据
    public static RespBean error(RespBeanEnum respBeanEnum, Object data) {
        return new RespBean(respBeanEnum.getCode(), respBeanEnum.getMessage(), data);
    }
}
