# -*- coding: utf-8 -*-
import uuid
import json
from dotenv import dotenv_values

secrets = dotenv_values(".SECRETS")

PATH: str = secrets["pathPREPROCESS"]

def readTxt() -> str:
    qaData: list = []

    file      = open(f"{PATH}steps.txt", encoding="utf-8")
    text: str = file.read()

    textSplitted: list = text.split("\n")

    for i in range(len(textSplitted)):
        id       = str(uuid.uuid4())
        question = textSplitted[i].split(":")[0]
        answer   = textSplitted[i].split(":")[-1] 
        
        qaData.append({"id": id, "question": question, "answer": answer})
        
    file.close
    print(qaData)
    return qaData
    
def createJsonFile(qaData: dict) -> None:
    with open(f"{PATH}steps.json", "w", encoding="utf-8") as file:
        json.dump(qaData, file, indent=4, ensure_ascii=False)

def main():
    qaData: str = readTxt()
    createJsonFile(qaData=qaData)

if __name__ == "__main__":
    main()
