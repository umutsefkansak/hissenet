# -*- coding: utf-8 -*-
import logging
import pymongo
from dotenv import dotenv_values

secrets = dotenv_values(".SECRETS")

dbLogger = logging.getLogger('database')

def readDBUri() -> str:
    try:
        uri = secrets["mongoDBURI"]
        if not uri:
            dbLogger.warning("Uyarı: 'mongoDBURI' bulunamadı veya boş!")
            return None
        return uri
    except Exception as e:
        dbLogger.exception("HATA: uri bilgisi okunamadı!")

def connectMongo(uri:str):
    try:
        client = pymongo.MongoClient(uri)
        documents = client.RagDocuments0
        dbLogger.debug("MongoDb bağlantısı kuruldu.")
        return documents
    except Exception as e:
        dbLogger.exception("HATA: MongoDb bağlantısı kurulamadı!")

def getCollection(db):
    try:
        steps = db.steps
        dbLogger.debug("MongoDb koleksiyonu okundu.")
        return steps
    except Exception as e:
        dbLogger.exception("HATA: MongoDb koleksiyonu okunamadı!")

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
                "$gte": 0.3}
                }}]);

def getResults(results):
    res = list()
    for r in results:
        res.append(r)

    return res