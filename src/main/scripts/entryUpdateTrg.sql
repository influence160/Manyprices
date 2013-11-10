DELIMITER $$
drop trigger if exists `ManyPrices`.`entryUpdateTrg`;
CREATE TRIGGER `ManyPrices`.`entryUpdateTrg`
    AFTER UPDATE
    ON `ManyPrices`.`entry` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee + NEW.quantitee - OLD.quantitee
    where id = NEW.product_id;
END $$
DELIMITER ;