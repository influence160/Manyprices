DELIMITER $$
drop trigger if exists `ManyPrices`.`selItemDeleteTrg`;
CREATE TRIGGER `ManyPrices`.`selItemDeleteTrg`
    AFTER DELETE
    ON `ManyPrices`.`sellItem` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee + OLD.quantitee
    where id = OLD.product_id;
END $$
DELIMITER ;