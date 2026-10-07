# Arclou API — Contrato do app do organizador

> **Fonte da verdade:** [`openapi.yaml`](openapi.yaml) (OpenAPI 3.1). Este guia explica as convenções e mostra **qual endpoint cada tela usa**.
>
> Decisão de trabalhar contract-first: [ADR-0004](../adr/0004-contrato-de-api-contract-first.md). Telas e regras: [`produto-e-design.md`](../produto-e-design.md). Campos por versão: [`modelo-de-dominio.md`](../modelo-de-dominio.md).

## Escopo

- **Dentro:** todo o app mobile do organizador (ADR-0001): acesso, Painel, Eventos, Pedidos, Mais, Perfil, Configurações e Check-in.
- **Fora:** o site do participante (PT-\*, AC-A\*), que terá um contrato próprio. Os dois compartilham contas (RN34), domínio e erros, mas os recursos e as permissões são diferentes.

## Como o contrato evolui por versão

O contrato descreve o **produto final**. O backend o implementa por partes:

| Campo | Significado |
|---|---|
| `x-since` | Primeira versão do backend que implementa a operação (V2…V8). Antes disso, o app usa mock gerado do próprio `openapi.yaml`. |
| `x-roles` | Papéis da produtora que podem chamar a operação. Sem `x-roles` = qualquer usuário autenticado (nos próprios dados). |

| Versão | Operações | O que entra |
|---|---|---|
| V2 | 20 | CRUD de eventos e tipos de ingresso, ações do evento, pedidos (leitura), `/me` |
| V3 | 17 | Painel, visão geral, participantes, busca, exportações, capas, ajuda, suporte, documentos legais |
| V4 | 40 | Login, cadastro, 2FA, passkeys, sessões, produtora, equipe, chaves de API |
| V5 | 16 | Reembolso, cancelamento e reenvio de cobrança, check-in online, saldo e repasses |
| V6 | 14 | Notificações, preferências, push, reenvio de ingressos, webhooks, pixels |
| V7 | 2 | Check-in offline (snapshot e sincronização) |
| V8 | 0 | Só preenche `conversionRate` (hoje `null`) |

Regras para não quebrar o contrato:

- Campo que ainda não existe no backend vem `null` (ou fora da resposta, se for opcional) — nunca com outro formato.
- Mudança que quebra o app (remover campo, mudar tipo, renomear) exige `/v2` na URL e um ADR.
- Campo novo em resposta não é quebra; o app deve ignorar campos desconhecidos.

### V2 e V3: antes da produtora existir

Pelo ADR-0003, `Organization` só entra na V4. Para as rotas `/organizations/{organizationId}/…` já valerem na V2:

- `GET /me` devolve **uma** `membership` com papel `OWNER`, cujo `organizationId` é derivado do próprio usuário organizador.
- Na V4, a migration cria a produtora **com o mesmo id**. O app não percebe a troca.
- Até a V4 não há autenticação: a API roda só localmente, agindo como o usuário de exemplo configurado. `x-roles` passa a ser aplicado na V4.

### Status do ingresso antes da V5

O ciclo `ISSUED → USED / CANCELLED` (ADR-0002) entra na V5. Antes disso, um ingresso vendido (`SOLD` na V1) aparece na API como `ISSUED`.

## Convenções

| Tema | Regra |
|---|---|
| Base | `https://api.arclou.com/v1` · JSON UTF-8 · campos em `camelCase` · enums em `UPPER_SNAKE_CASE` |
| IDs | Strings opacas com prefixo (`evt_…`, `ord_…`). O número visível do pedido é `number` (`"1042"`) |
| Dinheiro | Sempre `Money`: `{ "amount": 32000, "currency": "BRL" }`, em **centavos**. O app só formata. Todo cálculo (total, taxa, reembolso) vem do servidor (RN10, RN17) |
| Datas | ISO-8601 com fuso (`2026-10-12T19:00:00-03:00`); datas puras em `YYYY-MM-DD`. O app exibe no fuso da produtora (`settings.timezone`) |
| Paginação | Cursor: `?cursor=…&limit=20` (máx. 100). Resposta `{ data, page: { nextCursor, hasMore }, meta? }` |
| Listas com abas | `meta.counts` traz a contagem de cada aba (ex.: `Publicados 3`), já respeitando busca e filtros |
| Filtros múltiplos | Valores separados por vírgula: `?paymentMethod=PIX,CARD` |
| Uploads | `multipart/form-data`, campo `file`. Imagens já recortadas no app |
| Exportações | Assíncronas: `202` + `ExportJob`; o arquivo vai para o e-mail de quem pediu |
| Idempotência | Ações com dinheiro ou irreversíveis exigem `Idempotency-Key` (UUID): publicar, cancelar evento, reembolsar. Leituras de check-in usam `clientScanId` |
| Concorrência | `GET /events/{id}` devolve `etag`; o `PATCH` aceita `If-Match`. Conflito = `412` e o app recarrega |
| Ações | Transições de estado são `POST` em sub-recurso verbal (`/publish`, `/sales/pause`, `/cancel`), nunca `PATCH status` |

## Autenticação (V4)

```text
POST /auth/login ──► 200 TokenPair
                └──► 200 { twoFactorRequired, challengeToken } ──► POST /auth/login/2fa ──► TokenPair
POST /auth/signup ──► verificationToken ──► POST /auth/email/verify (código de 6 dígitos) ──► TokenPair
```

- **Access token** JWT de 15 min no header `Authorization: Bearer …`. **Refresh token** rotativo em `/auth/refresh`, guardado no Keychain/Keystore.
- `401` em qualquer chamada → o app tenta um refresh; se falhar, volta para AC-B1.
- Login e redefinição de senha nunca dizem se o e-mail existe. Bloqueio de 15 min após 5 tentativas (RN30) = `423` com `retryAfterSeconds`.

## Erros

Formato [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) (`application/problem+json`):

```json
{
  "type": "https://api.arclou.com/problems/event-not-publishable",
  "title": "O evento ainda não pode ser publicado",
  "status": 409,
  "code": "EVENT_WITHOUT_TICKET_TYPES",
  "rule": "RN04",
  "detail": "Cadastre pelo menos um tipo de ingresso."
}
```

| Status | Quando | O que o app faz |
|---|---|---|
| `401` | Token ausente/expirado; credenciais erradas | Refresh ou volta ao login; em login, mensagem genérica |
| `403` | O papel não permite | Esconde a ação; se chegar aqui, toast "Sem permissão" |
| `404` | Não existe **ou** é de outra produtora (RN15 — não revela existência) | Estado vazio |
| `409` | Regra de negócio (`code` + `rule`) | Mostra `detail` no sheet/tela da ação |
| `410` | Link, convite ou código expirado (RN31, RN33) | Tela de expirado com "Pedir novo" |
| `412` | Evento alterado por outra pessoa | Recarrega e avisa |
| `422` | Campos inválidos — `errors[].field` aponta o campo | Erro embaixo de cada campo |
| `423` | Conta bloqueada (RN30) | AC-A6 com contagem regressiva |
| `429` | Limite de chamadas | Espera `Retry-After` |

Códigos de regra mais usados:

| `code` | Regra | Onde |
|---|---|---|
| `EVENT_WITHOUT_TICKET_TYPES` | RN04 | Publicar |
| `EVENT_NOT_DRAFT` | RN16 | Excluir rascunho |
| `EVENT_NOT_PUBLISHED` | RN18 | Pausar/encerrar vendas |
| `EVENT_ALREADY_CANCELLED` | RN06 | Cancelar evento |
| `QUANTITY_BELOW_SOLD` | RN09 | Editar tipo de ingresso |
| `TICKET_TYPE_HAS_SALES` | RN09 | Remover tipo |
| `ORDER_NOT_PENDING` | — | Cancelar pedido / reenviar cobrança |
| `REFUND_WINDOW_CLOSED` | RN24 | Reembolsar |
| `REFUND_TICKET_USED` | RN28 | Reembolso parcial com ingresso já usado |
| `LAST_OWNER` | ADR-0003 | Remover/rebaixar o único `OWNER` |
| `ACCOUNT_DELETION_BLOCKED` | RN27 | Excluir conta |
| `EMAIL_ALREADY_REGISTERED` | RN14 | Cadastro, convite |

Leitura de check-in recusada **não** é erro HTTP: volta `200` com `result` (`REJECTED_USED`, `REJECTED_CANCELLED`…), porque é um resultado esperado da operação.

## Permissões por papel

| Área | OWNER | ADMIN | FINANCE | CHECK_IN |
|---|:-:|:-:|:-:|:-:|
| Painel, visão geral do evento | ✓ | ✓ | ✓ | — |
| Criar/editar/publicar eventos e ingressos | ✓ | ✓ | — | — |
| Pausar, encerrar e cancelar evento | ✓ | ✓ | — | — |
| Ver pedidos e participantes | ✓ | ✓ | ✓ | — |
| Reembolsar | ✓ | ✓ | ✓ | — |
| Cancelar pedido pendente, reenviar ingressos/cobrança | ✓ | ✓ | — | — |
| Validar ingressos (scanner, busca manual) | ✓ | ✓ | — | ✓ |
| Painel ao vivo e configuração do check-in | ✓ | ✓ | — | — |
| Saldo, repasses e frequência | ✓ | — | ✓ | — |
| Editar dados bancários, criar/revogar chaves de API | ✓ | — | — | — |
| Página pública, configurações, equipe, webhooks | ✓ | ✓ | — | — |
| Regras de segurança da produtora | ✓ | — | — | — |

O app usa `GET /me` → `memberships[].role` para esconder o que o papel não pode fazer. O servidor sempre revalida.

## Mapa tela → endpoint

### Acesso (AC-B)

| Tela | Endpoints |
|---|---|
| AC-B0 Abertura | — |
| AC-B1 Entrar | `POST /auth/login` · `POST /auth/passkey/options` + `/verify` |
| AC-B2 Código 2FA | `POST /auth/login/2fa` |
| AC-B3 Criar conta de organizador | `POST /auth/signup` · `GET /legal/documents/{slug}` |
| AC-B4 Aceitar convite | `GET /invitations/{token}` · `POST …/accept` · `POST …/decline` |
| AC-B5 Esqueci a senha | `POST /auth/password/forgot` · `POST /auth/password/reset` |
| AC-B6 Confirmar e-mail | `POST /auth/email/verify` · `POST /auth/email/resend` |

### Painel (PN)

| Tela | Endpoints |
|---|---|
| PN-A1…A4 Painel (todos os estágios) | `GET /organizations/{id}/dashboard` (`stage` decide o layout) |
| PN-B1 Notificações | `GET /me/notifications` · `POST /me/notifications/read-all` · `POST /me/notifications/{id}/read` |
| PN-B2 Buscas recentes | — (guardadas no aparelho) |
| PN-B3 Resultados da busca | `GET /organizations/{id}/search?q=` |

### Eventos (EV)

| Tela | Endpoints |
|---|---|
| EV-A1 Lista, abas | `GET /organizations/{id}/events?status=` (`meta.counts` nas abas) |
| EV-A2 Menu ⋯ da linha | ações do EV-D |
| EV-A3 Excluir rascunho | `DELETE /events/{id}` |
| EV-A5 Busca | `GET …/events?q=` |
| EV-A6 Filtrar e ordenar | `GET …/events?period=&format=&city=&sort=` |
| EV-A7 Exportar | `POST /organizations/{id}/exports` (`type: EVENTS`) |
| EV-A8 Lista vazia | `GET …/events` com `data: []` |
| EV-B1…B6 Criar/editar (etapas 1 e 2) | `POST /organizations/{id}/events` · `PATCH /events/{id}` · `PUT /events/{id}/cover` |
| EV-B7 3. Ingressos | `GET/POST /events/{id}/ticket-types` · `PATCH/DELETE /ticket-types/{id}` |
| EV-B8 4. Revisar e publicar | `GET /events/{id}` (`publishChecklist`) · `POST /events/{id}/publish` |
| EV-B9 Publicado | resposta do `publish` (`publicUrl`) |
| EV-C1 Visão geral | `GET /events/{id}` · `GET /events/{id}/overview` |
| EV-C2 Ingressos | `GET /events/{id}/ticket-types` |
| EV-C3 Pedidos | `GET /organizations/{id}/orders?eventId=` |
| EV-C4 Participantes | `GET /events/{id}/attendees` |
| EV-C5 Check-in (pré-evento) | `GET/PUT /events/{id}/check-in/settings` |
| EV-D2 Copiar link | `publicUrl` do evento (sem chamada) |
| EV-D3 → EV-E4 Duplicar | `POST /events/{id}/duplicate` |
| EV-D4 Baixar QR code | `GET /events/{id}/qr-code?format=&size=` |
| EV-D5 → EV-E5 Exportar relatório | `POST /events/{id}/reports` |
| EV-D6 → EV-E1 Pausar / retomar | `POST /events/{id}/sales/pause` · `POST …/sales/resume` |
| EV-D7 → EV-E2 Encerrar vendas | `POST /events/{id}/sales/close` |
| EV-D8 → EV-E3 Cancelar evento | `POST /events/{id}/cancel` (progresso em `cancellation.refunds`) |

### Pedidos (PD)

| Tela | Endpoints |
|---|---|
| PD-A1 Lista, abas, alerta de pendentes | `GET /organizations/{id}/orders` (`meta.counts`, `meta.pendingOver24h`) |
| PD-A2 / A3 / A5 / A8 Detalhe | `GET /orders/{id}` |
| PD-A3 Reenviar boleto | `POST /orders/{id}/resend-payment` |
| PD-A4 Reembolsar (total/parcial) | `POST /orders/{id}/refunds/preview` · `POST /orders/{id}/refunds` |
| PD-A6 Reenviar ingressos | `POST /orders/{id}/resend-tickets` |
| PD-A7 → A8 Cancelar pendente | `POST /orders/{id}/cancel` |
| PD-B1…B3 Evento, filtros, busca | `GET …/orders?eventId=&period=&paymentMethod=&sort=&q=` |
| PD-B4 Exportar | `POST /organizations/{id}/exports` (`type: ORDERS`) |

### Mais (MA), Perfil (PF) e Configurações (CF)

| Tela | Endpoints |
|---|---|
| MA-A1 Menu | `GET /me` (papel define os itens) |
| MA-A2 Página pública | `GET/PATCH /organizations/{id}` · `PUT /organizations/{id}/images/{logo\|cover}` |
| MA-A3 Central de ajuda | `GET /help/articles` |
| MA-A4 Artigo | `GET /help/articles/{slug}` · `POST …/feedback` |
| MA-A5 → A6 Falar com o suporte | `POST /support/tickets` · `GET /support/tickets` |
| MA-A7 / A8 Termos e privacidade | `GET /legal/documents` · `GET /legal/documents/{slug}` |
| PF-A1 Meu perfil | `GET /me` |
| PF-A2 Editar | `PATCH /me` |
| PF-A3 Foto | `PUT /me/photo` · `DELETE /me/photo` |
| PF-A4 Excluir conta | `GET /me/deletion-eligibility` · `POST /me/deletion` |
| CF-A2 Geral | `PATCH /organizations/{id}` (`settings`) |
| CF-A3 Notificações | `GET/PUT /me/notification-preferences` |
| CF-A4 Pagamentos e repasses | `GET /organizations/{id}/balance` · `GET/PUT …/bank-account` · `PUT …/payout-settings` · `GET …/payouts` |
| CF-A5 Equipe | `GET …/members` · `POST …/invitations` · `POST …/invitations/{id}/resend` · `PATCH/DELETE …/members/{id}` |
| CF-A6 Integrações | `…/webhooks` (+ `/test`) · `…/api-keys` · `…/pixels` |
| CF-A7 Segurança | `GET /me/sessions` · `DELETE /me/sessions[/{id}]` · `…/recovery-codes` · `/me/passkeys` · `PATCH /organizations/{id}` (`security`) · `GET …/activity` |
| CF-B1 Alterar senha | `PUT /me/password` |
| CF-B2 / B3 Ativar 2FA | `POST /me/2fa/setup` · `POST /me/2fa/activate` |
| CF-B4 Desativar 2FA | `DELETE /me/2fa` |

### Check-in (CK)

| Tela | Endpoints |
|---|---|
| CK-A1 Escolher evento e portão | `GET /me/check-in/events` · (V7) `GET /events/{id}/check-in/snapshot` |
| CK-A2 Scanner | `POST /events/{id}/check-in/scans` (`method: QR`) |
| CK-A3 Entrada liberada | `result: ACCEPTED` |
| CK-A3 Conferir carteirinha | `result: REQUIRES_DOCUMENT` → `POST /check-in/scans/{scanId}/decision` |
| CK-A4 Já utilizado | `result: REJECTED_USED` + `previousUse` |
| CK-A5 Ingresso inválido | `REJECTED_CANCELLED` · `REJECTED_OTHER_EVENT` · `REJECTED_UNKNOWN` |
| CK-A6 Busca manual | `GET /events/{id}/check-in/tickets?q=` · `POST …/scans` (`method: MANUAL`) |
| CK-A7 Painel ao vivo | `GET /events/{id}/check-in/live` (polling de 10 s) |
| CK-A8 Sem internet | validação local pelo snapshot · `POST /events/{id}/check-in/scans/batch` ao reconectar |

## Como usar

```bash
# Validar o contrato (o mesmo comando roda na CI)
npx @redocly/cli lint docs/api/openapi.yaml

# Ver a documentação navegável
npx @redocly/cli preview -d docs/api

# App: servidor mock a partir do contrato
npx @stoplight/prism-cli mock docs/api/openapi.yaml
```

Na V2, o backend valida as respostas contra este arquivo nos testes de integração; um endpoint só está pronto quando bate com o contrato.

## Como mudar o contrato

1. A mudança nasce de uma tela nova/alterada (`produto-e-design.md`) ou de uma regra nova.
2. PR alterando `openapi.yaml` **e** este guia (mapa e tabelas), com o lint verde.
3. Mudança que quebra o app exige ADR e nova versão da URL.
