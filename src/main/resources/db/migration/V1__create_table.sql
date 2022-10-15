DROP TABLE IF EXISTS NK225MINI;
CREATE TABLE NK225MINI (
  trd_date DATE ,
  trd_time TIME ,
  mk_date DATE ,
  price INT ,
  price_type CHAR ,
  vol INT ,
  no INT ,
  PRIMARY KEY (trd_date, no)
) ENGINE=MyISAM;
