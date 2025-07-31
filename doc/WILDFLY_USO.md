
# Guia do Servidor WildFly 31 para a Aplicação csonline

## Um Pouco de História

O WildFly é um dos servidores de aplicação Java mais reconhecidos e utilizados no mundo corporativo. Originalmente conhecido como JBoss Application Server, o projeto foi iniciado no final dos anos 1990 como uma alternativa open source robusta para execução de aplicações Java EE (hoje Jakarta EE). Em 2013, o nome foi alterado para WildFly, marcando uma nova fase de modernização, foco em performance, modularidade e aderência aos padrões mais recentes da plataforma Java.

Ao longo dos anos, o WildFly consolidou-se como referência em ambientes empresariais, sendo adotado por grandes empresas e comunidades de desenvolvedores. Sua arquitetura flexível, suporte a clustering, ferramentas administrativas avançadas e constante atualização o tornam uma escolha confiável para aplicações críticas e inovadoras.

Na aplicação csonline, o WildFly 31 proporciona um ambiente moderno, seguro e alinhado com as melhores práticas do ecossistema Java, garantindo escalabilidade, facilidade de integração e automação.

---

Este documento apresenta as principais orientações para uso, administração e boas práticas do WildFly 31 no contexto da aplicação csonline.

## 1. Estrutura Recomendada

- WildFly instalado em: `server/wildfly-31.0.1.Final`
- Deploy do WAR: `standalone/deployments/csonline.war`
- Logs customizados: `logs/app.log`
- Scripts de automação: raiz do projeto (`*.ps1`)

## 2. Principais Scripts de Administração

- **Iniciar o servidor:**
  ```powershell
  pwsh ./start-wildfly-31.ps1
  ```
- **Parar o servidor:**
  ```powershell
  pwsh ./stop-wildfly-31.ps1
  ```
- **Deploy manual do WAR:**
  ```powershell
  pwsh ./deploy-wildfly-31.ps1
  ```
- **Configurar driver JDBC/DataSource:**
  ```powershell
  pwsh ./config-wildfly-31.ps1 [-SomenteDriver]
  ```
- **Configurar log customizado:**
  ```powershell
  pwsh ./config-log-wildfly-31.ps1
  ```
- **Configurar HTTPS/SSL:**
  ```powershell
  pwsh ./config-ssl-wildfly-31.ps1
  ```

## 3. Acesso à Administração

- Console web: http://localhost:9990
- Usuário/senha: definidos na instalação (veja README)
- CLI: `server/wildfly-31.0.1.Final/bin/jboss-cli.bat --connect`

## 4. Logs

- Log padrão do servidor: `server/wildfly-31.0.1.Final/standalone/log/server.log`
- Log customizado da aplicação: `logs/app.log`
- Para visualizar em tempo real:
  ```powershell
  Get-Content -Wait logs/app.log
  ```

## 5. Deploy e Hot-Deploy

- O WildFly faz hot-deploy de arquivos `.war` copiados para `standalone/deployments`.
- Para atualizar a aplicação, basta substituir o WAR e aguardar o redeploy automático.

## 6. Parâmetros Importantes

- Porta HTTP: 8080
- Porta HTTPS: 8443 (se configurado)
- Porta Admin: 9990

## 7. Recomendações

- Sempre pare o WildFly antes de alterações manuais em configurações.
- Use os scripts para garantir padronização e evitar erros.
- Consulte os logs em caso de problemas de deploy ou inicialização.
- Para ambientes de produção, revise configurações de segurança, memória e logs.

---

Consulte também os demais documentos da pasta `doc` para detalhes de configuração, scripts e arquitetura.
