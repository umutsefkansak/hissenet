# -*- coding: utf-8 -*-
import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

import logging
from dotenv import dotenv_values
from langchain.chains import RetrievalQA
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_mongodb import MongoDBAtlasVectorSearch
from langchain.prompts import PromptTemplate

secrets       = dotenv_values(".SECRETS")
googleAPIKEY  = secrets["googleAPIKEY"]

llmLogger = logging.getLogger('llm')

# admin sayfasında admin için cevap ver.
# admine personel raporu sunulabilir.

system_prompt = PromptTemplate.from_template("""
    Sen bir B2B Hisse Alım Satım Platformu olan HisseNet'in destek asistanısın. Personel sana platformun kullanımı hakkında sorular sorar.

    Kurallar:
    - Kullanıcıya sade, açık ve teknik olmayan bir dille cevap ver.
    - Eğer dökümanda geçen bir terime veya özelliğe dair bilgi varsa, sadece o bilgi üzerinden cevap ver.
    - Tahminde bulunma, emin olmadığın konuda "Bu konuda elimde bilgi bulunmuyor." de.
    - Dökümanda geçen ekranlar, butonlar, işlem adımları, hata kodları gibi şeylere referans ver.
    - Kullanıcı sorularında geçen bağlamı dikkate alarak detaylı ama özlü cevaplar ver.
    - Basit finansal terimler ile ilgili soru sorunca dökümanda yoksa geminiden cevap ver.
    - Cevapları Türkçe ver.

    Aşağıdaki bağlam (context) ile kullanıcı sorusunu dikkatlice cevapla.
    Eğer cevap bağlamda açıkça belirtilmemişse, "Bu konuda elimde bilgi yok." de.

    Context:
    {context}

    Soru:
    {question}

    Cevap:
    """)

async def makeEmbedding(query: str) -> list:
    embedding = GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)
    return embedding.embed_query(query)

def getVectorStore(collection):
    try:
        embeddings  = GoogleGenerativeAIEmbeddings(model="gemini-embedding-001", google_api_key=googleAPIKEY)
        avs = MongoDBAtlasVectorSearch(collection=collection, embedding=embeddings, index_name="embeddingSemSearch", text_key="answer", embedding_key="qEmbedding") 
        llmLogger.info("Embedding ve MongoDB Atlas vektör arama modeli oluşturuldu.")
        return avs
    except Exception as e:
        llmLogger.exception("HATA: Embedding veya VectorSearch modeli oluşturulamadı!")

async def searchVector(query, collection):
    try:   
        vectorStore = getVectorStore(collection=collection)
        llmLogger.info("Vector Store çalışıyor.")

        # steps = vectorStore.similarity_search(query, K=3)
        # asOutput = steps[1].page_content

        llm = ChatGoogleGenerativeAI(model="gemini-2.5-flash", temperature=0.5, google_api_key=googleAPIKEY)
        retriever = vectorStore.as_retriever()
        
        qa = RetrievalQA.from_chain_type(
            llm=llm,
            chain_type="stuff",
            retriever=retriever,
            chain_type_kwargs={"prompt": system_prompt}
        )
        llmLogger.debug("Ayarlar yüklendi, system_prompt verildi, RetrievalQA zinciri oluşturuldu.")
        
        llmLogger.debug("LLM modeli system_prompt ile çalışıyor.")
        retrieverOutput = qa.run(query)
        return retrieverOutput
        # return asOutput, retrieverOutput
    except Exception as e:
        llmLogger.exception("HATA: LLM zinciri çalışması durduruldu!")
        return "Bir hata oluştu. LLM yanıtı üretilemedi!"
