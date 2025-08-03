import os
import sys
import asyncio
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from dotenv import dotenv_values
import google.generativeai as genai
from langchain.chains import RetrievalQA
from langchain_core.messages import HumanMessage
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_mongodb import MongoDBAtlasVectorSearch
# from langchain.document_loaders import DirectoryLoader

from Database.database import readDBUri, connectDB, getCollection, findMatch, getResults

secrets       = dotenv_values(".SECRETS")
googleAPIKEY  = secrets["googleAPIKEY"]

async def makeEmbedding(query: str) -> list:
    embedding = GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)
    return embedding.embed_query(query)

def getVectorStore(collection):
    embeddings  = GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)
    return MongoDBAtlasVectorSearch(collection=collection, embedding=embeddings, index_name="embeddingSemSearch", text_key="answer", embedding_key="qEmbedding") 

async def searchVector(query, collection):
    vectorStore = getVectorStore(collection=collection)
    steps       = vectorStore.similarity_search(query, K=3)
    asOutput    = steps[0].page_content

    llm       = ChatGoogleGenerativeAI(model="gemini-2.5-flash", temperature=0.5, google_api_key=googleAPIKEY)
    retriever = vectorStore.as_retriever()
    qa        = RetrievalQA.from_chain_type(llm, chain_type="stuff", retriever=retriever)
    retrieverOutput = qa.run(query)

    return asOutput, retrieverOutput

"""
from langchain.prompts import PromptTemplate
from langchain.chains import RetrievalQA

# Özelleştirilmiş prompt
custom_prompt = PromptTemplate.from_template(
    ""
    Aşağıdaki bağlam (context) ile kullanıcı sorusunu dikkatlice cevapla.
    Eğer cevap bağlamda açıkça belirtilmemişse, "Bu konuda elimde bilgi yok." de.

    Context:
    {context}

    Soru:
    {question}

    Cevap:
    ""
)

# QA zinciri özel prompt ile
qa = RetrievalQA.from_chain(
    llm=llm,
    retriever=retriever,
    chain_type="stuff",
    chain_type_kwargs={"prompt": custom_prompt}
)

"""

"""
query = "portföy oluşmak"

uri: str      = readDBUri()
db            = connectDB(uri=uri)
collection    = getCollection(db=db)
results       = asyncio.run(searchVector(query=query, collection=collection))
print(results)
"""