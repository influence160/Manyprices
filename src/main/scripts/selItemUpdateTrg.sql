DELIMITER $$
drop trigger if exists `ManyPrices`.`selItemUpdateTrg`;
CREATE TRIGGER `ManyPrices`.`selItemUpdateTrg`
    AFTER UPDATE
    ON `ManyPrices`.`sellItem` FOR EACH ROW
BEGIN
    UPDATE `ManyPrices`.`product`
    set quantitee = quantitee - NEW.quantitee + OLD.quantitee
    where id = NEW.product_id;
END $$
DELIMITER ;