# -*- coding: utf-8 -*-
import asyncio
from LLM.llm import searchVector
from Database.database import readDBUri, connectMongo, getCollection

def connectDatabase():
    uri: str   = readDBUri()
    db         = connectMongo(uri=uri)
    collection = getCollection(db=db)

    return collection

def getLlmResponse(message):
    collection = connectDatabase()
    return asyncio.run(searchVector(query=message, collection=collection))