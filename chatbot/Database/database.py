import json
import pymongo
from dotenv import dotenv_values

secrets = dotenv_values(".SECRETS")

def readDBUri() -> str:
    return secrets["mongoDBURI"]

def connectDB(uri:str):
    client = pymongo.MongoClient(uri, connect=False)
    return client.RagDocuments0

def getCollection(db):
    return db.steps

def findMatch(collection, embeddedQuery: list):
    return collection.aggregate([{"$vectorSearch":{
        "queryVector"  : embeddedQuery,
        "path"         : "qEmbedding",
        "index"        : "embeddingSemSearch",
        "numCandidates": 100,
        "limit"        : 3
    }},{"$project": {
            "content" : 1,
            "score"   : {"$meta": "vectorSearchScore"},
            "question": 1,
            "answer"  : 1
        }
    },{"$match": {
            "score": {
                "$gte": 0.1}
                }}]);

def getResults(results):
    res = list()
    for r in results:
        res.append(r)
 
    return res

# ----------------------------------
"""
uri: str   = readDBUri()
db         = connectDB(uri=uri) 
collection = getCollection(db=db)

query: str  = "portföy oluşmak" 
pathDB: str = secrets["pathDATABASE"]

with open(f"{pathDB}embeddedQuery.json", "r") as file:
    embeddedQuery: list = json.load(file)
        
results = findMatch(collection, embeddedQuery)
printResults(results)
"""