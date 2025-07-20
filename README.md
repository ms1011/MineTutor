# MineTutor

MineTutor is a gameplay guide plugin for Minecraft multiplayer servers. It allows players to get real-time answers to any gameplay-related questions—such as crafting recipes, mob strategies, or resource locations—simply by asking in the chat. The plugin leverages LLM technology to provide a natural and accurate guide experience.

## Features

- **Multi-AI Support:** Choose your preferred AI provider. Supports OpenAI, Anthropic Claude, and Google Gemini.
- **AI-Powered Q&A:** Get answers to your Minecraft questions directly in-game.
- **Asynchronous API Handling:** Prevents server lag by handling API requests off the main thread.
- **Command Cooldown:** Prevents API spam and controls costs with a configurable per-player cooldown.
- **Fully Configurable:** Customize everything from the AI provider, API key, and AI personality to cooldown times and user-facing messages.
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

# AI Provider
# Specifies which AI provider to use. Supported: "openai", "claude", "gemini"
ai-provider: openai

# API Key
# Get your API key from the respective provider's website.
api-key: "YOUR_API_KEY"

# Model selection for each provider
openai-model: "gpt-3.5-turbo"
claude-model: "claude-3-haiku-20240307"
gemini-model: "gemini-1.5-flash"

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

-   **`ai-provider`:** Determines which AI service to use. You can choose between `openai`, `claude`, or `gemini`.
-   **`api-key` (Required):** You **must** replace `"YOUR_API_KEY"` with your actual API key from the chosen provider.
-   **`openai-model`, `claude-model`, `gemini-model`:** Specifies the model to use for each respective provider.
-   **`system-prompt`:** This text defines the personality and instructions for the AI.
-   **`command-cooldown`:** The time in seconds a player must wait before using the command again.
-   **`messages`:** Customize all messages shown to players.

### 3. Commands and Permissions

#### Player Commands

-   `/guide <question>`: Asks a question to the AI.
-   Aliases: `/ask`, `/가이드`

#### Admin Commands

-   `/guide reload`: Reloads the `config.yml` file.
    -   **Permission:** `minetutor.admin`

---

## 한글 안내

### 1. 설치 방법

1.  [GitHub Releases](https://github.com/ms1011/MineTutor/releases) 페이지에서 최신 `MineTutor-*.jar` 파일을 다운로드합니다.
2.  다운로드한 `.jar` 파일을 서버의 `plugins` 디렉토리에 넣어주세요.
3.  서버를 시작하거나 재시작합니다.

### 2. 설정 방법

`plugins/MineTutor/` 경로에 생성된 `config.yml` 파일을 수정하세요.

```yaml
# MineTutor 설정

# AI 공급자
# 사용할 AI 공급자를 지정합니다. 지원: "openai", "claude", "gemini"
ai-provider: openai

# API 키
# 선택한 AI 공급자의 웹사이트에서 API 키를 발급받으세요.
api-key: "YOUR_API_KEY"

# 각 공급자별 모델 선택
openai-model: "gpt-3.5-turbo"
claude-model: "claude-3-haiku-20240307"
gemini-model: "gemini-1.5-flash"

# 시스템 프롬프트는 AI의 행동을 설정합니다.
system-prompt: "You are MineTutor, a helpful Minecraft expert. Answer the user's questions about Minecraft concisely and accurately. Your response should be in Korean."

# 명령어 재사용 대기시간 (초)
command-cooldown: 30

# 메시지
messages:
  cooldown: "§c명령어를 다시 사용하려면 {time}초 더 기다려야 합니다."
  thinking: "§eMineTutor가 생각 중입니다..."
  error: "§c오류: MineTutor로부터 응답을 받지 못했습니다."
  no-permission: "§c이 명령어를 사용할 권한이 없습니다."
  usage: "§c사용법: /guide <질문>"
```

-   **`ai-provider`:** `openai`, `claude`, `gemini` 중 사용할 AI 서비스를 선택합니다.
-   **`api-key` (필수):** 선택한 공급자의 실제 API 키를 입력해야 합니다.
-   **`openai-model`, `claude-model`, `gemini-model`:** 각 AI 공급자별로 사용할 모델을 지정합니다.
-   **`system-prompt`:** AI의 성격과 지침을 정의합니다.
-   **`command-cooldown`:** 명령어 재사용 대기시간입니다.
-   **`messages`:** 플레이어에게 보여지는 메시지를 수정합니다.

### 3. 명령어 및 권한

-   `/guide <질문>`: AI에게 질문합니다. (별칭: `/ask`, `/가이드`)
-   `/guide reload`: 설정을 다시 불러옵니다. (`minetutor.admin` 권한 필요)