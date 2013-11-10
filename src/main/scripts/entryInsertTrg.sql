DELIMITER $$
drop trigger if exists `ManyPrices`.`entryInsertTrg`;
CREATE TRIGGER `ManyPrices`.`entryInsertTrg`
    AFTER INSERT
    ON `ManyPrices`.`entry` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee + NEW.quantitee
    where id = NEW.product_id;
END $$
DELIMITER ;