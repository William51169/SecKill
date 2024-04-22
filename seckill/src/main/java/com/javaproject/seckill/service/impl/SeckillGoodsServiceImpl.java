package com.javaproject.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javaproject.seckill.mapper.SeckillGoodsMapper;
import com.javaproject.seckill.pojo.SeckillGoods;
import com.javaproject.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {
}
