# --- !Ups

CREATE TABLE Interest (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  value VARCHAR(50) NOT NULL,
  `user` VARCHAR(50) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT '2001-01-01 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE INDEX IDX_user_on_Interest ON Interest (`user`);


# --- !Downs

DROP INDEX IDX_user_on_Interest ON Interest;
DROP TABLE Interest;
