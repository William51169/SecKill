package com.javaproject.seckill.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javaproject.seckill.mapper.GoodsMapper;
import com.javaproject.seckill.pojo.Goods;
import com.javaproject.seckill.service.GoodsService;
import com.javaproject.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    // 装配GoodsMapper
    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVo();
    }

    // 根据商品id，返回对应秒杀商品详情
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}

