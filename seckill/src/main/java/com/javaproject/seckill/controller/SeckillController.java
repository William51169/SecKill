package com.javaproject.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.javaproject.seckill.config.AccessLimit;
import com.javaproject.seckill.pojo.Order;
import com.javaproject.seckill.pojo.SeckillMessage;
import com.javaproject.seckill.pojo.SeckillOrder;
import com.javaproject.seckill.pojo.User;
import com.javaproject.seckill.rabbitmq.MQSenderMessage;
import com.javaproject.seckill.service.GoodsService;
import com.javaproject.seckill.service.OrderService;
import com.javaproject.seckill.service.SeckillOrderService;
import com.javaproject.seckill.vo.GoodsVo;
import com.javaproject.seckill.vo.RespBean;
import com.javaproject.seckill.vo.RespBeanEnum;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.common.Fonts;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    // 装配需要的组件/对象
    @Resource
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate redisTemplate;

    // 装配消息的生产者/发送者
    @Resource
    private MQSenderMessage mqSenderMessage;

    // 定义map，用于记录秒杀商品是否还有库存
    private HashMap<Long, Boolean> entryStockMap = new HashMap<>();

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V1.0
    // 先完成一个V1.0版本，后面在高并发的情况下，在做优化
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//
//        if (user == null) {
//            return "/login";
//        }
//
//        // 将User放入到model，下一个模板可以使用
//        model.addAttribute("user", user);
//
//        // 查询秒杀商品的信息，获取goodsVo
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 判断库存
//        // 库存不足
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 判断用户是否复购
//        // 判断当前购买用户id和购买商品id是否已经在商品秒杀表存在了
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail"; // 错误页面;
//        }
//
//        // 抢购
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//           return "secKillFail"; // 错误页面;
//        }
//
//        // 进入到订单页
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        // 进入到订单详情页
//        return "orderDetail";
//    }

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V2.0
    // 先完成一个V1.0版本，后面在高并发的情况下，在做优化
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//
//        if (user == null) {
//            return "/login";
//        }
//
//        // 将User放入到model，下一个模板可以使用
//        model.addAttribute("user", user);
//
//        // 查询秒杀商品的信息，获取goodsVo
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 判断库存
//        // 库存不足
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 判断用户是否复购——直接到redis中，获取对应的秒杀订单，如果有，则说明已经抢购了
//        SeckillOrder o = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        // 说明该用户已经抢购了该商品
//        if (o != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 抢购
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面;
//        }
//
//        // 进入到订单页
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        // 进入到订单详情页
//        return "orderDetail";
//    }

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V3.0
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//
//        if (user == null) {
//            return "/login";
//        }
//
//        // 将User放入到model，下一个模板可以使用
//        model.addAttribute("user", user);
//
//        // 查询秒杀商品的信息，获取goodsVo
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 判断库存
//        // 库存不足
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 判断用户是否复购——直接到redis中，获取对应的秒杀订单，如果有，则说明已经抢购了
//        SeckillOrder o = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        // 说明该用户已经抢购了该商品
//        if (o != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 库存预减，如果在Redis中预减库存，发现秒杀商品已经没有了，就直接返回
//        // 从而减少去执行 orderService.seckill() 请求，防止线程堆积，优化秒杀/高并发
//        // decrement()具有原子性
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        // 说明该商品没有库存了
//        if (decrement < 0){
//            // 恢复库存为0
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
//            return "secKillFail"; // 错误页面
//        }
//
//        // 抢购
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面;
//        }
//
//        // 进入到订单页
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        // 进入到订单详情页
//        return "orderDetail";
//    }

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V4.0,加入内存标记优化秒杀
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//
//        if (user == null) {
//            return "/login";
//        }
//
//        // 将User放入到model，下一个模板可以使用
//        model.addAttribute("user", user);
//
//        // 查询秒杀商品的信息，获取goodsVo
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 判断库存
//        // 库存不足
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 判断用户是否复购——直接到redis中，获取对应的秒杀订单，如果有，则说明已经抢购了
//        SeckillOrder o = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        // 说明该用户已经抢购了该商品
//        if (o != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 对map进行判断【内存标记】,如果商品在map中已经标记没有库存，直接返回，无需进行redis预减
//        if (entryStockMap.get(goodsId)){
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return  "secKillFail"; // 错误页面
//        }
//
//
//        // 库存预减，如果在Redis中预减库存，发现秒杀商品已经没有了，就直接返回
//        // 从而减少去执行 orderService.seckill() 请求，防止线程堆积，优化秒杀/高并发
//        // decrement()具有原子性
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        // 说明该商品没有库存了
//        if (decrement < 0){
//            // 说明当前秒杀的商品已经没有库存了
//            entryStockMap.put(goodsId, true);
//            // 恢复库存为0
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
//            return "secKillFail"; // 错误页面
//        }
//
//        // 抢购
//        Order order = orderService.seckill(user, goodsVo);
//        if (order == null) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面;
//        }
//
//        // 进入到订单页
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goodsVo);
//
//        // 进入到订单详情页
//        return "orderDetail";
//    }

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V5.0,消息队列，完成秒杀的异步请求
//    @RequestMapping("/doSeckill")
//    public String doSecKill(Model model, User user, Long goodsId) {
//
//        if (user == null) {
//            return "/login";
//        }
//
//        // 将User放入到model，下一个模板可以使用
//        model.addAttribute("user", user);
//
//        // 查询秒杀商品的信息，获取goodsVo
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//
//        // 判断库存
//        // 库存不足
//        if (goodsVo.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 判断用户是否复购——直接到redis中，获取对应的秒杀订单，如果有，则说明已经抢购了
//        SeckillOrder o = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
//        // 说明该用户已经抢购了该商品
//        if (o != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
//            return "secKillFail"; // 错误页面
//        }
//
//        // 对map进行判断【内存标记】,如果商品在map中已经标记没有库存，直接返回，无需进行redis预减
//        if (entryStockMap.get(goodsId)){
//            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
//            return  "secKillFail"; // 错误页面
//        }
//
//
//        // 库存预减，如果在Redis中预减库存，发现秒杀商品已经没有了，就直接返回
//        // 从而减少去执行 orderService.seckill() 请求，防止线程堆积，优化秒杀/高并发
//        // decrement()具有原子性
//        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
//        // 说明该商品没有库存了
//        if (decrement < 0){
//            // 说明当前秒杀的商品已经没有库存了
//            entryStockMap.put(goodsId, true);
//            // 恢复库存为0
//            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
//            return "secKillFail"; // 错误页面
//        }
//
//        // 抢购，向消息队列发送秒杀请求，实现了秒杀异步请求
//        // 这里我们发送秒杀消息后，立即快速返回结果（临时结果，如“排队中”）
//        // 客户端可以通过轮询，获得最后的结果
//        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
//        mqSenderMessage.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));
//
//        model.addAttribute("errmsg", "排队中");
//        return "secKillFail"; // 排队页面
//
//    }

    // 方法：处理用户抢购请求/秒杀
    // 秒杀V6.0,消息队列，加入秒杀安全,直接返回RespBean
    @RequestMapping("/{path}/doSeckill")
    @ResponseBody
        public RespBean doSecKill(@PathVariable String path, Model model, User user, Long goodsId) {

        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 增加判断逻辑，校验用户携带的路径path是否正确
        boolean b = orderService.checkPath(user, goodsId, path);
        if (!b) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }


        // 查询秒杀商品的信息，获取goodsVo
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);

        // 判断库存
        // 库存不足
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 判断用户是否复购——直接到redis中，获取对应的秒杀订单，如果有，则说明已经抢购了
        SeckillOrder o = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsVo.getId());
        // 说明该用户已经抢购了该商品
        if (o != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }

        // 对map进行判断【内存标记】,如果商品在map中已经标记没有库存，直接返回，无需进行redis预减
        if (entryStockMap.get(goodsId)){
            model.addAttribute("errmsg", RespBeanEnum.ENTRY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }


        // 库存预减，如果在Redis中预减库存，发现秒杀商品已经没有了，就直接返回
        // 从而减少去执行 orderService.seckill() 请求，防止线程堆积，优化秒杀/高并发
        // decrement()具有原子性
        Long decrement = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        // 说明该商品没有库存了
        if (decrement < 0){
            // 说明当前秒杀的商品已经没有库存了
            entryStockMap.put(goodsId, true);
            // 恢复库存为0
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.ENTRY_STOCK);
        }

        // 抢购，向消息队列发送秒杀请求，实现了秒杀异步请求
        // 这里我们发送秒杀消息后，立即快速返回结果（临时结果，如“排队中”）
        // 客户端可以通过轮询，获得最后的结果
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSenderMessage.sendSeckillMessage(JSONUtil.toJsonStr(seckillMessage));

        return RespBean.error(RespBeanEnum.SEK_KILL_WAIT);

    }

    // 方法：获取秒杀路径
    @RequestMapping("/path")
    @ResponseBody
    /**
     * 自定义注解
     * 1. 使用注解的方式，完成对用户的限流防刷——通用性和灵活性提高
     * 2. second = 5, maxCount = 5, 说明在5秒内可以访问的最大次数是5次
     * 3. needLogin = true, 说明需要登录才能访问
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return new RespBean().error(RespBeanEnum.SESSION_ERROR);
        }

        // 增加业务逻辑：加入Redis计数器，完成对用户的限流防刷
        // 比如5秒内访问次数超过5次，我们就认为是刷接口
        // 先用方法实现，然后使用注解完成，提高通用性
        // uri 是 http//localhost:8080/seckill/path 中的 seckill/path
//        String uri = request.getRequestURI();
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String key = uri + ":" + user.getId();
//        Integer count = (Integer)valueOperations.get(key);
//        // 说明还没有key,进行初始化，值为1，过期时间为5秒
//        if (count == null) {
//            valueOperations.set(key, 1, 5, TimeUnit.SECONDS);
//        } else if (count < 5) { // 说明是正常访问
//            valueOperations.increment(key);
//        } else { // 此时，用户在频繁访问
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }

        // 增加一个业务逻辑：校验用户输入的验证码是否正确
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        // 校验失败
        if (!check) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    // 生成校验码---happyCaptcha
    @RequestMapping("/captcha")
    public void happyCaptcha(HttpServletRequest request, HttpServletResponse response, User user, Long goodsId) {
        // 生成验证码，并输出
        // 该验证码默认保存到session中，key是happy-captcha
        HappyCaptcha.require(request, response)
                .style(CaptchaStyle.ANIM)               //设置展现样式为动画
                .type(CaptchaType.NUMBER)               //设置验证码内容为数字
                .length(6)                              //设置字符长度为6
                .width(220)                             //设置动画宽度为220
                .height(80)                             //设置动画高度为80
                .font(Fonts.getInstance().zhFont())     //设置汉字的字体
                .build().finish();                      //生成并输出验证码

        // 把验证码的值，保存到redis中【考虑项目分布式】
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId,
                (String) request.getSession().getAttribute("happy-captcha"), 100, TimeUnit.SECONDS);
    }


    // 该方法是在类的所有属性，都初始化后，自动执行的
    // 这里我们可以将所有秒杀商品的库存量，都加载到redis
    @Override
    public void afterPropertiesSet() throws Exception {
        // 查询所有的秒杀商品
        List<GoodsVo> list = goodsService.findGoodsVo();
        // 先判断是否为空
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 遍历list，然后将秒杀商品的库存量，放入到redis
        // 秒杀圣品的库存量对应key：seckillGoods:商品id
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());

            // 初始化map
            // 如果goodsId:false,表示还有库存
            // 如果goodsId:true,表示没有库存
            entryStockMap.put(goodsVo.getId(), false);
        });
    }


}
