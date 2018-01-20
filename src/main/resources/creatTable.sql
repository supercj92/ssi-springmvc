CREATE TABLE `order` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(45) NOT NULL,
  `skuid` int(4) NOT NULL,
  `num` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `product` (
  `id` int(4) NOT NULL auto_increment,
  `product_name` varchar(45) NOT NULL,
  `stock` int(4) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` int(4) NOT NULL auto_increment,
  `user_name` varchar(45) default NULL,
  `pwd` varchar(45) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#动态的造几十万数据
CREATE  PROCEDURE CREATE_TEST_DATA(IN userName varchar(50),IN datanum INTEGER(11))
  BEGIN
    declare i int DEFAULT 1;
    WHILE i <= datanum DO
      insert into user VALUES(i,concat(userName, i),concat('pwd', i));
      set i = i + 1;
    END WHILE;
  END;

CALL CREATE_TEST_DATA('lcj',1000);

drop procedure if exists CREATE_TEST_DATA;

select * from information_schema.routines where routine_name = 'CREATE_TEST_DATA';

TRUNCATE TABLE ssm.user;

SELECT * from user;