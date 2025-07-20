# CSOnline Delivery

Aplicação Web para controle de entregas.

## Descrição

CSOnline Delivery é uma aplicação desenvolvida em Java (SDK 11) utilizando Jakarta EE 10, MyFaces e PrimeFaces para gerenciamento de entregas. O sistema roda em Jakarta EE 10 (Tomcat compatível) e utiliza PostgreSQL 15 como banco de dados relacional.

Acesse via navegador: [https://www.caracore.com.br/csonline](https://www.caracore.com.br/csonline)

## Funcionalidades

- Cadastro e controle de entregas
- Perfil de administrador
- Interface web responsiva

## Requisitos


- Java SDK 11
- Jakarta EE 10 (compatível com Tomcat 10.1.x ou superior)
- PostgreSQL 15
- Tomcat 10.1.x (ou superior) configurado para Jakarta EE
- Jars obrigatórios no WAR (WEB-INF/lib):
  - primefaces-13.0.4-jakarta.jar (baixe manualmente se necessário)
  - myfaces-api-4.0.0.jar (MyFaces)
  - myfaces-impl-4.0.0.jar (MyFaces)
  - weld-servlet-shaded-4.0.3.Final.jar (CDI Weld)
  - Demais dependências do projeto (jsoup, log4j, postgresql, etc.)

## Instalação

1. Clone o repositório.
2. Configure o banco de dados PostgreSQL 15 conforme o padrão do projeto (consulte os scripts em `doc/dump`).
3. Faça o build do projeto e gere o arquivo WAR.
4. Certifique-se que os jars obrigatórios estão presentes em WEB-INF/lib do WAR.
5. Faça o deploy do WAR em um Tomcat 10.1.x (ou superior) configurado para Jakarta EE.

## Perfil de Administrador

- Usuário: `chmulato`
- Senha: `admin`

## Demonstração

Veja no Youtube: [https://youtu.be/vAMd647anMA](https://youtu.be/vAMd647anMA)

## Licença

Este projeto está licenciado sob a Licença MIT. Consulte o arquivo LICENSE para mais detalhes.

## Autor

Christian Vladimir Uhdre Mulato

## Contato

Para dúvidas ou sugestões, entre em contato pelo site ou pelo e-mail disponível no perfil do autor.
