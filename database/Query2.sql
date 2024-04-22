CREATE TABLE `seckill_user` (
	`id` BIGINT(20) NOT NULL COMMENT '用户ID,设为主键，唯一 手机号',
	`nickname` VARCHAR(255) NOT NULL DEFAULT '',
	`password` VARCHAR(32) NOT NULL DEFAULT '' COMMENT 'MD5(MD5(pass明文+固定salt))',
	`salt` VARCHAR(10) NOT NULL DEFAULT '',
	`head` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '头像',
	`register_date` DATETIME DEFAULT NULL COMMENT '注册时间',
	`last_login_date` DATETIME DEFAULT NULL COMMENT '最后一次登录时间',
	`login_count` INT(11) DEFAULT '0' COMMENT '登录次数',
	PRIMARY KEY (`id`)
	) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `t_goods` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
	`goods_name` VARCHAR(16) NOT NULL DEFAULT '',
	`goods_title` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '商品标题',
	`goods_img` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '商品图片',
	`goods_detail` LONGTEXT NOT NULL COMMENT '商品详情',
	`goods_price` DECIMAL(10, 2) DEFAULT '0.00' COMMENT '商品价格',
	`goods_stock` INT(11) DEFAULT '0' COMMENT '商品库存',
	PRIMARY KEY (`id`)
	) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

	
INSERT INTO `t_goods` VALUES ('1', '整体厨房设计-套件', '整体厨房设计-套件', '/imgs/kitchen.jpg', '整体厨房设计-套件', '15266', '100');
INSERT INTO `t_goods` VALUES ('2', '学习书桌-套件', '学习书桌-套件', '/imgs/desk.jpg','学习书桌套件', '5690', '100');


CREATE TABLE `t_seckill_goods` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
	`goods_id` BIGINT(20) DEFAULT 0 COMMENT '该秒杀商品对应t_goods表中商品id',
	`seckill_price` DECIMAL(10, 2) DEFAULT '0.00' COMMENT '秒杀价',
	`stock_count` INT(10) DEFAULT 0 COMMENT '秒杀商品库存',
	`start_date` DATETIME DEFAULT NULL COMMENT '秒杀开始时间',
	`end_date` DATETIME DEFAULT NULL COMMENT '秒杀结束时间',
	PRIMARY KEY (`id`)
	) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
	

INSERT INTO `t_seckill_goods` VALUES ('1', '1', '5266.00', '10', '2022-11-18 19:36:00', '2022-11-19 09:00:00');
INSERT INTO `t_seckill_goods` VALUES ('2', '2', '690.00', '10', '2022-11-18 08:00:00', '2022-11-19 09:00:00');


-- 编写sql语句，可以返回秒杀商品列表/信息
-- 左外连接
SELECT 
	g.id, 
	g.goods_name, 
	g.goods_title,
	g.goods_img, 
	g.goods_detail, 
	g.goods_price, 
	g.goods_stock, 
	sg.seckill_price, 
	sg.stock_count,
	sg.start_date,
	sg.end_date 
FROM t_goods g 
LEFT JOIN t_seckill_goods sg 
ON g.id = sg.goods_id


SELECT 
	g.id, 
	g.goods_name, 
	g.goods_title,
	g.goods_img, 
	g.goods_detail, 
	g.goods_price, 
	g.goods_stock, 
	sg.seckill_price, 
	sg.stock_count,
	sg.start_date,
	sg.end_date 
FROM t_goods g 
LEFT JOIN t_seckill_goods sg 
ON g.id = sg.goods_id 
WHERE g.id=2


-- 普通订单表，记录订单完整信息
CREATE TABLE `t_order` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT(20) NOT NULL DEFAULT 0,
	`goods_id` BIGINT(20) NOT NULL DEFAULT 0,
	`delivery_addr_id` BIGINT(20) NOT NULL DEFAULT 0,
	`goods_name` VARCHAR(16) NOT NULL DEFAULT '',
	`goods_count` INT(11) NOT NULL DEFAULT '0',
	`goods_price` DECIMAL(10, 2) NOT NULL DEFAULT '0.00',
	`order_channel` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '订单渠道1pc,2Android,3ios',
	`status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '订单状态：0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
	`create_date` DATETIME DEFAULT NULL,
	`pay_date` DATETIME DEFAULT NULL,
	PRIMARY KEY (`id`)
	) ENGINE=INNODB AUTO_INCREMENT=600 DEFAULT CHARSET=utf8mb4;


-- 秒杀订单表，记录某用户id，秒杀的商品id
CREATE TABLE `t_seckill_order` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT(20) NOT NULL DEFAULT 0,
	`order_id` BIGINT(20) NOT NULL DEFAULT 0,
	`goods_id` BIGINT(20) NOT NULL DEFAULT 0,
	PRIMARY KEY(`id`),
	UNIQUE KEY `seckill_uid_gid` (`user_id`, `goods_id`) USING BTREE COMMENT '用id，商品id的唯一索引，解决用一个用户多次抢购'
	)ENGINE=INNODB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8mb4;




