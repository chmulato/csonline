-- Adiciona ON DELETE CASCADE nas FKs de customer e courier para app_user

-- Foreign key de customer para app_user (idbusiness)
ALTER TABLE customer DROP CONSTRAINT FK_customer_idbusiness;
ALTER TABLE customer ADD CONSTRAINT FK_customer_idbusiness
    FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;

-- Foreign key de courier para app_user (idbusiness)
ALTER TABLE courier DROP CONSTRAINT FK_courier_idbusiness;
ALTER TABLE courier ADD CONSTRAINT FK_courier_idbusiness
    FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;

-- Adicione outros ALTER TABLE conforme necessário para demais FKs
