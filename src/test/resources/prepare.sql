CREATE TABLE ORDERS (
                        ID           BIGINT         NOT NULL auto_increment,
                        STATUS       INTEGER        NOT NULL,
                        ORDER_NUMBER BIGINT         NOT NULL,
                        TOTAL_AMOUNT DECIMAL(19, 2) NOT NULL,
                        PRIMARY KEY (ID)
);

CREATE TABLE ORDER_ITEM
(
    ID           BIGINT         NOT NULL auto_increment,
    PRICE_ITEM   DECIMAL(19, 2) NOT NULL,
    PRODUCT_ID   BIGINT         NOT NULL,
    QUANTITY     BIGINT         NOT NULL,
    TOTAL_AMOUNT DECIMAL(19, 2) NOT NULL,
    ORDER_ID     BIGINT,
    PRIMARY KEY (ID)
);

ALTER TABLE ORDER_ITEM
    ADD CONSTRAINT ORDER_ITEM_ORDER_ID_FK FOREIGN KEY (ORDER_ID) REFERENCES ORDERS (ID);