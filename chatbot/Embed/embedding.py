# -*- coding: utf-8 -*-
import json
import time
from dotenv import dotenv_values
from langchain_google_genai import GoogleGenerativeAIEmbeddings

secrets = dotenv_values(".SECRETS")

PATH: str = secrets["pathPREPROCESS"]
googleAPIKEY = secrets["googleAPIKEY"]

def getJson() -> json:
    with open(f'{PATH}steps.json', 'r', encoding="utf-8") as file:
        return json.load(file)

def exportJson(data: json) -> None:
    with open(f"{PATH}embedded_steps.json", "w", encoding="utf-8") as file:
        json.dump(data, file, indent=4, ensure_ascii=False)

def getEmbeddingModel():
    return GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)

def makeEmbedding(text: str, model) -> list:
    return model.embed_query(text)

def embedQandA(data: json, model) -> json:
    for i in data:
        question: str = i['question'] 
        qEmbedding = makeEmbedding(question, model)
        i.update({"qEmbedding":qEmbedding})
        time.sleep(3)

    return data

def main():
    data = getJson()
    model = getEmbeddingModel()
    embedded_data = embedQandA(data, model)
    exportJson(embedded_data)

if __name__ == "__main__":
    main()