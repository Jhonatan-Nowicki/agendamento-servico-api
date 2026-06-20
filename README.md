# Agendamento de Serviços API

API REST desenvolvida com Java e Spring Boot para gerenciamento de clientes, profissionais, especialidades, serviços e agendamentos.

O projeto foi construído aplicando conceitos de arquitetura em camadas, DTOs para transporte de dados, validação com Bean Validation, documentação OpenAPI/Swagger, tratamento centralizado de exceções e testes unitários com JUnit 5 e Mockito.

---

# Objetivo

Fornecer uma API para gerenciamento de agendas de prestação de serviços, permitindo o cadastro de clientes, profissionais, especialidades e serviços, além do controle completo de agendamentos com validações de regras de negócio.

---

# Tecnologias Utilizadas

* Java 17
* Spring Boot 3.5.15
* Spring Data JPA
* PostgreSQL
* Docker
* Maven
* Bean Validation
* Lombok
* SpringDoc OpenAPI (Swagger)
* JUnit 5
* Mockito

---

# Arquitetura da Aplicação

A aplicação segue o padrão de arquitetura em camadas:

```text
Cliente HTTP
      │
      ▼
Controller
      │
      ▼
DTO Request
      │
      ▼
Service
      │
      ▼
Repository
      │
      ▼
PostgreSQL
      │
      ▲
Entity
      │
      ▼
DTO Response
      │
      ▼
Cliente HTTP
```

## Responsabilidades

### Controller
Responsável por receber requisições HTTP e retornar respostas da API.

### DTO Request
Responsável pela entrada de dados e validações iniciais.

### Service
Responsável pelas regras de negócio da aplicação.

### Repository
Responsável pelo acesso aos dados utilizando Spring Data JPA.

### Entity
Representa os dados persistidos no banco de dados.

### DTO Response
Responsável por retornar apenas as informações necessárias para o cliente, evitando exposição direta das entidades.

---

# Modelo de Domínio

```text
Cliente
    │
    │
    ▼
Agendamento
 ▲          ▲
 │          │
 │          │
Profissional  Serviço
      │         │
      ▼         ▼
Especialidade
```

## Relacionamentos

### Cliente
Representa o usuário que agenda um serviço.

### Profissional
Representa o prestador responsável pela execução do serviço.

### Especialidade
Define uma área de atuação do profissional.

Exemplos:
* Corte Masculino
* Barba
* Manicure
* Pedicure

### Serviço
Representa um serviço oferecido e está vinculado a uma especialidade.

### Agendamento
Relaciona:
* Cliente
* Profissional
* Serviço
* Horário de início
* Horário de término
* Status

---

# Endpoints da API

## Clientes — `/api/clientes`

| Método | Endpoint              | Descrição                      |
|--------|------------------------|---------------------------------|
| POST   | /api/clientes          | Criar novo cliente              |
| GET    | /api/clientes          | Listar todos os clientes ativos |
| GET    | /api/clientes/{id}     | Buscar cliente por ID           |
| PUT    | /api/clientes/{id}     | Atualizar cliente               |
| DELETE | /api/clientes/{id}     | Desativar cliente (soft delete) |

## Especialidades — `/api/especialidades`

| Método | Endpoint                  | Descrição                              |
|--------|----------------------------|------------------------------------------|
| POST   | /api/especialidades        | Criar nova especialidade                |
| GET    | /api/especialidades        | Listar todas as especialidades ativas   |
| GET    | /api/especialidades/{id}   | Buscar especialidade por ID             |
| PUT    | /api/especialidades/{id}   | Atualizar especialidade                 |
| DELETE | /api/especialidades/{id}   | Desativar especialidade (soft delete)   |

## Profissionais — `/api/profissionais`

| Método | Endpoint                              | Descrição                                  |
|--------|-----------------------------------------|-----------------------------------------------|
| POST   | /api/profissionais                      | Criar novo profissional                       |
| GET    | /api/profissionais                      | Listar todos os profissionais ativos          |
| GET    | /api/profissionais/{id}                 | Buscar profissional por ID                    |
| GET    | /api/profissionais/{id}/disponibilidade | Consultar horários já ocupados (param: `data`)|
| PUT    | /api/profissionais/{id}                 | Atualizar profissional                        |
| DELETE | /api/profissionais/{id}                 | Desativar profissional (soft delete)          |

## Serviços — `/api/servicos`

| Método | Endpoint                              | Descrição                          |
|--------|-----------------------------------------|---------------------------------------|
| POST   | /api/servicos                           | Criar novo serviço                    |
| GET    | /api/servicos                           | Listar todos os serviços ativos       |
| GET    | /api/servicos/{id}                      | Buscar serviço por ID                 |
| GET    | /api/servicos/especialidade/{id}        | Listar serviços por especialidade     |
| PUT    | /api/servicos/{id}                      | Atualizar serviço                     |
| DELETE | /api/servicos/{id}                      | Desativar serviço (soft delete)       |

## Agendamentos — `/api/agendamentos`

| Método | Endpoint                                  | Descrição                              |
|--------|---------------------------------------------|--------------------------------------------|
| POST   | /api/agendamentos                           | Criar novo agendamento                     |
| GET    | /api/agendamentos                           | Listar todos os agendamentos               |
| GET    | /api/agendamentos/{id}                      | Buscar agendamento por ID                  |
| GET    | /api/agendamentos/cliente/{id}              | Listar agendamentos por cliente            |
| GET    | /api/agendamentos/profissional/{id}         | Listar agendamentos por profissional       |
| PATCH  | /api/agendamentos/{id}/cancelar             | Cancelar agendamento                       |
| PATCH  | /api/agendamentos/{id}/concluir             | Marcar agendamento como concluído          |

---

# Regras de Negócio

## Clientes
* Não permite cadastro de clientes com e-mail duplicado.

## Profissionais
* Não permite cadastro de profissionais com e-mail duplicado.
* Todas as especialidades informadas devem existir e estar ativas.
* Não é possível desativar um profissional que possua agendamentos futuros com status `AGENDADO`.

## Especialidades
* Não permite nomes duplicados.

## Serviços
* Devem estar vinculados a uma especialidade válida e ativa.

## Agendamentos
* Cliente, profissional e serviço informados devem existir e estar ativos.
* O profissional deve possuir a especialidade necessária para executar o serviço.
* Não é permitido criar agendamentos em datas passadas.
* Não é permitido conflito de horários para o mesmo profissional — `dataHoraFim` é calculada automaticamente como `dataHoraInicio + duracaoMinutos` do serviço.

### Exemplo de conflito de horário

```text
Agendamento existente:
09:00 às 09:30

Tentativa de novo agendamento:
09:15 às 09:45

Resultado:
409 Conflict — ConflitoException
```

## Cancelamentos
* Não é permitido cancelar agendamentos com status `CONCLUIDO`.
* O cancelamento exige antecedência mínima de 2 horas em relação ao horário de início.

## Conclusão
* Não é permitido concluir agendamentos com status `CANCELADO`.

---

# Tratamento de Exceções

A aplicação utiliza tratamento centralizado de exceções através do `GlobalExceptionHandler`, que padroniza todas as respostas de erro no seguinte formato:

```json
{
  "status": 404,
  "mensagem": "Cliente não encontrado",
  "dataHora": "2026-06-20T14:32:10"
}
```

| Exceção                          | Status HTTP | Quando ocorre                                                        |
|-----------------------------------|-------------|------------------------------------------------------------------------|
| `MethodArgumentNotValidException` | 400         | Dados inválidos (Bean Validation nos DTOs de Request)                  |
| `RecursoNaoEncontradoException`   | 404         | Recurso não encontrado (ou inativo)                                    |
| `ConflitoException`               | 409         | E-mail/nome duplicado, conflito de horário                             |
| `RegraDeNegocioException`         | 422         | Violação de regra de negócio (ex: cancelamento fora do prazo)          |
| `Exception` (genérico)            | 500         | Erro interno não tratado                                                |

---

# Banco de Dados

Banco utilizado:

```text
PostgreSQL
```

Configuração padrão (pode ser sobrescrita pelas variáveis de ambiente `DB_USERNAME` e `DB_PASSWORD`):

```text
Database: agendamento_db
Host: localhost
Porta: 5432
Usuário: postgres
Senha: postgres
```

---

# Executando com Docker

```bash
  docker compose up -d
```

Ou, alternativamente, subindo apenas o container do banco manualmente:

```bash
docker run --name postgres-agendamento \
  -e POSTGRES_DB=agendamento_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres
```

---

# Executando o Projeto

## Clonar o repositório

```bash
  git clone https://github.com/Jhonatan-Nowicki/agendamento-servico-api.git
```

## Entrar na pasta

```bash
  cd agendamento-servico-api
```

## Subir o banco de dados

```bash
  docker compose up -d
```

## Executar a aplicação

```bash
  ./mvnw spring-boot:run
```

Ou executar diretamente pela IDE, a partir da classe `AgendamentoServicosApiApplication`.

A aplicação sobe por padrão na porta `8080`.

---

# Documentação da API (Swagger)

Com a aplicação rodando, a documentação interativa está disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação contém todos os endpoints da aplicação, parâmetros, DTOs, respostas e códigos HTTP possíveis, através das anotações `@OpenAPIDefinition`, `@Tag`, `@Operation` e `@ApiResponse`.

---

# Exemplos de Uso da API

## Especialidades

### Criar Especialidade

`POST /api/especialidades` → `201 Created`

**Request**
```json
{
  "nome": "Corte Masculino",
  "descricao": "Serviços de corte de cabelo masculino"
}
```

**Response**
```json
{
  "id": 1,
  "nome": "Corte Masculino",
  "descricao": "Serviços de corte de cabelo masculino",
  "ativo": true
}
```

---

## Clientes

### Criar Cliente

`POST /api/clientes` → `201 Created`

**Request**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "41999999999"
}
```

**Response**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "41999999999",
  "ativo": true
}
```

---

## Profissionais

### Criar Profissional

`POST /api/profissionais` → `201 Created`

**Request**
```json
{
  "nome": "Carlos Oliveira",
  "email": "carlos@email.com",
  "especialidadeIds": [1]
}
```

**Response**
```json
{
  "id": 1,
  "nome": "Carlos Oliveira",
  "email": "carlos@email.com",
  "ativo": true,
  "especialidades": [
    {
      "id": 1,
      "nome": "Corte Masculino",
      "descricao": "Serviços de corte de cabelo masculino",
      "ativo": true
    }
  ]
}
```

### Consultar Disponibilidade

`GET /api/profissionais/1/disponibilidade?data=2026-06-25` → `200 OK`

Retorna os horários **já ocupados** do profissional na data informada:

**Response**
```json
[
  {
    "agendamentoId": 1,
    "dataHoraInicio": "2026-06-25T14:00:00",
    "dataHoraFim": "2026-06-25T14:30:00",
    "status": "AGENDADO"
  }
]
```

---

## Serviços

### Criar Serviço

`POST /api/servicos` → `201 Created`

**Request**
```json
{
  "nome": "Corte Tradicional",
  "descricao": "Corte masculino tradicional",
  "duracaoMinutos": 30,
  "preco": 35.00,
  "especialidadeId": 1
}
```

**Response**
```json
{
  "id": 1,
  "nome": "Corte Tradicional",
  "descricao": "Corte masculino tradicional",
  "duracaoMinutos": 30,
  "preco": 35.00,
  "ativo": true,
  "especialidade": {
    "id": 1,
    "nome": "Corte Masculino",
    "descricao": "Serviços de corte de cabelo masculino",
    "ativo": true
  }
}
```

---

## Agendamentos

### Criar Agendamento

`POST /api/agendamentos` → `201 Created`

**Request**
```json
{
  "clienteId": 1,
  "profissionalId": 1,
  "servicoId": 1,
  "dataHoraInicio": "2026-06-25T14:00:00",
  "observacao": "Primeiro atendimento"
}
```

**Response**
```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "41999999999",
    "ativo": true
  },
  "profissional": {
    "id": 1,
    "nome": "Carlos Oliveira",
    "email": "carlos@email.com",
    "ativo": true,
    "especialidades": [
      {
        "id": 1,
        "nome": "Corte Masculino",
        "descricao": "Serviços de corte de cabelo masculino",
        "ativo": true
      }
    ]
  },
  "servico": {
    "id": 1,
    "nome": "Corte Tradicional",
    "descricao": "Corte masculino tradicional",
    "duracaoMinutos": 30,
    "preco": 35.00,
    "ativo": true,
    "especialidade": {
      "id": 1,
      "nome": "Corte Masculino",
      "descricao": "Serviços de corte de cabelo masculino",
      "ativo": true
    }
  },
  "dataHoraInicio": "2026-06-25T14:00:00",
  "dataHoraFim": "2026-06-25T14:30:00",
  "status": "AGENDADO",
  "observacao": "Primeiro atendimento"
}
```

### Cancelar Agendamento

`PATCH /api/agendamentos/1/cancelar` → `200 OK`

Sem corpo de requisição. Retorna o agendamento atualizado, no mesmo formato do exemplo acima, com `"status": "CANCELADO"`.

Caso o cancelamento seja solicitado com menos de 2 horas de antecedência, ou o agendamento já esteja `CONCLUIDO`, a API retorna `422 Unprocessable Entity`:

```json
{
  "status": 422,
  "mensagem": "Cancelamento permitido apenas com antecedência mínima de 2 horas",
  "dataHora": "2026-06-25T13:00:00"
}
```

### Concluir Agendamento

`PATCH /api/agendamentos/1/concluir` → `200 OK`

Sem corpo de requisição. Retorna o agendamento atualizado, no mesmo formato do exemplo de criação, com `"status": "CONCLUIDO"`.

---

# Testes Unitários

O projeto possui testes unitários implementados utilizando JUnit 5 e Mockito, cobrindo todas as classes de service.

Classes cobertas:
* `AgendamentoServiceTest` — inclui criação, conflito de horário, agendamento no passado, profissional sem especialidade do serviço, cancelamento fora do prazo, cancelamento de agendamento concluído e conclusão de agendamento cancelado
* `ClienteServiceTest`
* `EspecialidadeServiceTest`
* `ProfissionalServiceTest` — inclui desativação com e sem agendamentos futuros, consulta de disponibilidade
* `ServicoServiceTest`

Execução:

```bash
  ./mvnw test
```
Resultado obtido:

```text
Tests run: 36
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```
---

# Estrutura do Projeto

```text
src/main/java/com/jhonatan/agendamento/
 ├── controller/
 ├── dto/
 │   ├── request/
 │   └── response/
 ├── exception/
 ├── model/
 ├── repository/
 ├── service/
 ├── config/
 └── AgendamentoServicosApiApplication.java
```

---

# Histórico de Desenvolvimento

```text
chore: configura projeto spring boot com postgresql e docker
feat: implementa estrutura base e tratamento de excecoes
feat: implementa gerenciamento de especialidades
feat: implementa gerenciamento de clientes
feat: implementa gerenciamento de profissionais
feat: implementa gerenciamento de servicos
feat: implementa sistema de agendamentos e regras de negocio
test: adiciona testes unitarios dos services
test: amplia cobertura de testes unitarios
docs: aprimora documentacao swagger dos endpoints
docs: adiciona exemplos de uso da API
refactor: usa dto na consulta de disponibilidade
refactor: padroniza codigos HTTP REST
docs: reescreve README com exemplos completos e validados
```

---

# Autor

Jhonatan Nowicki

GitHub: https://github.com/Jhonatan-Nowicki