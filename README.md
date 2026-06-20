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

# Funcionalidades

## Especialidades

* Criar especialidade
* Listar especialidades ativas
* Buscar especialidade por ID
* Atualizar especialidade
* Desativar especialidade

## Clientes

* Criar cliente
* Listar clientes ativos
* Buscar cliente por ID
* Atualizar cliente
* Desativar cliente

## Profissionais

* Criar profissional
* Listar profissionais ativos
* Buscar profissional por ID
* Atualizar profissional
* Desativar profissional
* Consultar disponibilidade por data

## Serviços

* Criar serviço
* Listar serviços ativos
* Buscar serviço por ID
* Atualizar serviço
* Desativar serviço
* Listar serviços por especialidade

## Agendamentos

* Criar agendamento
* Listar agendamentos
* Buscar agendamento por ID
* Listar por cliente
* Listar por profissional
* Cancelar agendamento
* Concluir agendamento

---

# Regras de Negócio

## Clientes

* Não permite cadastro de clientes com e-mail duplicado.

## Profissionais

* Não permite cadastro de profissionais com e-mail duplicado.
* Todas as especialidades devem existir e estar ativas.

## Especialidades

* Não permite nomes duplicados.

## Serviços

* Devem estar vinculados a uma especialidade válida e ativa.

## Agendamentos

* Cliente deve existir e estar ativo.
* Profissional deve existir e estar ativo.
* Serviço deve existir e estar ativo.
* O profissional deve possuir a especialidade necessária para executar o serviço.
* Não é permitido criar agendamentos em datas passadas.
* Não é permitido conflito de horários para o mesmo profissional.

### Exemplo de conflito de horário

```text
Agendamento existente:
09:00 às 09:30

Tentativa de novo agendamento:
09:15 às 09:45

Resultado:
ConflitoException
```

## Cancelamentos

* Não é permitido cancelar agendamentos concluídos.
* O cancelamento exige antecedência mínima de 2 horas.

## Conclusão

* Não é permitido concluir agendamentos cancelados.

## Desativação de Profissionais

* Não é permitido desativar profissionais que possuam agendamentos futuros.

---

# Tratamento de Exceções

A aplicação utiliza tratamento centralizado de exceções através de:

```text
GlobalExceptionHandler
```

Exceções tratadas:

* RecursoNaoEncontradoException
* ConflitoException
* RegraDeNegocioException
* Erros de validação

Retornando respostas padronizadas para o cliente.

---

# Banco de Dados

Banco utilizado:

```text
PostgreSQL
```

Configuração padrão:

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

## Executar a aplicação

```bash
  ./mvnw spring-boot:run
```

Ou executar diretamente pela IDE.

---

# Documentação da API

Swagger disponível em:

```text
http://localhost:8080/swagger-ui/index.html
```

A documentação contém todos os endpoints da aplicação, parâmetros, DTOs, respostas e códigos HTTP possíveis.

---

# Testes Unitários

O projeto possui testes unitários implementados utilizando:

* JUnit 5
* Mockito

Classes cobertas:

* AgendamentoServiceTest
* ClienteServiceTest
* EspecialidadeServiceTest
* ProfissionalServiceTest
* ServicoServiceTest

Execução:

```bash
  ./mvnw test
```

Resultado atual:

```text
Tests run: 36
Failures: 0
Errors: 0
BUILD SUCCESS
```

---

# Estrutura do Projeto

```text
src
 ├── controller
 ├── dto
 │   ├── request
 │   └── response
 ├── exception
 ├── model
 ├── repository
 ├── service
 └── config
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
docs: aprimora documentacao swagger dos endpoints
refactor: usa dto na consulta de disponibilidade
```

---

# Autor

Jhonatan Nowicki

GitHub:
https://github.com/Jhonatan-Nowicki
