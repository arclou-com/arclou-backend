# Arclou API — Site do participante

> **Fonte da verdade:** [`participant.yaml`](participant.yaml) (OpenAPI 3.1). Convenções comuns (dinheiro em centavos, datas, cursor, erros RFC 9457, `x-since`): [`README.md`](README.md).
>
> Telas: `PT-*` e `AC-A*` em [`produto-e-design.md`](../produto-e-design.md). Decisão: [ADR-0004](../adr/0004-contrato-de-api-contract-first.md).

## Escopo

Site web responsivo (ADR-0001): descobrir eventos, comprar, pagar (Pix, cartão, boleto, gratuito), meus pedidos, meus ingressos (QR, PDF, carteira do celular), transferência e fila de espera.

**Reaproveitado de `organizer.yaml`** (conta única — RN34): `/auth/*`, `/me`, `/legal/documents/{slug}`, erros (`Problem`), `Money`, `Page` e enums. O cadastro é a mesma operação do app: `POST /auth/signup` **sem** `organization` cria só a conta (AC-A2).

## Operações por versão

| Versão | Operações próprias | O que entra |
|---|---|---|
| V2 | 5 | Explorar, página do evento, criar/consultar checkout, pagar (simulado) |
| V3 | 1 | Página da produtora |
| V4 | 6 | Titulares, meus pedidos, meus ingressos, transferência (+ login/cadastro compartilhados) |
| V5 | 11 | Reserva, Pix, cartão, boleto, troca de forma de pagamento, PDFs, QR, reembolso, webhook do provedor |
| V6 | 5 | Fila de espera, carteira do celular |
| V8 | 1 | Visitas à página do evento (conversão) |

**Antes da V4** não há login: a API roda localmente agindo como o participante de exemplo. **Antes da V5** não há cobrança: `pay` confirma o pedido na hora, como a V1 ("criar pedido → confirmar pedido"), e o checkout é o próprio pedido `PENDING`.

## Fluxo de compra

```text
PT-A2 Página do evento
  │  GET /public/events/{slug}
  ▼
PT-B1 Escolher ingressos ── POST /checkouts ──► Checkout ACTIVE, reservado por 10 min (RN20)
  ▼
PT-B2 Pagamento ── PATCH /checkouts/{id} (CPF, titulares)
  │                POST  /checkouts/{id}/pay  (Idempotency-Key)
  ├── PIX ────► PT-B3  pedido PENDING · QR + copia-e-cola · 30 min ──┐
  ├── BOLETO ─► PT-B7  pedido PENDING · linha digitável + PDF ───────┤ GET /me/orders/{n}/payment
  ├── CARD ───► PT-B4  pedido CONFIRMED (ou 3-D Secure → returnUrl)   │ (polling 5 s no Pix)
  │        └─ 402 ► PT-B6 cartão recusado · reserva continua          │
  └── FREE ───► PT-B4  pedido CONFIRMED                               ▼
                                                   PAID ► PT-B4 · EXPIRED ► PT-B9
```

### Estados do checkout

| `status` | Significado | Tela |
|---|---|---|
| `ACTIVE` | Reserva valendo (`reservedUntil`) | PT-B2, PT-B5, PT-B6, PT-B11 |
| `EXPIRED` | 10 min (ou 30 min da fila) acabaram sem pagamento. Se um tipo esgotou, `unavailable` traz alternativas | PT-B8 |
| `COMPLETED` | Pedido criado (`orderNumber`) | redireciona para o pedido |
| `RELEASED` | A pessoa desistiu (`DELETE`) | — |

PT-B8: "Trocar para Pista" = `POST /checkouts/{id}/change-ticket-type` (mantém quantidade e titulares, nova reserva). "Entrar na fila" = `POST /me/waitlist-entries`.

### Valores

O site **nunca calcula**. `pricing` vem do servidor (RN10, RN17):

```json
{
  "subtotal":   { "amount": 32000, "currency": "BRL" },
  "serviceFee": { "amount": 2560,  "currency": "BRL" },
  "total":      { "amount": 34560, "currency": "BRL" },
  "feePassedToBuyer": true
}
```

Pedido #1042: 2× VIP de R$ 160,00 + taxa de 8% = **R$ 345,60**. No cartão parcelado, o total com juros vem em `InstallmentOption.total` e `CardCharge.totalCharged`.

## Pagamento

| Método | O que o site recebe | O que mostra |
|---|---|---|
| **Pix** | `pix.qrCodePayload` (BR Code EMV) e `expiresAt` (30 min) | QR gerado no cliente a partir do payload + "Copiar código Pix" (no celular, copiar é o principal) |
| **Boleto** | `boleto.digitableLine` (47 dígitos formatados), `barcode` (44 dígitos), `dueDate`, `pdfUrl` | "Copiar linha digitável", código de barras **ITF-25** desenhado a partir de `barcode`, "Baixar boleto (PDF)" |
| **Cartão** | `card.brand`, `last4`, `installments`, `totalCharged` — ou `nextAction` (3-D Secure) | Resumo da compra; 3-D Secure abre `nextAction.url` e volta em `returnUrl` |
| **Gratuito** | Pedido `CONFIRMED` | PT-B4 |

**Cartão — PCI-DSS:** número, validade e CVV vão do navegador **direto para o provedor** (SDK/iframe dele), que devolve um `cardToken`. A Arclou só recebe o token, a bandeira e os 4 últimos dígitos. Parcelas: `GET /checkouts/{id}/installments?bin=` (até 12x, juros do comprador).

**Formas indisponíveis** vêm em `paymentMethods[].available = false` com o motivo:
- `WAITLIST_RESERVATION`: boleto não cabe nos 30 min da fila (RN22);
- `EVENT_TOO_CLOSE`: o boleto não compensaria antes do evento (RN21).

**Trocar a forma de pagamento** de um pedido pendente (PT-B7 "Pagar com Pix"): `POST /me/orders/{n}/payment`. A cobrança anterior é cancelada.

**Confirmação:** o provedor avisa a Arclou em `POST /payments/webhooks/{provider}` (assinatura `X-Signature`, idempotente). O site descobre pelo polling de `GET /me/orders/{n}/payment`. Pagamento que chega depois do pedido cancelado é devolvido (`REFUNDED_LATE` — RN21).

## Ingresso e QR code

- `qr.payload` é um **token opaco** (`arc1_` + ≥ 128 bits aleatórios). Não carrega dados pessoais nem é "decodificável".
- O site desenha o QR a partir do payload e guarda o ingresso aberto no aparelho: o QR aparece **sem internet** (PT-C2).
- No check-in, o app da equipe valida online (`POST /events/{id}/check-in/scans` com `qrToken`) ou offline, comparando o SHA-256 do payload com a lista baixada (`CheckInSnapshot.qrTokenHash`, em `organizer.yaml`).
- **Transferência (RN23)** gera um payload novo; o anterior deixa de valer em todos os lugares (PDF, carteira do celular, cópia offline).
- `GET /me/tickets/{id}/qr` gera a mesma imagem (SVG/PNG) para e-mail e PDF. `GET /me/tickets/{id}/pdf` = "Baixar PDF". `GET /me/tickets/{id}/wallet?platform=APPLE|GOOGLE` = "Adicionar à carteira do celular".

## Erros específicos

| `code` | HTTP | Regra | Tela |
|---|---|---|---|
| `TICKET_TYPE_SOLD_OUT` | 409 | RN09 | PT-B1, PT-B8 |
| `NOT_ENOUGH_AVAILABLE` | 409 | RN09 | PT-B1 (mostra `remaining`) |
| `MAX_PER_ORDER_EXCEEDED` | 409 | RN19 | PT-B1 |
| `SALES_NOT_OPEN` | 409 | RN05, RN18, RN40 | PT-A2, PT-B1 |
| `EMAIL_NOT_VERIFIED` | 403 | RN32 | AC-A7 antes de comprar |
| `RESERVATION_EXPIRED` | 410 | RN20 | PT-B8 |
| `CHECKOUT_ALREADY_COMPLETED` | 410 | — | redireciona ao pedido |
| `BUYER_DOCUMENT_REQUIRED` | 422 | RN42 | PT-B2 (campo CPF) |
| `INVALID_BUYER_DOCUMENT` | 422 | RN42 | PT-B2 |
| `PAYMENT_METHOD_UNAVAILABLE` | 409 | RN21, RN22 | PT-B2, PT-B11 |
| `CARD_DECLINED` | 402 | — | PT-B6 (`declineReason` genérico) |
| `ORDER_NOT_PENDING` | 409 | — | trocar forma de pagamento |
| `TRANSFER_WINDOW_CLOSED` | 409 | RN23 | PT-C4 |
| `TICKET_NOT_TRANSFERABLE` | 409 | RN23 | PT-C4 (usado, cancelado ou já transferido) |
| `REFUND_WINDOW_CLOSED` | 409 | RN24 | pedido de reembolso |
| `WAITLIST_NOT_AVAILABLE` | 409 | RN22 | PT-B8 (fila desligada ou tipo com estoque) |
| `ALREADY_IN_WAITLIST` | 409 | RN22 | PT-B8 |

## Mapa tela → endpoint

### Descobrir (PT-A)

| Tela | Endpoints |
|---|---|
| PT-A1 Explorar | `GET /public/events` (`featured=true` para "Em destaque"; `meta.cities` e `meta.categories` para os filtros) |
| PT-A2 Página do evento | `GET /public/events/{slug}` · (V8) `POST /public/events/{slug}/views` |
| PT-A3 Página da produtora | `GET /public/organizations/{slug}` |

### Comprar (PT-B)

| Tela | Endpoints |
|---|---|
| PT-B1 Escolher ingressos | `GET /public/events/{slug}` · `POST /checkouts` |
| PT-B2 Pagamento | `GET /checkouts/{id}` · `PATCH /checkouts/{id}` · `POST /checkouts/{id}/pay` |
| PT-B3 Aguardando Pix | `GET /me/orders/{n}/payment` (polling) |
| PT-B4 Pedido confirmado | `GET /me/orders/{n}` |
| PT-B5 Pagamento com cartão | `GET /checkouts/{id}/installments?bin=` · `POST …/pay` (`CARD`) |
| PT-B6 Cartão recusado | resposta `402` do `pay` |
| PT-B7 Boleto gerado | `GET /me/orders/{n}/payment` · `GET /me/orders/{n}/boleto.pdf` · `POST /me/orders/{n}/payment` ("Pagar com Pix") |
| PT-B8 Reserva expirou · tipo esgotou | `GET /checkouts/{id}` (`unavailable`) · `POST …/change-ticket-type` · `POST /me/waitlist-entries` · `DELETE /checkouts/{id}` |
| PT-B9 Pix expirado | `GET /me/orders/{n}/payment` (`EXPIRED`) → `POST /checkouts` de novo |
| PT-B10 Na fila de espera | `GET /me/waitlist-entries/{id}` · `DELETE /me/waitlist-entries/{id}` |
| PT-B11 Sua vez na fila | `GET /me/waitlist-entries/{id}` (`checkoutId`) → fluxo do PT-B2 (sem boleto) |

### Meus ingressos e pedidos (PT-C)

| Tela | Endpoints |
|---|---|
| PT-C1 Meus ingressos | `GET /me/tickets?scope=UPCOMING\|PAST` |
| PT-C2 Ingresso | `GET /me/tickets/{id}` · `…/pdf` · `…/wallet?platform=` · `…/qr` |
| PT-C3 Meus pedidos | `GET /me/orders?status=` · `GET /me/orders/{n}` · `GET /me/orders/{n}/receipt.pdf` |
| PT-C4 Transferir ingresso | `POST /me/tickets/{id}/transfer` |
| PT-C5 Ingresso transferido | `GET /me/tickets/{id}` (`transfer.direction = SENT`, sem `qr`) |
| *(sem tela)* Pedir reembolso | `POST /me/orders/{n}/refund-requests` — ver "Lacuna" abaixo |

### Acesso (AC-A)

| Tela | Endpoints |
|---|---|
| AC-A1 Entrar | `POST /auth/login` (→ `/auth/login/2fa` se a conta tiver 2FA) — depois volta para `?continuar=` |
| AC-A2 Criar conta | `POST /auth/signup` **sem** `organization` · `GET /legal/documents/{slug}` |
| AC-A3 / A4 Esqueci a senha | `POST /auth/password/forgot` |
| AC-A5 Nova senha | `POST /auth/password/reset` |
| AC-A6 Erro ao entrar | `401` genérico · `423` com `retryAfterSeconds` (RN30) |
| AC-A7 Confirmar e-mail | `POST /auth/email/verify` · `POST /auth/email/resend` |

## Lacuna conhecida

**Pedido de reembolso pelo participante (RN24):** a regra existe e o endpoint está no contrato, mas o fluxo não foi desenhado. Registrado em `produto-e-design.md` §6. Até a tela existir, o reembolso é feito pela produtora (PD-A4).

## Fora do escopo

- Cupons de desconto, meia-entrada com validação online de carteirinha, cartão salvo e Apple Pay/Google Pay como meio de pagamento: não estão no design.
- Escolha do provedor de pagamento: decisão da V5 (provável ADR). O contrato não depende do provedor.
