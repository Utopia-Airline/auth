-- -----------------------------------------------------
-- Schema utopia
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS utopia;
USE utopia ;
-- -----------------------------------------------------
-- Table utopia.user_role
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS utopia.user_role (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY role_id_UNIQUE (id),
  UNIQUE KEY name_UNIQUE (name)
  );
-- -----------------------------------------------------
-- Table utopia.user
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS utopia.user (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  role_id INT UNSIGNED NOT NULL,
  given_name VARCHAR(255) NOT NULL,
  family_name VARCHAR(255) NOT NULL,
  username VARCHAR(45) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY user_id_UNIQUE (id),
  UNIQUE KEY username_UNIQUE (username),
  UNIQUE KEY email_UNIQUE (email),
  UNIQUE KEY phone_UNIQUE (phone),
  KEY fk_user_user_role1_idx (role_id),
  CONSTRAINT fk_user_user_role1
    FOREIGN KEY (role_id)
    REFERENCES utopia.user_role (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);