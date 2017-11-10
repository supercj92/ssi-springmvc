CREATE TABLE `order` (
  `id` int(11) NOT NULL auto_increment,
  `customer_name` varchar(45) NOT NULL,
  `skuid` int(11) NOT NULL,
  `num` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `product` (
  `id` int(11) NOT NULL auto_increment,
  `product_name` varchar(45) NOT NULL,
  `stock` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(45) default NULL,
  `pwd` varchar(45) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8