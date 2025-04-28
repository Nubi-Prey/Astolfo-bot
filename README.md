# AstolfoBot (Beta 0.1)

Um bot simples e modular para Discord construído com JDA 5.

**Versão Atual:** Beta 0.1 (28 de Abril de 2025)

## ✨ Funcionalidades (Beta 0.1)

* Conexão com a API do Discord usando JDA.
* Carregamento dinâmico de Slash Commands a partir do pacote `commands`.
* Registro automático de Slash Commands em um servidor de teste específico durante a inicialização (`onReady`).
* Manipulador básico para executar a lógica dos comandos Slash (`SlashCommandListener`).
* Comando de exemplo: `/ping` para verificar a latência do bot.
* Configuração de Log usando Logback (`logback.xml`).

## ⚙️ Requisitos

* **JDK (Java Development Kit):** Versão 17 ou superior é recomendada (JDA 5 requer Java 11+, mas funcionalidades mais recentes podem usar versões superiores).
* **Maven:** Para gerenciar dependências e compilar o projeto.
* **Conta Discord e Bot Application:** Você precisará criar uma aplicação de bot no [Portal de Desenvolvedores do Discord](https://discord.com/developers/applications).
* **Token do Bot:** O token de autenticação do seu bot.

## 🔧 Configuração

Antes de rodar o bot, você **precisa** configurar a seguinte variável de ambiente:

1.  **`DISCORD_BOT_TOKEN`**: Defina esta variável com o token secreto do seu bot obtido no Portal de Desenvolvedores do Discord.
    * *Exemplo Linux/macOS:* `export DISCORD_BOT_TOKEN="SEU_TOKEN_AQUI"`
    * *Exemplo Windows (cmd):* `set DISCORD_BOT_TOKEN=SEU_TOKEN_AQUI`
    * *Exemplo Windows (PowerShell):* `$env:DISCORD_BOT_TOKEN="SEU_TOKEN_AQUI"`
    * Consulte a documentação do seu sistema operacional para defini-la permanentemente ou configure-a na sua IDE. **NUNCA coloque o token diretamente no código!**

## 🛠️ Compilando (Build)

Este projeto usa Maven. Para compilar o projeto e gerar o arquivo JAR executável:

1.  Abra um terminal ou prompt de comando na pasta raiz do projeto (onde está o `pom.xml`).
2.  Execute o comando Maven:
    ```bash
    mvn clean package
    ```
3.  Isso irá baixar as dependências, compilar o código e criar um arquivo JAR (ex: `AstolfoBot-0.1.jar`) no diretório `target/`.

## ▶️ Executando o Bot

Após compilar o projeto e configurar as variáveis de ambiente:

1.  Navegue até o diretório `target/` que foi criado pelo Maven.
2.  Execute o JAR usando o Java:
    ```bash
    java -jar AstolfoBot-0.1.jar
    ```
    *(Substitua `AstolfoBot-0.1.jar` pelo nome exato do JAR gerado no seu diretório `target/`)*

3.  O bot tentará se conectar ao Discord. Verifique o console para mensagens de log, incluindo a mensagem de "pronto" e o status do registro de comandos.

## 📂 Estrutura do Projeto (Simplificada)

* `src/main/java`: Contém o código-fonte Java.
    * `AstolfoBot.java`: Classe principal que inicializa o JDA e registra os listeners.
    * `commands/`: Pacote contendo as implementações dos comandos Slash.
        * `AbstractCommand.java`: Classe base para os comandos.
        * `ping.java`: Exemplo de comando.
    * `listeners/`: Pacote contendo classes que ouvem eventos do JDA.
        * `ReadyListener.java`: Ouve o evento Ready e coordena a inicialização.
        * `SlashCommandListener.java`: Ouve e processa interações de Slash Command.
    * `handlers/`: Pacote contendo lógica de gerenciamento.
        * `CommandHandler.java`: Responsável por carregar e registrar os comandos.
* `src/main/resources`: Contém arquivos de recursos.
    * `logback.xml`: Configuração de log.
* `pom.xml`: Arquivo de configuração do build tool.

## 🚀 Comandos Implementados (Beta 0.1)

* `/ping`: Responde "Pong!" e mostra a latência da gateway do bot.
---
