# Arclou — V1 Java Core

> **Objetivo:** construir o núcleo funcional do Arclou usando exclusivamente Java, sem Spring Boot, banco de dados, API REST ou frameworks de infraestrutura.
>
> **Princípio:** esta versão não pretende entregar o MVP web completo. Ela existe para transformar os requisitos do Arclou em um domínio de software real e, ao mesmo tempo, consolidar os fundamentos de Java.

---

## 1. Visão da V1

A V1 representa a primeira implementação real do Arclou.

O sistema será executado localmente pelo terminal (CLI) e armazenará os dados apenas em memória.

O objetivo é aprender Java através do próprio produto:

- variáveis e métodos;
- classes e objetos;
- encapsulamento;
- composição e associação;
- collections;
- generics;
- enums;
- exceptions;
- interfaces;
- lambdas e Streams;
- organização de código;
- testes automatizados;
- separação de responsabilidades;
- regras de negócio.

Ao final, o fluxo principal será:

```text
Criar usuário
     ↓
Criar evento
     ↓
Configurar ingresso
     ↓
Publicar evento
     ↓
Listar eventos
     ↓
Comprar ingresso
     ↓
Confirmar pedido
     ↓
Gerar ingresso
     ↓
Consultar pedidos/ingressos
```

---

## 2. O que NÃO entra na V1

Para manter o foco no aprendizado:

- Spring Boot;
- Spring Framework;
- REST API;
- HTTP;
- frontend;
- PostgreSQL;
- JPA/Hibernate;
- autenticação real;
- JWT;
- Spring Security;
- gateway de pagamento;
- Kafka;
- Redis;
- Docker;
- Kubernetes;
- cloud;
- Terraform;
- microservices;
- observabilidade avançada;
- integrações externas;
- envio real de e-mail/WhatsApp;
- IA.

Os dados podem desaparecer ao encerrar a aplicação. Isso é proposital.

---

## 3. Objetivos de aprendizado

### 3.1 Fundamentos

Consolidar:

- variáveis;
- tipos primitivos;
- String;
- operadores;
- if/else;
- switch;
- for;
- while;
- métodos;
- retorno;
- entrada e saída no terminal.

### 3.2 Orientação a objetos

Praticar:

- classes;
- objetos;
- atributos;
- métodos;
- construtores;
- encapsulamento;
- modificadores de acesso;
- `this`;
- composição;
- associação;
- interfaces;
- polimorfismo;
- herança somente quando houver motivo.

### 3.3 Java aplicado

Utilizar:

- `List`;
- `Set`;
- `Map`;
- generics;
- `enum`;
- `LocalDate`;
- `LocalDateTime`;
- `BigDecimal`;
- exceptions;
- exceptions customizadas;
- lambdas;
- Streams;
- `Optional` quando fizer sentido.

### 3.4 Engenharia de software

Praticar:

- separação de responsabilidades;
- baixo acoplamento;
- coesão;
- Repository;
- Service;
- domínio;
- validação;
- testes;
- refatoração.

---

## 4. Escopo funcional

### 4.1 Usuários

Existirão três papéis conceituais:

```text
PARTICIPANT
ORGANIZER
SUPER_ADMIN
```

Um usuário terá:

```text
ID
Nome
E-mail
Senha
Papel
```

Não haverá autenticação real. A CLI poderá trabalhar com um usuário selecionado no contexto da execução.

### 4.2 Eventos

Um organizador poderá:

- criar evento;
- editar evento;
- consultar evento;
- listar eventos;
- publicar evento;
- cancelar evento.

Um evento terá:

```text
ID
Nome
Descrição
Data
Horário
Local
Cidade
Status
Organizador
Tipos de ingresso
```

### 4.3 Ingressos

Cada evento poderá possuir vários tipos:

```text
Pista
VIP
Camarote
```

Cada tipo terá:

```text
ID
Nome
Descrição
Preço
Quantidade total
Quantidade vendida
```

Disponibilidade:

```text
disponível = quantidade total - quantidade vendida
```

### 4.4 Pedidos

Um pedido terá:

```text
ID
Participante
Evento
Itens
Valor total
Status
```

Cada item:

```text
Tipo de ingresso
Quantidade
Preço unitário
Subtotal
```

O total será calculado pelo sistema.

### 4.5 Tickets emitidos

Após a confirmação do pedido, serão gerados os tickets correspondentes:

```text
Pedido
  └── 2 x VIP
       ├── Ticket #101
       └── Ticket #102
```

Cada ticket terá:

```text
ID
Pedido
Participante
Evento
Tipo de ingresso
Status
```

---

## 5. Estados do domínio

### EventStatus

```text
DRAFT
PUBLISHED
CANCELLED
FINISHED
```

Fluxo:

```text
DRAFT
  │
  ├── PUBLISHED
  │      ├── FINISHED
  │      └── CANCELLED
  │
  └── CANCELLED
```

### OrderStatus

```text
PENDING
CONFIRMED
CANCELLED
```

### TicketStatus

```text
AVAILABLE
SOLD
```

---

## 6. Regras de negócio

### RN01 — Nome obrigatório

Não é permitido criar evento sem nome.

### RN02 — Organizador obrigatório

Todo evento deve possuir um organizador.

### RN03 — Evento inicia como DRAFT

Todo evento novo começa como `DRAFT`.

### RN04 — Ingresso obrigatório para publicação

Evento sem tipo de ingresso não pode ser publicado.

### RN05 — Apenas eventos publicados recebem pedidos

Somente `PUBLISHED` pode receber compras.

### RN06 — Evento cancelado não recebe compras

`CANCELLED` não aceita novos pedidos.

### RN07 — Quantidade positiva

A quantidade de um tipo de ingresso deve ser maior que zero.

### RN08 — Preço não negativo

O preço deve ser maior ou igual a zero.

### RN09 — Não vender acima do estoque

A venda nunca pode ultrapassar a disponibilidade.

### RN10 — Total calculado pelo sistema

```text
total = soma(quantidade × preço unitário)
```

### RN11 — Pedido confirmado gera tickets

Somente pedidos confirmados geram tickets.

### RN12 — Cancelamento bloqueia novas vendas

Evento cancelado não recebe compras.

### RN13 — IDs únicos durante a execução

Pode ser usado um contador simples.

### RN14 — E-mail único

Não cadastrar dois usuários com o mesmo e-mail.

### RN15 — Isolamento conceitual do organizador

Um organizador só gerencia os próprios eventos.

---

# 7. Roadmap interno

```text
V1.1  → Fundamentos
V1.2  → Orientação a Objetos
V1.3  → Domínio e regras
V1.4  → Collections
V1.5  → Exceptions
V1.6  → Enums e tipos de domínio
V1.7  → Interfaces e Repository
V1.8  → Streams e Lambdas
V1.9  → Refatoração
V1.10 → Testes
V1.11 → V1 final
```

Cada etapa adiciona uma capacidade real ao Arclou.

---

# 8. V1.1 — Fundamentos

## Objetivo

Criar o primeiro protótipo executável.

Praticar:

- `main`;
- variáveis;
- String;
- int/double;
- condicionais;
- loops;
- métodos;
- Scanner;
- saída no console.

Menu inicial:

```text
========================
       EVENTHUB
========================

1 - Criar evento
2 - Listar eventos
3 - Buscar evento
4 - Sair

Escolha:
```

Inicialmente os eventos podem ser representados de forma simples.

### Definition of Done

- aplicação inicia;
- menu aparece;
- usuário escolhe opções;
- programa continua até sair;
- criação e consulta básica funcionam.

---

# 9. V1.2 — Orientação a Objetos

## Objetivo

Substituir estruturas primitivas por um domínio orientado a objetos.

Criar inicialmente:

```text
User
Event
TicketType
Order
OrderItem
Ticket
```

Exemplo:

```java
Event event = new Event(...);
```

Praticar:

- classe;
- objeto;
- atributo;
- método;
- construtor;
- encapsulamento;
- `private`;
- getters/setters;
- `this`.

### Definition of Done

- entidades principais existem como classes;
- objetos são instanciados;
- atributos importantes estão encapsulados;
- métodos representam comportamentos;
- `Main` começa a deixar de concentrar toda a lógica.

---

# 10. V1.3 — Domínio e regras de negócio

## Objetivo

Fazer os objetos possuírem comportamento.

Em vez de:

```java
event.setStatus(PUBLISHED);
```

preferir:

```java
event.publish();
```

Comportamentos esperados:

```text
Event.publish()
Event.cancel()
Event.addTicketType()

Order.addItem()
Order.calculateTotal()
Order.confirm()

TicketType.hasAvailability()
TicketType.sell()
```

Exemplo conceitual:

```text
Event.publish()
       ↓
Possui ingresso?
       ↓
     NÃO → erro
       ↓
      SIM
       ↓
PUBLISHED
```

### Definition of Done

- regras importantes não ficam concentradas no `Main`;
- entidades possuem comportamentos coerentes;
- estados inválidos são bloqueados;
- o domínio representa o negócio.

---

# 11. V1.4 — Collections

## Objetivo

Trabalhar com múltiplos objetos.

Utilizar:

```java
List<Event>
List<User>
List<Order>
List<Ticket>
```

e, quando fizer sentido:

```java
Map<Long, Event>
Map<Long, User>
```

Criar armazenamento em memória:

```text
EventRepositoryMemory

1 → Java Conference
2 → Cloud Summit
3 → DevOps Day
```

Operações:

```text
save
findById
findAll
delete
```

### Definition of Done

- múltiplos eventos podem existir;
- múltiplos usuários podem existir;
- entidades podem ser localizadas;
- dados permanecem disponíveis durante a execução;
- não existe banco de dados.

---

# 12. V1.5 — Exceptions

## Objetivo

Tratar erros de forma explícita e significativa.

Criar, por exemplo:

```text
EventNotFoundException
EventCannotBePublishedException
TicketSoldOutException
InvalidTicketQuantityException
UserAlreadyExistsException
OrderCannotBeConfirmedException
```

Exemplo:

```java
throw new TicketSoldOutException();
```

### Definition of Done

- regras inválidas geram exceptions;
- exceptions possuem nomes significativos;
- tratamento não fica espalhado;
- CLI apresenta mensagens compreensíveis.

---

# 13. V1.6 — Enums e tipos de domínio

Eliminar strings mágicas.

```java
enum UserRole {
    PARTICIPANT,
    ORGANIZER,
    SUPER_ADMIN
}
```

```java
enum EventStatus {
    DRAFT,
    PUBLISHED,
    CANCELLED,
    FINISHED
}
```

```java
enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
```

```java
enum TicketStatus {
    AVAILABLE,
    SOLD
}
```

### Definition of Done

- estados importantes usam enums;
- comparações não dependem de strings;
- transições ficam mais seguras.

---

# 14. V1.7 — Interfaces e Repository

## Objetivo

Introduzir abstração e baixo acoplamento.

Criar:

```java
interface EventRepository
```

e:

```text
InMemoryEventRepository
```

Conceito:

```text
EventService
     │
     ▼
EventRepository
     ▲
     │
InMemoryEventRepository
```

Exemplo:

```java
public interface EventRepository {

    void save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll();
}
```

Na V3, a ideia será:

```text
V1
EventRepository
      ↓
InMemoryEventRepository

V3
EventRepository
      ↓
JpaEventRepository
      ↓
PostgreSQL
```

### Definition of Done

- services dependem de abstrações;
- armazenamento em memória fica isolado;
- domínio não conhece detalhes de armazenamento;
- interfaces são usadas onde realmente agregam valor.

---

# 15. V1.8 — Streams e Lambdas

## Objetivo

Aprender processamento de coleções com dados reais.

Implementar consultas como:

```text
listarEventosPublicados()
listarEventosDoOrganizador()
listarEventosFuturos()
listarPedidosConfirmados()
calcularTotalDeVendas()
```

Praticar:

```java
stream()
filter()
map()
sorted()
collect()
toList()
```

e method references:

```java
Event::getName
Order::getTotalAmount
```

### Definition of Done

- consultas relevantes utilizam Streams;
- filtros e transformações são compreensíveis;
- Streams não são usados apenas para "forçar" a tecnologia.

---

# 16. V1.9 — Refatoração

## Objetivo

Revisar o código como um projeto profissional.

Verificar:

- nomes;
- responsabilidades;
- duplicação;
- tamanho dos métodos;
- classes muito grandes;
- dependências;
- regras espalhadas;
- código morto;
- tratamento de erros.

Perguntas:

```text
Esta classe possui responsabilidade demais?

Este método faz coisas demais?

O Main conhece detalhes que não deveria?

Existe código duplicado?

Uma regra está sendo validada em dois lugares?

Consigo testar esta classe isoladamente?
```

---

# 17. Organização sugerida

A estrutura final aproximada:

```text
arclou/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/arclou/
│   │           │
│   │           ├── domain/
│   │           │   ├── User.java
│   │           │   ├── Event.java
│   │           │   ├── TicketType.java
│   │           │   ├── Order.java
│   │           │   ├── OrderItem.java
│   │           │   └── Ticket.java
│   │           │
│   │           ├── enums/
│   │           │   ├── UserRole.java
│   │           │   ├── EventStatus.java
│   │           │   ├── OrderStatus.java
│   │           │   └── TicketStatus.java
│   │           │
│   │           ├── repository/
│   │           │   ├── EventRepository.java
│   │           │   ├── UserRepository.java
│   │           │   ├── OrderRepository.java
│   │           │   └── memory/
│   │           │
│   │           ├── service/
│   │           │   ├── EventService.java
│   │           │   ├── UserService.java
│   │           │   └── OrderService.java
│   │           │
│   │           ├── exception/
│   │           │
│   │           └── Main.java
│   │
│   └── test/
│       └── java/
│
└── README.md
```

A estrutura não precisa nascer completa; ela evolui junto com o aprendizado.

---

# 18. V1.10 — Testes

## Objetivo

Introduzir testes automatizados para as regras mais importantes.

Exemplos:

```text
deveCriarEvento()
deveIniciarEventoComoDraft()
naoDevePublicarEventoSemIngresso()
devePublicarEventoComIngresso()
naoDeveComprarEventoCancelado()
naoDeveVenderMaisIngressosQueDisponiveis()
deveCalcularTotalDoPedido()
deveConfirmarPedido()
deveGerarIngressoAposConfirmacao()
naoDeveCadastrarEmailDuplicado()
```

Não é necessário buscar 100% de cobertura.

O objetivo é aprender a provar automaticamente que uma regra continua funcionando.

---

# 19. Fluxo principal da V1

Ao final:

```text
EVENTHUB

1 - Criar/selecionar usuário
2 - Criar evento
3 - Gerenciar eventos
4 - Adicionar ingresso
5 - Publicar evento
6 - Listar eventos
7 - Comprar ingresso
8 - Meus pedidos
9 - Meus ingressos
10 - Sair
```

Exemplo de fluxo:

```text
> Criar organizador

Nome: João
Email: joao@email.com

Usuário criado.

> Criar evento

Nome: Java Conference 2026
Data: 20/10/2026
Local: São Paulo

Evento criado.

> Adicionar ingresso

Nome: VIP
Preço: 299.90
Quantidade: 100

Ingresso adicionado.

> Publicar evento

Evento publicado.

> Comprar ingresso

Evento: Java Conference 2026
Ingresso: VIP
Quantidade: 2

Pedido criado.

Total: R$ 599,80

Confirmar compra? S

Pedido confirmado.

2 ingressos gerados.
```

---

# 20. Critérios de aceitação

## CA01 — Criar usuário

Dados válidos criam um usuário.

## CA02 — E-mail duplicado

E-mail existente gera `UserAlreadyExistsException`.

## CA03 — Criar evento

Organizador válido consegue criar evento.

## CA04 — Evento inicia como DRAFT

Todo evento novo possui `EventStatus.DRAFT`.

## CA05 — Configurar ingresso

Um evento pode possuir múltiplos tipos.

## CA06 — Publicar evento

Evento com pelo menos um ingresso pode ser publicado.

## CA07 — Publicação inválida

Evento sem ingresso não pode ser publicado.

## CA08 — Listagem

Eventos existentes podem ser listados.

## CA09 — Compra

Participante pode criar pedido para evento publicado.

## CA10 — Estoque

Compra acima da disponibilidade é bloqueada.

## CA11 — Total

O sistema calcula o total automaticamente.

## CA12 — Confirmação

Pedido confirmado muda para `CONFIRMED`.

## CA13 — Geração de ingressos

Pedido confirmado gera tickets correspondentes à quantidade comprada.

## CA14 — Evento cancelado

Evento cancelado não aceita novas compras.

## CA15 — Consulta

Participante consulta pedidos e ingressos durante a execução.

---

# 21. Definition of Done — V1

- [ ] aplicação roda localmente;
- [ ] aplicação possui CLI;
- [ ] usuários podem ser criados;
- [ ] eventos podem ser criados;
- [ ] eventos possuem estados;
- [ ] ingressos podem ser configurados;
- [ ] eventos podem ser publicados;
- [ ] participantes podem comprar;
- [ ] pedidos possuem itens;
- [ ] pedidos calculam o total;
- [ ] estoque é controlado;
- [ ] pedidos podem ser confirmados;
- [ ] tickets são gerados;
- [ ] eventos podem ser cancelados;
- [ ] regras inválidas são bloqueadas;
- [ ] exceptions customizadas existem;
- [ ] enums são utilizados;
- [ ] collections armazenam dados;
- [ ] repository em memória está implementado;
- [ ] services possuem responsabilidades claras;
- [ ] Streams/Lambdas são usados em consultas relevantes;
- [ ] testes automatizados cobrem regras críticas;
- [ ] código está organizado em pacotes;
- [ ] não existe dependência de Spring;
- [ ] não existe banco de dados;
- [ ] não existe API REST.

---

# 22. O que você deverá conseguir explicar ao terminar

## Java

- O que é uma classe?
- O que é um objeto?
- Diferença entre atributo e método?
- O que é encapsulamento?
- Para que serve um construtor?
- O que significa `private`?
- O que é uma interface?
- Quando usar composição?
- O que é polimorfismo?

## Collections

- Quando usar `List`?
- Quando usar `Set`?
- Quando usar `Map`?
- Por que usar generics?

## Exceptions

- O que é uma exception?
- Quando lançar?
- Quando capturar?
- Por que criar exceptions próprias?

## Domínio

- Onde uma regra de negócio deve ficar?
- O que é uma entidade?
- O que é um service?
- O que é um repository?
- Por que separar responsabilidades?

## Streams

- O que é um Stream?
- Para que serve `filter`?
- Para que serve `map`?
- Quando usar `sorted`?
- O que é uma lambda?

## Testes

- O que é um teste unitário?
- O que deve ser testado?
- Por que testar regras de negócio?

---

# 23. O que NÃO precisa estar perfeito

A V1 não precisa ter:

- arquitetura perfeita;
- Clean Architecture completa;
- DDD avançado;
- design patterns em excesso;
- 100% de cobertura;
- performance otimizada;
- concorrência sofisticada;
- persistência;
- autenticação;
- segurança de produção;
- interface gráfica.

O objetivo é aprender.

Não transforme a V1 em uma tentativa de construir um sistema empresarial completo.

---

# 24. Relação entre as versões

```text
V1 — Java Core
│
│  Aprende Java e modela o domínio
│
▼
V2 — Spring Boot
│
│  Transforma o domínio em uma API HTTP
│
▼
V3 — PostgreSQL
│
│  Substitui memória por persistência
│
▼
V4 — Security
│
│  Adiciona autenticação e autorização
│
▼
V5 — Payment
│
│  Integra sistema externo de pagamentos
│
▼
V6 — Kafka
│
│  Introduz processamento assíncrono
│
▼
V7 — Redis
│
│  Cache e concorrência
│
▼
V8 — Observability
│
│  Métricas, logs e traces
│
▼
V9 — Docker + CI/CD
│
│  Empacotamento e entrega automatizada
│
▼
V10 — Cloud + Distributed Architecture
│
│  Escala, disponibilidade e distribuição
```

| Versão | Pergunta |
|---|---|
| V1 | Como modelar e implementar o domínio em Java? |
| V2 | Como transformar isso em uma API web? |
| V3 | Como persistir os dados? |
| V4 | Como proteger a aplicação? |
| V5 | Como integrar pagamentos? |
| V6 | Quando e por que usar mensageria? |
| V7 | Quando cache e concorrência se tornam necessários? |
| V8 | Como saber o que está acontecendo em produção? |
| V9 | Como empacotar e entregar a aplicação? |
| V10 | Como escalar e distribuir o sistema? |

---

# 25. Estratégia de aprendizado

A regra principal:

> **Aprender o conceito → aplicar no Arclou → refatorar o que já existe → documentar o aprendizado.**

Não será:

```text
Curso inteiro de Java
↓
Tentar memorizar tudo
↓
Começar o projeto
```

Será:

```text
Aprender variável
↓
Aplicar no Arclou
↓
Aprender método
↓
Aplicar no Arclou
↓
Aprender classe
↓
Refatorar Arclou
↓
Aprender Collections
↓
Aplicar no Arclou
↓
...
```

Assim, o próprio projeto acompanha sua evolução.

---

# 26. Regra importante do projeto

Não adicionar tecnologia antecipadamente.

Se o problema ainda pode ser resolvido com Java puro, resolva com Java puro.

Se surgir vontade de colocar:

```text
Spring
Banco
Redis
Docker
Kafka
```

antes da hora, primeiro pergunte:

> **"Qual problema concreto desta versão exige essa tecnologia?"**

Se não houver um problema real, a tecnologia fica para a próxima versão.

Isso evita premature optimization e preserva o objetivo educacional.

---

# 27. Resultado esperado

Ao final da V1:

```text
                 EVENTHUB V1

                      CLI
                       │
                       ▼
                Application
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
      Services                   Domain
          │                         │
          └────────────┬────────────┘
                       ▼
                Repositories
                       │
                       ▼
                Memory Storage
```

Você não terá apenas feito exercícios de Java.

Terá construído o primeiro núcleo real do produto.

Quando chegar ao Spring Boot, ele não será "o sistema". Será uma nova camada sobre um domínio que você já conhece.

---

# 28. Próxima etapa

```text
V1-JAVA-CORE.md
       │
       ▼
Java básico
       │
       ▼
V1.1 — CLI inicial
       │
       ▼
V1.2 — Classes e objetos
       │
       ▼
V1.3 — Domínio e regras
       │
       ▼
V1.4 — Collections
       │
       ▼
V1.5 — Exceptions
       │
       ▼
V1.6 — Enums
       │
       ▼
V1.7 — Interfaces/Repository
       │
       ▼
V1.8 — Streams/Lambdas
       │
       ▼
V1.9 — Refatoração
       │
       ▼
V1.10 — Testes
       │
       ▼
V1 COMPLETA
       │
       ▼
V2 — SPRING BOOT
```

**A V1 termina quando o domínio do Arclou funciona bem em Java puro.**

Não quando ele parece uma aplicação web.

Isso é proposital.
