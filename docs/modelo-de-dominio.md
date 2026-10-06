# Arclou — Modelo de domínio por versão

> **Objetivo:** dizer, para cada entidade, **quais campos existem e em que versão entram**, de forma que o que está desenhado no Figma tenha um lugar certo no código.
>
> Complementa [`docs/produto-e-design.md`](produto-e-design.md) (telas e regras) e [`docs/v1-java-core.md`](v1-java-core.md) (detalhe da V1). Decisões estruturais: ADR-0001 (superfícies), ADR-0002 (ciclo do ticket), ADR-0003 (produtora).

## Como ler

- **Versão** = primeira versão em que o campo passa a existir no código.
- A V1 é a base: campos marcados **V1** são exatamente os da spec da V1.
- Nomes em inglês são os nomes no código; a descrição está em português.
- Dinheiro: centavos (`long`) até a V1.5; tipo `Money` a partir da V1.6.
- Campo novo que surgir no design entra aqui **antes** de virar Issue.

---

## Visão geral

```text
User ──< Membership >── Organization ──< Event ──< TicketType
  │                                         │           │
  │                                         ├──< Gate   ├──< Reservation
  │                                         │           └──< WaitlistEntry
  └──< Order ──< OrderItem                  │
         │                                  │
         ├──< Ticket ──< CheckInScan ───────┘
         └──< Refund
```

| Entidade | Entra na | Observação |
|---|---|---|
| User | V1 | |
| Event, TicketType, Order, OrderItem, Ticket | V1 | núcleo da V1 |
| Organization, Membership, Session | V4 | ADR-0003 |
| Reservation, Refund, Gate, CheckInScan, BankAccount, Payout | V5 | pagamentos e check-in (ADR-0002) |
| WaitlistEntry, NotificationPreference | V6 | RN22 e notificações |
| SupportRequest | V3 | suporte (MA-A5) |
| EventPageView | V8 | conversão do Painel |

---

## User — conta

| Campo | Versão | Descrição |
|---|---|---|
| `id` | V1 | Identificador único (RN13) |
| `name` | V1 | Nome completo |
| `email` | V1 | Único (RN14) |
| `role` | V1 → V4 | V1: `PARTICIPANT` / `ORGANIZER` / `SUPER_ADMIN`. **V4:** só `SUPER_ADMIN` ou nenhum; organizador passa a ser derivado de `Membership` (ADR-0003) |
| `passwordHash` | V4 | Nunca a senha em texto |
| `emailVerifiedAt` | V4 | RN32 |
| `phone` | V3 | PF-A1 |
| `photo` | V3 | PF-A3 |
| `twoFactorEnabled`, códigos de recuperação, passkeys | V4 | CF-A7, CF-B2–B4 |
| `marketingOptIn` | V4 | Novidades por e-mail (AC-A2, LGPD) |
| `deletionRequestedAt` | V4 | Exclusão em 30 dias (RN27) |

## Session — aparelho conectado (V4)

| Campo | Descrição |
|---|---|
| `user`, `deviceName`, `city`, `lastSeenAt`, `trustedUntil` | Sessões ativas (CF-A7); "confiar neste aparelho" por 30 dias (AC-B2) |

## Organization — produtora (V4)

| Campo | Versão | Descrição |
|---|---|---|
| `id`, `name`, `city` | V4 | AC-B3 |
| `slug` | V4 | Página pública `/o/<slug>` (MA-A2, PT-A3) |
| `about` (até 300), `logo`, `cover`, `instagram`, `website`, `contactEmail` | V4 | MA-A2 |
| `showPastEvents`, `showTicketsSold` | V4 | O que a página pública exibe |
| `language`, `timezone`, `currency`, `dateFormat` | V4 | CF-A2 |
| `defaultMaxPerOrder` (6) | V4 | RN19 |
| `refundPolicyDays` (7) | V5 | RN24 |
| `passFeeToBuyer` (true) | V5 | RN17 |
| `waitlistDefault` (false) | V6 | Padrão para tipos novos (RN22) |

## Membership — vínculo com a produtora (V4)

| Campo | Descrição |
|---|---|
| `user`, `organization` | Um vínculo por pessoa e produtora |
| `role` | `OWNER` (exatamente 1) · `ADMIN` · `FINANCE` · `CHECK_IN` (CF-A5) |
| `status` | `INVITED` · `ACTIVE` |
| `invitedEmail`, `invitedBy`, `expiresAt` | Convite de 7 dias, só para aquele e-mail (RN33) |

## Event — evento

| Campo | Versão | Descrição |
|---|---|---|
| `id`, `name`, `description` | V1 | Nome obrigatório (RN01) |
| `date`, `time`, `location`, `city` | V1 | |
| `status` | V1 | `DRAFT` / `PUBLISHED` / `CANCELLED` / `FINISHED` (RN03, RN04) |
| `organizer` → `organization` | V1 → V4 | V1–V3: `User`. V4: `Organization` (ADR-0003) |
| `ticketTypes` | V1 | Array (até 5) na V1.3; `List` a partir da V1.4 |
| `category`, `format` (`IN_PERSON` / `ONLINE` / `HYBRID`), `ageRating`, `language` | V2 | EV-B3 |
| `endsAt`, `gatesOpenAt`, endereço completo, `streamUrl` | V2 | EV-B4–B6 |
| `salesPaused`, `salesClosed` | V2 | RN18 |
| `slug`, `cover` | V3 | `/e/<slug>`, capa |
| `checkInOpensAt` | V5 | Padrão: 2 h antes do início (RN35) |

## TicketType — tipo de ingresso (e lote)

| Campo | Versão | Descrição |
|---|---|---|
| `id`, `name`, `description` | V1 | |
| `price` | V1 | Centavos (V1.3) → `Money` (V1.6). ≥ 0; zero = gratuito (RN08) |
| `quantity`, `sold` | V1 | Quantidade > 0 (RN07); disponível = `quantity − sold` (RN09) |
| `salesStartAt`, `salesEndAt` | V3 | Janela de vendas — é assim que existem **lotes** (RN40) |
| `requiresDocument` | V5 | Exige documento na entrada (RN41, RN38) |
| `waitlistEnabled` | V6 | Fila de espera (RN22) |

## Gate — portão de entrada (V5)

| Campo | Descrição |
|---|---|
| `event`, `name` (ex.: "Portão B"), `ticketTypes` | Organiza filas e relatórios do check-in (CK-A1, CK-A7). Não altera a validade do ticket (RN35) |

## Order — pedido

| Campo | Versão | Descrição |
|---|---|---|
| `id`, `participant`, `event`, `items`, `total`, `status` | V1 | `total` calculado pelo sistema (RN10); `PENDING` / `CONFIRMED` / `CANCELLED` |
| `createdAt` | V2 | |
| `paymentMethod` (`PIX` / `CARD` / `BOLETO`), `serviceFee`, `totalPaid`, `paidAt`, `expiresAt` | V5 | RN17, RN21 |
| `buyerDocument` (CPF) | V5 | Pedido no checkout; exibido mascarado (RN42) |
| `refundedAmount` | V5 | RN29 |

## OrderItem — item do pedido (V1)

| Campo | Descrição |
|---|---|
| `ticketType`, `quantity`, `unitPrice`, `subtotal` | `subtotal = quantity × unitPrice` |

## Ticket — ingresso emitido

| Campo | Versão | Descrição |
|---|---|---|
| `id`, `order`, `participant`, `event`, `ticketType`, `status` | V1 | `AVAILABLE` / `SOLD` na V1 |
| `status` (novo ciclo) | V5 | `ISSUED` → `USED` / `CANCELLED` (ADR-0002) |
| `holderName`, `holderEmail` | V4 | Titular por ingresso (RN26); transferência troca o titular (RN23) |
| `qrToken` | V5 | Renovado na transferência (QR anterior deixa de valer) |
| `usedAt`, `usedGate`, `validatedBy` | V5 | RN35 |

## CheckInScan — leitura na entrada (V5; offline V7)

| Campo | Descrição |
|---|---|
| `ticket`, `gate`, `operator` (Membership), `scannedAt` | Toda leitura é registrada |
| `result` | `ACCEPTED` · `REJECTED_USED` · `REJECTED_CANCELLED` · `REJECTED_OTHER_EVENT` · `REJECTED_UNKNOWN` |
| `method` | `QR` · `MANUAL` (RN39) |
| `syncedAt` | V7 — leituras offline; conflito: vale a mais antiga (RN37) |

## Reservation — reserva de estoque (V5)

| Campo | Descrição |
|---|---|
| `ticketType`, `quantity`, `expiresAt`, `source` (`CHECKOUT` / `WAITLIST`) | 10 min no checkout (RN20); 30 min para a fila (RN22) |

## Refund — reembolso (V5)

| Campo | Descrição |
|---|---|
| `order`, `tickets`, `amount`, `feeAmount`, `reason`, `requestedBy`, `status`, `createdAt` | Total (RN28) ou parcial (RN29); comprador recebe ingressos + taxa |

## BankAccount e Payout — repasses (V5)

| Campo | Descrição |
|---|---|
| `organization`, banco, agência, conta, chave Pix, `verified` | CF-A4 |
| `payout`: valor, `releasedAt` (2 dias após o evento), `paidAt`, status | Repasse semanal (MA-A4) |

## WaitlistEntry — fila de espera (V6)

| Campo | Descrição |
|---|---|
| `ticketType`, `user`, `quantity`, `position`, `status` (`WAITING` / `NOTIFIED` / `CONVERTED` / `EXPIRED` / `LEFT`), `notifiedAt` | RN22 |

## NotificationPreference (V6)

| Campo | Descrição |
|---|---|
| `user`, `channel` (`EMAIL` / `PUSH`), `event` (novo pedido, pendente > 24 h, reembolso, lote 90%, esgotado, lembrete, resumo semanal) | CF-A3. **Sem SMS** |

## SupportRequest — chamado (V3)

| Campo | Descrição |
|---|---|
| `protocol`, `organization`, `subject`, `event`, `order`, `message` (até 2.000), `attachment`, `status` | MA-A5, MA-A6 |

## EventPageView — visita à página (V8)

| Campo | Descrição |
|---|---|
| `event`, `visitedAt`, `sessionId` | Base da taxa de conversão (visitas → pedidos) do Painel e do detalhe do evento |
