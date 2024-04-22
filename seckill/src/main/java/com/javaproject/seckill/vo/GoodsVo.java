package com.javaproject.seckill.vo;

import com.javaproject.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

// 对应的就是显示在秒杀商品列表页的商品信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {
    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Date startDate;

    private Date endDate;

    // 如果后面有需求，可以在进行修改

}
