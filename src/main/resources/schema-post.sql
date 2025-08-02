-- Adiciona ON DELETE CASCADE nas FKs de customer e courier para app_user

-- Foreign key de customer para app_user (idbusiness)
-- Verificar se a constraint existe antes de tentar removê-la
-- Se não existir, apenas criá-la
ALTER TABLE customer DROP CONSTRAINT IF EXISTS FK_customer_idbusiness;
ALTER TABLE customer ADD CONSTRAINT FK_customer_idbusiness
    FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;

-- Foreign key de courier para app_user (idbusiness)
-- Verificar se a constraint existe antes de tentar removê-la
-- Se não existir, apenas criá-la
ALTER TABLE courier DROP CONSTRAINT IF EXISTS FK_courier_idbusiness;
ALTER TABLE courier ADD CONSTRAINT FK_courier_idbusiness
    FOREIGN KEY (idbusiness) REFERENCES app_user(id) ON DELETE CASCADE;