import json
import asyncio
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

from LLM.llm import makeEmbedding
from Database.database import readDBUri, connectDB, getCollection, findMatch, getResults

# from your_chatbot_module import get_bot_response

def get_bot_response(message):
    uri: str      = readDBUri()
    db            = connectDB(uri=uri)
    collection    = getCollection(db=db)
    embeddedQuery = asyncio.run(makeEmbedding(query=message))
    results       = findMatch(collection=collection, embeddedQuery=embeddedQuery)
    results=getResults(results=results)
    return results[0]["answer"]

def chat_page(request):
    return render(request, 'chat.html')

@csrf_exempt
def chat_api(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        user_message = data.get('message', '')
        bot_reply = get_bot_response(user_message)
        return JsonResponse({'response': bot_reply})
    return JsonResponse({'error': 'POST method required'}, status=400)
