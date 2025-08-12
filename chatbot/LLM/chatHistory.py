import os
import json
from langchain.schema import HumanMessage, AIMessage
historyPATH = os.path.join(os.path.dirname(__file__), "chatHistory.json")

history = []

def loadChatHistoryFromJson():
    global history
    if not os.path.exists(historyPATH):
        history = []
        return

    try:
        with open(historyPATH, "r", encoding="utf-8") as f:
            data = json.load(f)
            history = []
            for item in data:
                if item["type"] == "human":
                    history.append(HumanMessage(content=item["content"]))
                elif item["type"] == "ai":
                    history.append(AIMessage(content=item["content"]))
    except Exception as e:
        print(f"[ERROR] Chat geçmişi yüklenirken hata oluştu: {e}")
        history = []

def updateChatHistory(query: str, answer: str):
    global history
    maxHistory: int = 5
    history.append(HumanMessage(content=query))
    history.append(AIMessage(content=answer))
    
    if len(history) > maxHistory * 2:
        history = history[-maxHistory * 2:]

    saveChatHistoryToJson()

def saveChatHistoryToJson():
    with open(historyPATH, "w", encoding="utf-8") as f:
        json.dump(
            [{"type": "human" if isinstance(msg, HumanMessage) else "ai", "content": msg.content} for msg in history],
            f,
            ensure_ascii=False,
            indent=2
        )