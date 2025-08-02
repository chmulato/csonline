# Backups de Configuração - WildFly 31

## Descrição
Esta pasta contém backups dos arquivos de configuração do WildFly 31 utilizados no projeto csonline.

## Arquivos de Backup

### standalone-with-hsqldb-YYYYMMDD-HHMMSS.xml
- **Descrição**: Backup do arquivo `standalone.xml` com as configurações do datasource HSQLDB
- **Localização Original**: `server/wildfly-31.0.1.Final/standalone/configuration/standalone.xml`
- **Conteúdo**: 
  - Configuração do driver JDBC HSQLDB (hsqldb-2.7.2.jar)
  - Datasource HSQLDBDatasource com JNDI: java:/HSQLDBDatasource
  - URL de conexão: jdbc:hsqldb:file:csonline
  - Pool de conexões configurado (min: 2, max: 10)

## Como Restaurar
Para restaurar uma configuração do WildFly:

1. Pare o servidor WildFly
2. Copie o arquivo de backup para a pasta de configuração:
   ```powershell
   copy "C:\dev\csonline\bak\standalone-with-hsqldb-YYYYMMDD-HHMMSS.xml" "C:\dev\csonline\server\wildfly-31.0.1.Final\standalone\configuration\standalone.xml"
   ```
3. Reinicie o servidor WildFly

## Observações
- Sempre faça backup antes de modificar configurações do servidor
- Os backups incluem timestamp para facilitar identificação
- Em caso de problemas, use sempre o backup mais recente que funcionava

---
**Data da criação deste README**: 02/08/2025 18:48:00
