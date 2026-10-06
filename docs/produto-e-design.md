# Arclou — Contrato de Produto e Design

> **Objetivo:** garantir que o que está desenhado no Figma é exatamente o que será desenvolvido neste repositório.
>
> **Figma:** [Arclou](https://www.figma.com/design/jFLXJ5S0eonNsXzx5ULmUu/Arclou) — toda tela tem um código (`EV-C3`, `PT-B2`…) e esse código é a referência usada em Issues, PRs e neste documento.

---

## 1. Como este documento funciona

- O **Figma** é a referência visual e de fluxo (o que a pessoa vê e faz).
- Este documento é a referência de **regras, dados e versão**: para cada tela, quais regras de negócio ela exige e a partir de qual versão do backend ela pode existir de verdade.
- [`docs/modelo-de-dominio.md`](modelo-de-dominio.md) diz quais campos cada entidade tem e em que versão entram.
- `docs/v1-java-core.md` continua sendo a especificação detalhada da V1. Quando uma tela depender de algo que a V1 ainda não modela, isso aparece aqui como regra futura (RN16+), nunca como mudança silenciosa da V1.

Regra de manutenção:

```text
Mudou o Figma  → atualizar este documento no mesmo ciclo (PR de docs).
Mudou uma regra → atualizar o Figma antes de abrir a Issue de implementação.
Divergência encontrada → sinalizar e corrigir; não manter inconsistência.
```

As telas representam o **produto final** (V1 → V10). A V1 não tem interface: ela implementa o domínio que essas telas vão consumir a partir da V2.

---

## 2. Superfícies do produto

Decisão registrada em [`docs/adr/0001-superficies-do-produto.md`](adr/0001-superficies-do-produto.md).

| Superfície | Quem usa | Formato | Páginas no Figma |
|---|---|---|---|
| **App do organizador** | `ORGANIZER` e sua equipe | Aplicativo **mobile-only** (430 px de referência), tema escuro | 📱 Painel · 📱 Eventos · 📱 Pedidos · 📱 Mais · 📱 Perfil · 📱 Configurações |
| **Site do participante** | `PARTICIPANT` e visitantes | **Web responsivo**: Desktop 1728 · Tablet 1024 · Mobile 430 | 🌐 Participante (web) |
| Administração da plataforma | `SUPER_ADMIN` | — | Fora do escopo de design por enquanto |

Navegação do app do organizador (Tab bar fixa): **Painel · Eventos · [+ Novo evento] · Pedidos · Mais**.

Navegação do site do participante (Site Header): busca, **Meus ingressos** e conta; visitantes veem **Entrar / Criar conta**.

### Design system

- Cores, espaçamento e raios são **variáveis** no Figma (`Arclou / Color`, `Arclou / Scale`). Tema único escuro; a cor de marca é `primary/600` (#D4FF69).
- Componentes reutilizáveis ficam nas páginas de componentes do Figma (Button, Badge, Text Field, Menu Item, Toast, Site Header, Event Card · Public…).
- QR codes e códigos de barras são sempre **preto no branco**, independentemente do tema, para garantir leitura.

---

## 3. Rótulos de status (domínio ↔ tela)

As telas usam rótulos em português. O código usa os enums do domínio. A correspondência é fixa:

| Enum (domínio) | Rótulo na tela | Tom do badge |
|---|---|---|
| `EventStatus.DRAFT` | Rascunho | Neutro |
| `EventStatus.PUBLISHED` | Publicado | Sucesso |
| `EventStatus.CANCELLED` | Cancelado | Perigo |
| `EventStatus.FINISHED` | Encerrado | Neutro |
| `OrderStatus.PENDING` | Pendente | Atenção |
| `OrderStatus.CONFIRMED` | Confirmado | Sucesso |
| `OrderStatus.CANCELLED` | Cancelado | Neutro |
| `TicketStatus.SOLD` | Emitido | Sucesso |

**A partir da V5** (ADR-0002), o ticket passa a ter o ciclo `ISSUED` (Emitido · Sucesso) → `USED` (Utilizado · Neutro) ou `CANCELLED` (Cancelado · Neutro). A V1 mantém `AVAILABLE`/`SOLD`.

Estados que aparecem nas telas mas **ainda não existem no domínio** estão na seção 6 (decisões em aberto) ou nas regras futuras (seção 5).

---

## 4. Inventário de telas

Legenda de versão: a coluna **Backend** indica a primeira versão em que a tela pode funcionar com dados reais.

### 4.1 App do organizador (mobile)

#### 📱 Painel — `PN`

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| PN-A1 | Primeiro acesso (0/3) | Onboarding: criar evento → configurar ingressos → publicar (RN03, RN04) | V2 |
| PN-A2 | Evento criado (1/3) | Evento em `DRAFT` sem tipos de ingresso | V2 |
| PN-A3 | Evento publicado · sem vendas | Evento `PUBLISHED`, nenhum pedido | V2 |
| PN-A4 | Painel com dados | Receita, ingressos vendidos, alertas (lote quase esgotado, pedidos pendentes, rascunho sem ingressos), próximos eventos, pedidos recentes | V3 / conversão: V8 (até lá o card não aparece) |

#### 📱 Eventos — `EV`

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| EV-A1 | Lista | Abas por status com contagem; cards com capacidade vendida | V2 |
| EV-A2 | Menu ⋯ da linha | Ações dependem do status — Rascunho: continuar editando, pré-visualizar, duplicar · excluir. Publicado: ver, editar, página pública, copiar link, duplicar · pausar, encerrar vendas · cancelar. Encerrado: relatório final, exportar, participantes, duplicar. Cancelado: reembolsos, exportar, duplicar. Ações destrutivas sempre por último. | V2 |
| EV-A3 | Excluir rascunho | Só `DRAFT` pode ser excluído (RN16) | V2 |
| EV-A5 | Busca (resultados / sem resultado) | Busca por nome **ou** cidade | V3 |
| EV-A6 | Filtrar e ordenar | Ordenar por data, criação, nome, mais vendidos; filtros período, formato, cidade | V3 |
| EV-A7 | Filtros aplicados · Menu ⋯ (exportar) | Exportar lista (CSV) respeitando filtros; copiar link da página do organizador | V3 |
| EV-B1–B2 | Novo evento (vazio / validação) | Nome obrigatório (RN01); validação no topo + campo em erro | V2 (validação com exceptions: V1.5) |
| EV-B3 | 1. Detalhes | Nome, categoria, formato, descrição, classificação etária, idioma, capa | V2 / capa: V3 |
| EV-B4–B6 | 2. Data e local (Online / Presencial / Híbrido) | Início e término; endereço com mapa; link de transmissão | V2 |
| EV-B7 | 3. Ingressos | Tipos com nome, preço ≥ 0 (RN08), quantidade > 0 (RN07), janela de vendas (lotes, RN40), exige documento (RN41) e fila de espera (RN22); resumo considera a taxa repassada ao comprador por padrão (RN17) | V2 / janela: V3 / documento: V5 / fila: V6 |
| EV-B8–B9 | 4. Revisão e publicação / Confirmar | Publicar exige ≥ 1 tipo de ingresso (RN04) | V2 |
| EV-C1 | Detalhe · Visão geral | KPIs, alertas, tipos de ingresso, gráfico de vendas, pedidos recentes | V3 / conversão: V8 |
| EV-C2 | Detalhe · Ingressos | Vendidos / total por tipo; disponível = total − vendido; estado da fila de espera de cada tipo (RN22) | V2 / fila: V6 |
| EV-C3 | Detalhe · Pedidos | Pedidos do evento por status | V2 |
| EV-C4 | Detalhe · Participantes | Titulares dos ingressos; dados pessoais (LGPD) | V3 |
| EV-C5 | Detalhe · Check-in (pré-evento) | Abertura do check-in (2 h antes do início); entradas por tipo; regras de validação: busca manual, modo offline, alerta de Estudante e **sem reentrada (sempre ativo)**; a equipe usa o mesmo app (ADR-0002) | V4 |
| EV-D1 | Menu ⋯ do evento (Publicado) | Duplicar, copiar link, QR code, exportar · pausar, encerrar vendas · cancelar | V2 |
| EV-D2 | Copiar link | Toast; link público `arclou.com/e/<slug>` | V2 |
| EV-D3 | Duplicar evento | Cópia nasce como `DRAFT` (RN03) | V2 |
| EV-D4 | Baixar QR code | QR da **página pública** (não é ingresso) | V2 |
| EV-D5 | Exportar relatório | CSV/PDF; sinaliza dados pessoais (LGPD) | V3 |
| EV-D6 | Pausar vendas | Reversível; bloqueia novos pedidos (RN18) | V2 |
| EV-D7 | Encerrar vendas | Irreversível para vendas; evento segue `PUBLISHED` até acontecer (RN18) | V2 |
| EV-D8 | Cancelar evento | `CANCELLED` (RN06/RN12); reembolso de todos os pedidos confirmados (RN25); digitar CANCELAR | V2 / reembolso: V5 |

#### 📱 Pedidos — `PD`

Aba **Pedidos** do Tab bar: todos os pedidos da produtora, de todos os eventos. No app, o valor de um pedido é sempre o **subtotal dos ingressos** (o que o organizador recebe); a taxa de serviço aparece separada, como paga pelo comprador.

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| PD-A1 | Lista | Abas por status (contagem só em Pendentes); filtro por evento; alerta de pendentes há mais de 24 h; agrupado por dia | V2 / filtros: V3 |
| PD-A2 | Pedido confirmado | Comprador, tickets emitidos com titular (RN11, RN26), pagamento (subtotal, taxa, total pago, valor a receber), histórico, liberação para repasse 2 dias após o evento | V2 / pagamento: V5 |
| PD-A3 | Pedido pendente | Ingressos reservados, ainda não emitidos (RN11); vencimento do boleto (RN21); reenviar boleto; cancelar pedido | V5 |
| PD-A4 | Reembolsar (total) | Motivo; impacto: devolução ao comprador, desconto no saldo, ingressos invalidados e devolvidos ao estoque (RN28) | V5 |
| PD-A4 | Reembolso parcial | Seleção dos ingressos a devolver; pedido continua `CONFIRMED` com os restantes (RN29) | V5 |
| PD-A5 | Pedido reembolsado | Pedido `CANCELLED`; tickets invalidados; prazo da devolução | V5 |

#### 📱 Mais — `MA`

Aba **Mais** do Tab bar: conta, produtora e suporte. O perfil e o "Sair da conta" ficam aqui; Configurações (CF-A1) é aberto a partir deste menu.

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| MA-A1 | Menu | Perfil; Produtora (página pública, equipe, pagamentos); Conta (configurações, segurança); Suporte (ajuda, falar com o suporte, termos e privacidade); sair | V2 |
| MA-A2 | Página pública | Logo, capa, nome, endereço da página (`/o/<slug>`; mudar quebra links já compartilhados), cidade, sobre (até 300 caracteres), redes e contato, o que exibir (eventos realizados, contagem de ingressos vendidos) | V3 |
| MA-A3 | Central de ajuda | Busca, categorias, artigos mais buscados, atalho para o suporte | V3 (conteúdo estático) |
| MA-A4 | Artigos de ajuda (mais buscados) | 5 artigos, um por item da lista de MA-A3. O conteúdo cita as regras vigentes — mudou a regra, muda o artigo: **Repasses** (liberação 2 dias após o evento, repasse semanal, RN17) · **Reembolsar pedido** (RN28, RN29; exemplo do parcial do #1042) · **Por que não posso publicar** (RN01, RN04, RN07, RN08; exemplo do Workshop) · **Pausar ou encerrar vendas** (RN18, comparado com cancelar/RN25) · **Convidar para a equipe** (RN33; papéis com o mesmo texto de CF-A5) | V3 |
| MA-A5 | Falar com o suporte | Assunto, evento e pedido relacionados, mensagem (até 2.000 caracteres), anexo (JPG, PNG, PDF até 10 MB), histórico de chamados | V3 / anexo: V3 |
| MA-A6 | Mensagem enviada | Protocolo do chamado; resposta por e-mail em até 1 dia útil | V6 (e-mail) |

#### 📱 Perfil — `PF`

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| PF-A1 | Meu perfil | Nome, e-mail (verificado), telefone, papel (só leitura), perfil público, atalho para Segurança | V2 / verificação: V4 |
| PF-A2 | Editando · Sair sem salvar · Salvo | Salvar habilitado só com alteração; aviso ao sair | V2 |
| PF-A3 | Opções da foto · Ajustar foto | Tirar foto, galeria, remover; recorte circular | V3 |
| PF-A4 | Exclusão bloqueada · Confirmar exclusão | Bloqueia com eventos à venda, saldo a liberar ou propriedade da equipe (RN27); dados pessoais apagados em 30 dias | V4 / V5 |

#### 📱 Configurações — `CF`

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| CF-A1 | Menu de seções | Geral, Notificações, Pagamentos, Equipe, Integrações, Segurança (aberto a partir de MA-A1) | V2 |
| CF-A2 | Geral | Idioma, fuso, moeda, formato de data; padrões de novos eventos: limite por pedido (RN19), política de reembolso (RN24), repassar taxa ao comprador (RN17), fila de espera em tipos novos (RN22) | V3 |
| CF-A3 | Notificações | Canais (e-mail e push — sem SMS) e eventos (novo pedido, pendente > 24 h, reembolso, lote 90%, esgotado, lembrete) | V6 |
| CF-A4 | Pagamentos e repasses | Saldo disponível / a liberar, dados bancários, frequência de repasse, histórico, taxas | V5 |
| CF-A5 | Equipe | Papéis: Proprietário, Administrador, Financeiro, Check-in; convites | V4 |
| CF-A6 | Integrações | Webhooks, chaves de API, pixels de marketing | V4 (chaves) / V6 (webhooks) |
| CF-A7 | Segurança | Senha, 2FA, códigos de recuperação, passkeys, sessões ativas (aparelhos), regras da produtora, atividade recente | V4 |
| CF-B1 | Alterar senha | Força da senha; opção de desconectar outros aparelhos | V4 |
| CF-B2–B3 | Ativar 2FA (conectar autenticador / códigos de recuperação) | No celular: abrir app autenticador + copiar chave; QR só para outro aparelho; 10 códigos de uso único | V4 |
| CF-B4 | Desativar 2FA | Exige senha | V4 |

### 4.2 Site do participante (web: Desktop, Tablet, Mobile)

| Código | Tela | Rota | Regras / dados | Backend |
|---|---|---|---|---|
| PT-A1 | Explorar | `/` | Só eventos `PUBLISHED` (RN05); gratuito = preço 0 (RN08); esgotado | V2 |
| PT-A2 | Página do evento | `/e/<slug>` | Tipos com preço e disponibilidade; prazo de vendas; taxa informada antes do pagamento | V2 |
| PT-A3 | Página da produtora | `/o/<slug>` | Dados editados em MA-A2; eventos próximos (`PUBLISHED`) e realizados (`FINISHED`); cancelados e rascunhos nunca aparecem | V3 |
| PT-B1 | Escolher ingressos | `/e/<slug>/ingressos` | Não passa da disponibilidade (RN09) nem do limite por pedido (RN19); total calculado (RN10) | V2 |
| PT-B2 | Pagamento | `/checkout/<id>` | Reserva de 10 min (RN20); titular por ingresso (RN26); Pix / cartão / boleto | V5 |
| PT-B3 | Aguardando Pix | `/pedidos/<n>/pagamento` | Pedido `PENDING`; ingressos só após confirmação (RN11) | V5 |
| PT-B4 | Pedido confirmado | `/pedidos/<n>` | Pedido `CONFIRMED`; tickets emitidos (RN11) | V5 (V1 já modela a emissão) |
| PT-B5 | Pagamento com cartão | `/checkout/<id>` | Parcelas com juros do emissor | V5 |
| PT-B6 | Cartão recusado | `/checkout/<id>` | Nada cobrado; reserva continua valendo | V5 |
| PT-B7 | Boleto gerado | `/pedidos/<n>/pagamento` | Vencimento antes do evento (RN21) | V5 |
| PT-B8 | Reserva expirou · tipo esgotou | `/checkout/<id>` | RN09 + RN20; alternativas e fila de espera (RN22) | V5 / V7 |
| PT-B9 | Pix expirado | `/pedidos/<n>/pagamento` | Cancelamento automático (RN21); pagamento tardio devolvido | V5 / V6 |
| PT-B10 | Na fila de espera | `/e/<slug>/fila` | Posição na fila, quantidade pedida, como funciona; sair da fila (RN22) | V6 |
| PT-B11 | Sua vez na fila | `/checkout/<id>` | Checkout com reserva exclusiva de 30 min; boleto indisponível (RN22) | V6 |
| PT-C1 | Meus ingressos | `/meus-ingressos` | Próximos / passados, agrupados por evento | V4 |
| PT-C2 | Ingresso | `/meus-ingressos/<id>` | QR do ticket, titular, tipo | V4 |
| PT-C3 | Meus pedidos | `/meus-pedidos` | Consulta de pedidos por status (CA15) | V4 |
| PT-C4 | Transferir ingresso | modal em `/meus-ingressos/<id>` | RN23 | V4 |
| PT-C5 | Ingresso transferido | `/meus-ingressos/<id>` | Ticket deixa a conta de quem transferiu | V4 |

### 4.3 Acesso (página 🔐 Acesso) — `AC`

Duas superfícies, dois cadastros: o **site cria contas `PARTICIPANT`**; o **app cria contas `ORGANIZER`** junto com a produtora. Equipes entram por convite. Tudo isso pertence à V4 (Security).

#### Site do participante (web: Desktop, Tablet, Mobile)

| Código | Tela | Rota | Regras / dados | Backend |
|---|---|---|---|---|
| AC-A1 | Entrar | `/entrar?continuar=<rota>` | Volta para onde a pessoa estava (ex.: escolher ingressos); link para o app de organizadores | V4 |
| AC-A2 | Criar conta | `/criar-conta` | Nome, e-mail, senha (mínimo 8, medidor de força); aceite de termos obrigatório; novidades por e-mail é opcional (LGPD) | V4 |
| AC-A3 | Esqueci a senha | `/esqueci-a-senha` | Envia link de redefinição | V4 / e-mail: V6 |
| AC-A4 | Link enviado | `/esqueci-a-senha/enviado` | Mensagem não revela se o e-mail existe (RN31); reenvio com espera | V4 |
| AC-A5 | Nova senha | `/redefinir-senha?token=…` | Link válido por 30 min (RN31); opção de desconectar outros aparelhos; também usada a partir do app (AC-B5) | V4 |
| AC-A6 | Erro ao entrar | `/entrar` | Mensagem genérica; bloqueio após 5 tentativas (RN30) | V4 |
| AC-A7 | Confirmar e-mail | `/criar-conta/confirmar` | Código de 6 dígitos enviado no cadastro (RN32) | V4 / V6 |

#### App do organizador (mobile)

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| AC-B1 | Entrar | E-mail e senha ou passkey (Face ID/digital); criar conta de organizador | V4 |
| AC-B2 | Código 2FA | Pedido após a senha quando 2FA está ativa; "confiar neste aparelho" por 30 dias; alternativa: código de recuperação (CF-B3) | V4 |
| AC-B3 | Criar conta de organizador | Dados pessoais + produtora (nome, cidade); cria `ORGANIZER` como Proprietário da produtora (RN34) | V4 |
| AC-B4 | Aceitar convite | E-mail do convite é fixo; papel e permissões visíveis antes de aceitar; convite expira em 7 dias (RN33) | V4 |
| AC-B5 | Esqueci a senha | Envia o link; a nova senha é criada na web (AC-A5); 2FA continua exigida depois | V4 |

### 4.4 Check-in (página 📱 Check-in) — `CK`

No app do organizador. Quem tem o papel Check-in entra direto nestas telas (sem Tab bar); organizadores abrem pelo detalhe do evento. Decisões em [`ADR-0002`](adr/0002-ciclo-de-vida-do-ticket-e-check-in.md).

| Código | Tela | Regras / dados | Backend |
|---|---|---|---|
| CK-A1 | Escolher evento e portão | Só eventos com check-in aberto; portão (A · Pista e Estudante, B · Área VIP) com contagem; lista para uso offline | V5 |
| CK-A2 | Scanner | Leitura do QR; contador de presentes; estado da conexão | V5 |
| CK-A3 | Entrada liberada | `ISSUED` → `USED` (RN35); registra horário, portão e quem validou | V5 |
| CK-A3 | Conferir carteirinha | Ingresso de meia-entrada: só vira `USED` depois de "Liberar entrada" (RN38) | V5 |
| CK-A4 | Já utilizado | Ticket `USED`: entrada recusada, mostra quando/onde/quem (RN36) | V5 |
| CK-A5 | Ingresso inválido | Outro evento, `CANCELLED` ou QR desconhecido: entrada recusada | V5 |
| CK-A6 | Busca manual | Por nome, e-mail ou nº do ingresso; exige conferir documento com foto; registrada como manual (RN39) | V5 |
| CK-A7 | Painel ao vivo | Aba Check-in do evento no dia: presentes por tipo e por portão, equipe online, ocorrências (inclui conflito offline) | V5 / V8 (métricas) |
| CK-A8 | Sem internet | Valida com a lista baixada; leituras aguardando envio (RN37) | V7 |

---

## 5. Regras previstas para versões futuras

Estas regras nasceram do design. **Nenhuma entra na V1.** Cada uma será refinada em Issue própria quando a versão indicada chegar.

| Regra | Descrição | Telas | Versão |
|---|---|---|---|
| **RN16 — Exclusão só de rascunho** | Apenas eventos `DRAFT` podem ser excluídos. Publicados são cancelados, nunca apagados. | EV-A3 | V2 |
| **RN17 — Taxa de serviço** | Taxa de 8% por ingresso vendido, mínimo de R$ 2,50 por ingresso; ingressos gratuitos não pagam taxa. Se o organizador repassar a taxa, o comprador paga `subtotal + taxa`; o organizador recebe o subtotal. | PT-A2, PT-B*, CF-A2, CF-A4 | V5 |
| **RN18 — Vendas pausadas/encerradas** | Um evento `PUBLISHED` pode ter vendas **pausadas** (reversível) ou **encerradas** (definitivo). Nos dois casos, não recebe novos pedidos — complementa a RN05. | EV-D6, EV-D7 | V2 |
| **RN19 — Limite por pedido** | Cada pedido aceita no máximo N ingressos (padrão 6, configurável). | PT-B1, CF-A2 | V2 |
| **RN20 — Reserva de estoque** | Ao iniciar o pagamento, a quantidade escolhida fica reservada por 10 minutos. Reserva expirada devolve o estoque. É a forma de aplicar a RN09 com vários compradores simultâneos. | PT-B2, PT-B5, PT-B6, PT-B8 | V5 (concorrência: V7) |
| **RN21 — Expiração de pedido pendente** | Pedido `PENDING` é cancelado automaticamente: Pix após 30 minutos; boleto no vencimento. O boleto precisa vencer com tempo de compensação antes do evento. Pagamento recebido depois do cancelamento é devolvido automaticamente. | PT-B3, PT-B7, PT-B9 | V5 / V6 |
| **RN22 — Fila de espera** | Configurada **por tipo de ingresso** (padrão para tipos novos em CF-A2). Quando um tipo com fila esgota, o participante entra informando a quantidade (até o limite por pedido, RN19); nada é cobrado. Quando a quantidade é liberada (reembolso, pedido expirado), a fila é atendida **em ordem**: a pessoa é avisada por e-mail e tem **30 minutos de reserva exclusiva** (RN20); se não comprar, a vez passa para a próxima e ela sai da fila. Na reserva da fila, só Pix e cartão (boleto não cabe em 30 min). Se as vendas terminarem, a fila é encerrada. | EV-B7, EV-C2, CF-A2, PT-B8, PT-B10, PT-B11 | V6 (concorrência da reserva: V7) |
| **RN23 — Transferência de ingresso** | O titular pode transferir um ticket para outra pessoa (nome + e-mail) até 24 h antes do evento. O QR anterior é invalidado; a transferência não pode ser desfeita por quem transferiu. | PT-C2, PT-C4, PT-C5 | V4 |
| **RN24 — Política de reembolso** | O participante pode pedir reembolso até 7 dias antes do evento (padrão configurável). | CF-A2, PT-B2 | V5 |
| **RN25 — Reembolso no cancelamento** | Cancelar um evento reembolsa todos os pedidos confirmados pelo mesmo meio de pagamento e avisa os participantes. | EV-D8 | V5 / V6 |
| **RN26 — Titular por ingresso** | Cada ticket tem um titular (nome). Por padrão é o comprador; pode ser outra pessoa. | PT-B2, PT-C2, EV-C4 | V4 |
| **RN27 — Exclusão de conta** | Conta de organizador só pode ser excluída sem eventos à venda, sem saldo a liberar e sem ser proprietária de equipe. Dados pessoais são apagados em até 30 dias (LGPD); registros fiscais são mantidos pelo prazo legal. | PF-A4 | V4 / V5 |
| **RN28 — Reembolso feito pelo organizador** | O comprador recebe o valor integral que pagou (ingressos + taxa de serviço) pelo mesmo meio de pagamento, em até 7 dias úteis. O organizador tem descontado apenas o subtotal dos ingressos do saldo a liberar. Reembolso total cancela o pedido (`CANCELLED`) e invalida todos os tickets, que voltam ao estoque. | PD-A4, PD-A5 | V5 |
| **RN29 — Reembolso parcial** | O organizador escolhe quais tickets devolver. Os tickets escolhidos são invalidados e voltam ao estoque; o comprador recebe o valor deles mais a taxa correspondente. O pedido continua `CONFIRMED` com os tickets restantes; o total original é mantido e o valor reembolsado é registrado à parte. Se todos os tickets forem reembolsados, o pedido passa a `CANCELLED` (RN28). | PD-A4 (parcial) | V5 |
| **RN30 — Bloqueio por tentativas** | Após 5 tentativas de login erradas, o acesso à conta fica bloqueado por 15 minutos. A mensagem de erro nunca diz se o e-mail existe. | AC-A6 | V4 |
| **RN31 — Redefinição de senha** | O link de redefinição vale 30 minutos e só pode ser usado uma vez. A resposta ao pedido é sempre a mesma, exista ou não a conta. | AC-A3, AC-A4, AC-A5, AC-B5 | V4 |
| **RN32 — Confirmação de e-mail** | Toda conta nova confirma o e-mail com um código de 6 dígitos antes de comprar ou publicar. | AC-A2, AC-A7, AC-B3 | V4 |
| **RN33 — Convite de equipe** | O convite é enviado para um e-mail específico, define o papel (Proprietário, Administrador, Financeiro, Check-in) e expira em 7 dias. Só esse e-mail pode aceitá-lo. | CF-A5, AC-B4 | V4 |
| **RN34 — Conta única** | Uma conta serve para comprar ingressos e para trabalhar em produtoras (ADR-0003). Cadastro no app cria a conta **e** a produtora, com a pessoa como Proprietária; cadastro no site cria só a conta. A mesma conta entra nas duas superfícies. | AC-A2, AC-B3 | V4 |
| **RN35 — Check-in** | Só tickets `ISSUED` do próprio evento entram, a partir da abertura do check-in (padrão: 2 h antes do início). A leitura válida muda o ticket para `USED` e registra horário, portão e pessoa da equipe. O portão organiza filas e relatórios; não muda a validade do ticket. | CK-A2, CK-A3, CK-A7 | V5 |
| **RN36 — Sem reentrada** | `USED` é final. Nova leitura do mesmo ticket é recusada e registrada como ocorrência. | CK-A4, EV-C5 | V5 |
| **RN37 — Check-in offline** | O app baixa a lista de tickets do evento e valida localmente sem internet, sincronizando depois. Se o mesmo ticket for lido em dois aparelhos offline, vale a leitura mais antiga; a outra vira ocorrência recusada. | CK-A1, CK-A7, CK-A8 | V7 |
| **RN38 — Meia-entrada** | Tickets de tipos marcados com "exige documento" (RN41 — ex.: meia-entrada) exigem que a equipe confira o documento e toque em "Liberar entrada"; só então viram `USED`. Recusar não altera o ticket. | CK-A3 (carteirinha), EV-C5 | V5 |
| **RN39 — Validação manual** | Sem QR, a equipe busca o ticket por nome, e-mail ou número, confere documento com foto e valida; a leitura fica marcada como manual. | CK-A6 | V5 |
| **RN40 — Lotes** | Um lote é um tipo de ingresso com janela de vendas (início e fim). "Criar 2º lote" cria um novo tipo, com novo preço e quantidade. Fora da janela, o tipo não aparece para compra. Abrir o próximo lote automaticamente ao esgotar o anterior fica para depois. | EV-B7, EV-C2, PT-A2 | V3 |
| **RN41 — Exige documento** | Um tipo de ingresso pode ser marcado como "exige documento na entrada" (meia-entrada, PCD e similares). A página do evento avisa o comprador, e o check-in pede conferência (RN38). | EV-B7, PT-A2, CK-A3 | V5 |
| **RN42 — Documento do comprador** | O CPF é pedido só no checkout (não no cadastro), fica no pedido e aparece mascarado para a produtora (LGPD). | PT-B2, PD-A2 | V5 |
| **RN15 (revisão na V4)** | Com a produtora (ADR-0003), a RN15 passa a ser: um usuário só gerencia eventos das produtoras de que é membro, dentro do que o papel permite. | CF-A5, EV-* | V4 |

### Ressalvas importantes para a V1

- **RN10 na V1:** `total = soma(quantidade × preço unitário)`, **sem taxa**. A taxa (RN17) só existe a partir da V5. No app do organizador, o valor do pedido é sempre o subtotal (ex.: pedido #1042 = R$ 320,00); no site, o comprador vê subtotal + taxa (R$ 345,60).
- **Titular na V1:** o participante do ticket é o próprio comprador. Titular diferente (RN26) chega na V4.

---

## 6. Decisões em aberto

Nenhuma decisão de produto em aberto no momento. Novas dúvidas que surgirem no design ou na implementação entram aqui antes de virar Issue.

---

## 7. Dados de exemplo

As telas usam um conjunto fixo de dados para que os números batam entre si. Use os mesmos valores em testes manuais e exemplos de documentação:

- **Tech Summit 2026** — Publicado · seg, 12 out 2026, 09:00–18:00 · Expo Center Norte, São Paulo/SP · 500 ingressos, 412 vendidos · Pista R$ 60,00 (260/300) · VIP R$ 160,00 (92/100) · Estudante R$ 44,00 (60/100).
- **Noite do Jazz** — Publicado, esgotado (180/180). **Meetup Backend BR** — Publicado, gratuito. **Workshop Java Moderno** — Rascunho. **Show Beneficente** — Cancelado. **DevConf 2026** — Encerrado.
- **Pedido #1042** — Ana Souza, 2× VIP, R$ 320,00 (subtotal) / R$ 345,60 (com taxa), Pix, Confirmado; tickets #10421 (Ana Souza) e #10422 (Bruno Souza).
- **Outros pedidos do Tech Summit** — #1041 Léo Martins, 1× Pista, R$ 60,00, boleto, Pendente · #1040 Carla Dias, 4× Pista, R$ 240,00 · #1039 Rafael Lima, 1× Estudante, R$ 44,00 · #1038 Bruno Alves, 1× VIP + 1× Pista, R$ 220,00 · #1037 Marina Costa, 2× Pista, R$ 120,00, Cancelado (cartão recusado).
- **Pendentes da produtora:** 14 (6 do Tech Summit + 8 do Noite do Jazz), número usado no alerta do Painel e na aba Pedidos.
