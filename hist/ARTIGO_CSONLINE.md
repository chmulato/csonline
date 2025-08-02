


## Refatoração e Evolução do CSOnline: Convite à Comunidade

![Participe do desafio](img/participe.png)

---

### Um pouco de história

O CSOnline nasceu como uma aplicação legado baseada em JSF rodando em Tomcat, com consultas SQL e uma fábrica de conexões manual. Com o tempo, percebemos a necessidade de modernizar a arquitetura para garantir escalabilidade, manutenção e integração com novas tecnologias.

---

### O que foi feito hoje

Avançamos significativamente na migração, levando toda a camada de persistência para JPA/EclipseLink, corrigindo queries JPQL, ajustando o tratamento de exceções e garantindo a integridade referencial entre entidades. Também aprimoramos os testes unitários com JUnit 5 e integramos o Log4j2 para uma melhor rastreabilidade.

Além disso, resolvemos desafios envolvendo mapeamentos de cascata, removendo entidades filhas corretamente e ajustando dependências Maven para garantir builds limpos e confiáveis.

O backend está sendo migrado para REST com Jakarta, tornando a aplicação mais flexível e pronta para integrações modernas. O front-end será aberto para colaboração de entusiastas, permitindo que quem gosta de UI/UX proponha novas interfaces e experiências.

---

### Convite à colaboração

Quer contribuir? Faça um fork da branch `service_restfull`, proponha melhorias, corrija bugs ou sugira novas funcionalidades. Se você é entusiasta de Java, arquitetura de sistemas ou front-end, seu talento é bem-vindo!

O código está disponível no repositório principal:
https://github.com/chmulato/csonline

Os endpoints REST para integração estão disponíveis na branch `service_restfull`.

---

Vamos juntos construir uma solução robusta e inovadora para gestão de entregas. Comente, compartilhe e contribua!

---

#java #jakarta #jpa #eclipselink #rest #opensource #colaboracao #devcommunity #frontend
