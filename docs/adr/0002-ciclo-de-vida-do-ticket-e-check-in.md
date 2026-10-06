# ADR-0002 — Ciclo de vida do ticket e check-in

## Status
Accepted — 2026-10-06

## Context
O domínio da V1 define `TicketStatus` como `AVAILABLE` e `SOLD`. O design do produto final precisa de mais do que isso:

- **Check-in:** validar um ingresso na entrada exige saber se ele já foi usado.
- **Reembolso e cancelamento de evento** (RN25, RN28, RN29): ingressos deixam de valer, mas continuam existindo para histórico e auditoria.
- `AVAILABLE` descreve estoque, não ticket. Um ticket só existe depois que o pedido é confirmado (RN11); a disponibilidade já é calculada no `TicketType` (`total − vendidos`).

Também era preciso decidir se um ticket permite reentrada e como o check-in funciona em locais sem internet.

Alternativas consideradas:

1. Manter `AVAILABLE`/`SOLD` e controlar uso e cancelamento com campos booleanos.
2. Novo ciclo de vida explícito no `TicketStatus`.
3. Permitir reentrada por padrão, com contagem de entradas.

## Decision

**1. Ciclo de vida do ticket (a partir da V5):**

```text
ISSUED (Emitido) ──► USED (Utilizado)
       └──────────► CANCELLED (Cancelado)
```

- `ISSUED`: criado quando o pedido é confirmado.
- `USED`: definido no check-in. É estado final.
- `CANCELLED`: reembolso (total ou parcial) ou cancelamento do evento. É estado final.
- Transferência (RN23) troca o titular e invalida o QR anterior; **não** cria status novo.

**2. Sem reentrada:** um ticket `USED` não entra de novo. Uma nova leitura é recusada e registrada. Permitir reentrada fica como possível opção futura por evento.

**3. Check-in offline:** o app baixa a lista de tickets antes do evento e valida localmente quando não há internet; as leituras são sincronizadas depois. Em conflito (o mesmo ticket lido em dois portões sem conexão), **vale a leitura mais antiga**; a outra é registrada como recusada e aparece em "Ocorrências". O desenho existe agora; a implementação do modo offline é da **V7** (concorrência).

**4. Um único app:** o check-in é feito no mesmo app do organizador, pela equipe com o papel Check-in (ADR-0001). Não existe app separado.

## Consequences

Positivas:
- O status do ticket passa a contar a história real do ingresso; relatórios de presença e reembolso saem direto do domínio.
- Regras simples de validar: só `ISSUED` entra.
- A V1 não muda: a Sprint 5 segue com `AVAILABLE`/`SOLD`, como está na spec.

Negativas / pontos de atenção:
- **V5:** migração de `AVAILABLE`/`SOLD` para `ISSUED`/`USED`/`CANCELLED` (ajuste de enum e de dados persistidos desde a V3).
- **V7:** sincronização offline e conflitos entre portões são um problema real de concorrência — é exatamente o tipo de caso que a V7 deve tratar.
- Toda leitura (válida ou recusada) precisa ser registrada com ticket, horário, portão e pessoa da equipe, para auditoria e para o painel ao vivo.

As telas e regras derivadas (CK-A1 a CK-A8, RN35–RN39) estão em [`docs/produto-e-design.md`](../produto-e-design.md).
