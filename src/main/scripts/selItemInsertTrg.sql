DELIMITER $$
drop trigger if exists `ManyPrices`.`selItemInsertTrg`;
CREATE TRIGGER `ManyPrices`.`selItemInsertTrg`
    AFTER INSERT
    ON `ManyPrices`.`sellItem` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee - NEW.quantitee
    where id = NEW.product_id;
END $$
DELIMITER ;