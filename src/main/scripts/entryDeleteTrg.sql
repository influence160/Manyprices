DELIMITER $$
drop trigger if exists `ManyPrices`.`entryDeleteTrg`;
CREATE TRIGGER `ManyPrices`.`entryDeleteTrg`
    AFTER DELETE
    ON `ManyPrices`.`entry` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee - OLD.quantitee
    where id = OLD.product_id;
END $$
DELIMITER ;