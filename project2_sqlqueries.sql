CREATE SCHEMA `universitylibrarysystem`;


CREATE TABLE `universitylibrarysystem`.`members` (
 `member_No` INT NOT NULL,
  `ssn` INT NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `campus_address` VARCHAR(45) NOT NULL,
  `mailing_address` VARCHAR(45) NOT NULL,
  `phone_no` VARCHAR(10) NOT NULL,
  `photo_id` VARCHAR(45) NOT NULL,
  `books_borrowed` INT NOT NULL DEFAULT 0,
  `membership_startdate` DATE NOT NULL,
  `mem_type` VARCHAR(45) NOT NULL,
  `mem_status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`Member_No`),
  UNIQUE INDEX `SSN_UNIQUE` (`SSN` ASC) VISIBLE);


CREATE TABLE `universitylibrarysystem`.`books` (
  `isbn` INT NOT NULL,
  `title` VARCHAR(45) NOT NULL,
  `author` VARCHAR(45) NOT NULL,
  `sub_area` VARCHAR(45) NOT NULL,
  `desc` LONGTEXT NULL,
  `is_rent` TINYINT NOT NULL DEFAULT 0,
  `quantity` INT NOT NULL,
  `books_rented` INT NOT NULL,
  UNIQUE INDEX `ISBN_UNIQUE` (`ISBN` ASC) VISIBLE,
  	PRIMARY KEY (`ISBN`));

CREATE TABLE `universitylibrarysystem`.`borrowed` (
  `member_No` INT NOT NULL,
  `isbn` INT NOT NULL,
  `issue_date` DATE NOT NULL,
  `is_returned` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`Member_No`, `ISBN`, `is_returned`),
  INDEX `ISBN_FK_idx` (`ISBN` ASC) VISIBLE,
  CONSTRAINT `Member_no_FK`
  FOREIGN KEY (`Member_No`)
 REFERENCES `universitylibrarysystem`.`members` (`Member_No`)
 ON DELETE NO ACTION
 ON UPDATE NO ACTION,
 CONSTRAINT `ISBN_FK`
FOREIGN KEY (`ISBN`)
REFERENCES `universitylibrarysystem`.`books` (`ISBN`)
ON DELETE NO ACTION
ON UPDATE NO ACTION);


DROP TRIGGER IF EXISTS `universitylibrarysystem`.`borrowed_AFTER_INSERT`;

DELIMITER $$
USE `universitylibrarysystem`$$
CREATE DEFINER=`root`@`localhost` TRIGGER `borrowed_AFTER_INSERT` AFTER INSERT ON `borrowed` FOR EACH ROW BEGIN
	SET @COUNT=(SELECT COUNT(*) FROM borrowed WHERE member_no=New.member_no);
	SET @COUNTBK=(SELECT COUNT(*) FROM borrowed WHERE ISBN=New.ISBN);
    IF @COUNT!=0 THEN
		UPDATE members set books_borrowed = @Count where member_no = New.member_no;
	end if;
	IF @COUNTBK!=0 THEN
		UPDATE books set books_rented = @Count where ISBN=New.ISBN;
		UPDATE books set Quantity = Quantity-1 where ISBN=New.ISBN;
	end if;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `universitylibrarysystem`.`borrowed_After_UPDATE`;

DELIMITER $$
USE `universitylibrarysystem`$$
CREATE DEFINER=`root`@`localhost` TRIGGER `borrowed_After_UPDATE` AFTER Update ON `borrowed` FOR EACH ROW BEGIN

	UPDATE members set books_borrowed = books_borrowed-1 where member_no = New.member_no;
	UPDATE books set books_rented = books_rented-1 where ISBN=New.ISBN;
	UPDATE books set Quantity = Quantity+1 where ISBN=New.ISBN;

END$$
DELIMITER ;
