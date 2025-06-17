# AstolfoBot

Um bot modular para Discord construído com JDA 5, com banco de dados local gerenciado por Docker e PostgreSQL.

**Versão Atual:** Beta 0.2 (17 de Junho de 2025)

## ✨ Funcionalidades

*   Conexão com a API do Discord usando JDA 5.
*   Infraestrutura de banco de dados (PostgreSQL) gerenciada via Docker Compose.
*   Carregamento dinâmico de Slash Commands.
*   Configuração flexível para dados não-sensíveis (`config.properties`).
*   Logging configurado com Logback.

## ⚙️ Requisitos

*   **JDK (Java Development Kit):** Versão 17 ou superior.
*   **Maven:** Para gerenciamento de dependências e build do projeto.
*   **Docker:** Para executar e gerenciar a infraestrutura do banco de dados.
*   **Conta Discord e Bot Application:** Necessária para obter o token do bot.

## 🔧 Configuração Inicial

A configuração do projeto é dividida em duas partes: a **infraestrutura** (o banco de dados) e a **aplicação** (o bot).

### 1. Configuração da Infraestrutura (PostgreSQL)

Usamos um arquivo `.env` para facilitar a configuração do Docker Compose sem expor senhas.

1.  Navegue até o diretório de infraestrutura (infra).
2.  Você encontrará um arquivo chamado `.env.example`. **Copie este arquivo** e renomeie a cópia para `.env`.
3.  Abra o novo arquivo `infra/.env` e **defina uma senha** para `POSTGRES_PASSWORD`.

### 2. Configuração da Aplicação (Bot)

O bot em si precisa de suas próprias variáveis de ambiente para dados sensíveis. Elas devem ser configuradas no seu sistema ou na sua IDE, fora do projeto.

**Variáveis de Ambiente Obrigatórias para o Bot:**

*   `DISCORD_BOT_TOKEN`: O token de autenticação do seu bot.
*   `DATABASE_URL`: A URL de conexão JDBC completa. Use os mesmos dados que você definiu no arquivo `infra/.env`.

**Exemplo de como definir as variáveis:**

*   **Linux/macOS:**
    ```sh
    export DISCORD_BOT_TOKEN="SEU_TOKEN_AQUI"
    export DATABASE_URL="jdbc:postgresql://localhost:5432/<postgres_db>?user=<postgres_user>&password=<postgres_password>"
    ```
*   **Windows (PowerShell):**
    ```shell
    $env:DISCORD_BOT_TOKEN="SEU_TOKEN_AQUI"
    $env:DATABASE_URL="jdbc:postgresql://localhost:5432/<postgres_db>?user=<postgres_user>&password=<postgres_password>"
    ```

**Importante:** Nunca coloque o token do bot ou a `DATABASE_URL` diretamente no código ou em arquivos versionados pelo Git!

## 🛠️ Executando o Projeto

1.  **Iniciar o Banco de Dados:**
    *   Abra um terminal e navegue até a pasta `infra/`.
    *   Execute o comando abaixo. O Docker Compose irá ler seu arquivo `infra/.env` e iniciar o container do PostgreSQL em segundo plano.
        ```bash
        docker compose up -d
        ```

2.  **Compilar (Build) a Aplicação:**
    *   Volte para o diretório raiz do projeto.
    *   Compile e empacote a aplicação:
        ```bash
        mvn clean package
        ```

3.  **Executar o Bot:**
    *   Execute o JAR gerado. A aplicação Java lerá as variáveis de ambiente (`DISCORD_BOT_TOKEN`, `DATABASE_URL`) para se conectar aos serviços.
        ```bash
        java -jar target/AstolfoBot-0.1.jar
        ```
        *(Lembre-se de substituir pelo nome exato do seu arquivo JAR)*

## 📂 Estrutura do Projeto
* `infra`
  * `docker-entrypoint-initdb.d`
  * `compose.yaml`
  * `.env`
* `src/main/java`
    * `DiscordBot`: Classe principal.
    * `commands/`:  Comandos Slash.
    * `listeners/`:  Classes que ouvem eventos do JDA.
        * `ReadyListener`: Ouve o evento Ready e coordena a inicialização.
        * `SlashCommandListener`: Ouve e processa interações de Slash Command.
    * `handlers/`: Pacote contendo lógica de gerenciamento.
        * `CommandHandler`: Responsável por carregar e registrar os comandos.
* `src/main/resources`: Contém arquivos de recursos.
* `pom.xml`: Arquivo de configuração do build tool.  
