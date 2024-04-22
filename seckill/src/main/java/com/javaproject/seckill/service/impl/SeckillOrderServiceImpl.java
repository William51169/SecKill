package com.javaproject.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.javaproject.seckill.mapper.SeckillOrderMapper;
import com.javaproject.seckill.pojo.SeckillOrder;
import com.javaproject.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {
}
