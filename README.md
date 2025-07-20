# MineTutor

MineTutor is a gameplay guide plugin for Minecraft multiplayer servers. It allows players to get real-time answers to any gameplay-related questions—such as crafting recipes, mob strategies, or resource locations—simply by asking in the chat. The plugin leverages OpenAI's LLM technology to provide a natural and accurate guide experience.

## Features

- **AI-Powered Q&A:** Get answers to your Minecraft questions directly in-game.
- **Asynchronous API Handling:** Prevents server lag by handling API requests off the main thread.
- **Command Cooldown:** Prevents API spam and controls costs with a configurable per-player cooldown.
- **Fully Configurable:** Customize everything from the API key and AI personality to cooldown times and user-facing messages.
- **Command Aliases:** Use `/guide`, `/ask`, or `/가이드`.
- **Reload Command:** Admins can reload the configuration without a full server restart.

---

## English Guide

### 1. Installation

1.  Download the latest `MineTutor-*.jar` file from the [GitHub Releases](https://github.com/ms1011/MineTutor/releases) page.
2.  Place the downloaded `.jar` file into your server's `plugins` directory.
3.  Start or restart your server. The plugin will load and generate a default configuration file.

### 2. Configuration

After the first run, a `config.yml` file will be created in `plugins/MineTutor/`. You must edit this file to get the plugin working.

```yaml
# MineTutor Configuration

# OpenAI API Key
# Get your API key from https://platform.openai.com/account/api-keys
api-key: "YOUR_OPENAI_API_KEY"

# The system prompt helps set the behavior of the assistant.
# You can modify this to change how MineTutor responds.
system-prompt: "You are MineTutor, a helpful Minecraft expert. Answer the user's questions about Minecraft concisely and accurately."

# Cooldown in seconds between uses of the /guide command for each player.
command-cooldown: 30

# Messages
messages:
  cooldown: "§cYou must wait {time} more seconds before using this command again."
  thinking: "§eMineTutor is thinking..."
  error: "§cError: Failed to get response from MineTutor."
  no-permission: "§cYou do not have permission to use this command."
  usage: "§cUsage: /guide <question>"
```

-   **`api-key` (Required):** This is the most important setting. You **must** replace `"YOUR_OPENAI_API_KEY"` with your actual OpenAI API key for the plugin to function.
-   **`system-prompt`:** This text defines the personality and instructions for the AI. You can customize it to change how it behaves.
-   **`command-cooldown`:** The time in seconds a player must wait before using the command again.
-   **`messages`:** Customize all messages shown to players. The `{time}` placeholder can be used in the cooldown message.

### 3. Commands and Permissions

#### Player Commands

-   `/guide <question>`: Asks a question to the AI.
-   Aliases: `/ask`, `/가이드`

#### Admin Commands

-   `/guide reload`: Reloads the `config.yml` file.
    -   **Permission:** `minetutor.admin`
    -   This permission is granted to server operators (OPs) by default.

---

## 한글 안내

### 1. 설치 방법

1.  [GitHub Releases](https://github.com/ms1011/MineTutor/releases) 페이지에서 최신 `MineTutor-*.jar` 파일을 다운로드합니다.
2.  다운로드한 `.jar` 파일을 서버의 `plugins` 디렉토리에 넣어주세요.
3.  서버를 시작하거나 재시작합니다. 플러그인이 로드되면서 기본 설정 파일이 생성됩니다.

### 2. 설정 방법

플러그인을 처음 실행하면 `plugins/MineTutor/` 경로에 `config.yml` 파일이 생성됩니다. 플러그인이 정상적으로 작동하려면 반드시 이 파일을 수정해야 합니다.

```yaml
# MineTutor 설정

# OpenAI API 키
# API 키는 https://platform.openai.com/account/api-keys ��서 발급받을 수 있습니다.
api-key: "YOUR_OPENAI_API_KEY"

# 시스템 프롬프트는 AI의 행동을 설정하는 데 도움을 줍니다.
# MineTutor가 응답하는 방식을 변경하려면 이 부분을 수정하세요.
system-prompt: "You are MineTutor, a helpful Minecraft expert. Answer the user's questions about Minecraft concisely and accurately. Your response should be in Korean."

# 각 플레이어가 /guide 명령어를 다시 사용하기까지 기다려야 하는 시간 (초 단위)
command-cooldown: 30

# 메시지
messages:
  cooldown: "§c명령어를 다시 사용하려면 {time}초 더 기다려야 합니다."
  thinking: "§eMineTutor가 생각 중입니다..."
  error: "§c오류: MineTutor로부터 응답을 받지 못했습니다."
  no-permission: "§c이 명령어를 사용할 권한이 없습니다."
  usage: "§c사용법: /guide <질문>"
```

-   **`api-key` (필수):** 가장 중요한 설정입니다. 플러그인이 작동하려면 **반드시** `"YOUR_OPENAI_API_KEY"` 부분을 실제 OpenAI API 키로 교체해야 합니다.
-   **`system-prompt`:** AI의 성격과 지침을 정의하는 텍스트입니다. AI의 행동 방식을 바꾸고 싶다면 이 부분을 수정할 수 있습니다.
-   **`command-cooldown`:** 플레이어가 명령어를 재사용하기까��� 기다려야 하는 시간(초)입니다.
-   **`messages`:** 플레이어에게 보여지는 모든 메시지를 수정할 수 있습니다. 쿨다운 메시지에서는 `{time}` 변수를 사용할 수 있습니다.

### 3. 명령어 및 권한

#### 플레이어 명령어

-   `/guide <질문>`: AI에게 질문을 합니다.
-   별칭: `/ask`, `/가이드`

#### 관리자 명령어

-   `/guide reload`: `config.yml` 설정 파일을 다시 불러옵니다.
    -   **권한:** `minetutor.admin`
    -   이 권한은 기본적으로 서버 관리자(OP)에게 부여됩니다.