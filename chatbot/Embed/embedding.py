import json
from dotenv import dotenv_values
from sentence_transformers import SentenceTransformer

secrets = dotenv_values(".SECRETS")

PATH: str = secrets["pathPREPROCESS"]

def getJson() -> json:
    with open(f'{PATH}steps.json', 'r', encoding="utf-8") as file:
        return json.load(file)

def exportJson(data: json) -> None:
    with open(f"{PATH}embedded_steps.json", "w", encoding="utf-8") as file:
        json.dump(data, file, indent=4, ensure_ascii=False)

def getEmbeddingModel():
    # model = AutoModel.from_pretrained(f'{PATH}all-MiniLM-L6-v2')
    # model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')
    # model = SentenceTransformer(f'{PATH}all-MiniLM-L6-v2')
    model = SentenceTransformer(f'{PATH}mxbai-embed-large-v1-nli-matryoshka')
    return model

def makeEmbedding(text: str, model) -> list:
    return model.encode(text) 

def embedQandA(data: json, model) -> json:
    for i in data:
        question: str = i['question'] 
        answer: str   = i['answer'] 

        qEmbedding = makeEmbedding(question, model)
        aEmbedding = makeEmbedding(answer, model)
        
        i.update({"qEmbedding":qEmbedding.tolist()})
        i.update({"aEmbedding":aEmbedding.tolist()})

    return data

data: json = getJson()
model      = getEmbeddingModel()
embedQandA(data=data, model=model)
exportJson(data)