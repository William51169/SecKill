package com.javaproject.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.javaproject.seckill.pojo.Goods;
import com.javaproject.seckill.vo.GoodsVo;
import java.util.List;

public interface GoodsService extends IService<Goods> {
    // 秒杀商品列表
    List<GoodsVo> findGoodsVo();

    // 获取商品详情 根据goodsId
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
