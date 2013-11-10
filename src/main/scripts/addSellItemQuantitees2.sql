CREATE DEFINER=`root`@`localhost` PROCEDURE `addSellItemQuantitees2`()
BEGIN
  DECLARE v_siid, v_prid, v_siqantite, v_prquantite INT;
  DECLARE done INT DEFAULT FALSE;
  DECLARE cur1 CURSOR FOR SELECT id, product_id, quantitee
  FROM sellitem;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur1;

  the_loop: LOOP
 
    #get the values of each column into our variables
    FETCH cur1 INTO v_siid, v_prid, v_siqantite;
    IF done THEN
      LEAVE the_loop;
    END IF;

    #Insert it
    UPDATE product
    set quantitee = quantitee + v_siqantite
    where id = v_prid;

  END LOOP the_loop;

  CLOSE cur1;
END