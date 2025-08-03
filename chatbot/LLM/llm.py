import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import asyncio
from dotenv import dotenv_values
import google.generativeai as genai
from langchain.chains import retrieval_qa 
from langchain_google_genai import GoogleGenerativeAIEmbeddings
# from langchain.document_loaders import DirectoryLoader

# from Database.database import readDBUri, connectDB, getCollection, findMatch, printResults

secrets       = dotenv_values(".SECRETS")
googleAPIKEY  = secrets["googleAPIKEY"]

async def makeEmbedding(query: str) -> list:
    embedding = GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)
    return embedding.embed_query(query)

"""
uri: str      = readDBUri()
db            = connectDB(uri=uri)
collection    = getCollection(db=db)
embeddedQuery = makeEmbedding(query=query)
results       = findMatch(collection=collection, embeddedQuery=embeddedQuery)
printResults(results=results)
"""