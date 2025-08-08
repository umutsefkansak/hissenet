# -*- coding: utf-8 -*-
import asyncio
from LLM.llm import searchVector
from Database.database import readDBUri, connectMongo, getCollection

def connectDatabase():
    uri: str   = readDBUri()
    db         = connectMongo(uri=uri)
    return getCollection(db=db)
    
def getLlmResponse(message):
    collection = connectDatabase()
    return asyncio.run(searchVector(query=message, collection=collection))