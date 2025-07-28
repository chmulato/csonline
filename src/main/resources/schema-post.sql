-- Adiciona ON DELETE CASCADE nas FKs de customer e courier para app_user
ALTER TABLE customer DROP CONSTRAINT IF EXISTS FK_customer_idbusiness;
ALTER TABLE customer ADD CONSTRAINT FK_customer_idbusiness FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;

ALTER TABLE courier DROP CONSTRAINT IF EXISTS FK_courier_idbusiness;
ALTER TABLE courier ADD CONSTRAINT FK_courier_idbusiness FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;

-- Adicione outros ALTER TABLE conforme necessário para demais FKs
