# -*- coding: utf-8 -*-
import json
import logging
from django.http import JsonResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt

from LLM.llmDbOperate import getLlmResponse

chatLogger = logging.getLogger('chat')

def get_bot_response(message):
    chatLogger.info(f"LLM çağrısı başlatıldı. Kullanıcı mesajı: {message}")

    try:
        llmResponse = getLlmResponse(message=message)
        chatLogger.debug(f"LLM yanıtı alındı: {llmResponse}")
        return llmResponse
    except Exception as e:
        chatLogger.exception("LLM yanıtı alınırken hata oluştu!")
        return "Bir hata oluştu. Tekrar deneyin."

def chat_page(request):
    return render(request, 'chat.html')

@csrf_exempt
def chat_api(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            user_message = data.get('message', '')
            chatLogger.info(f"Kullanıcı mesaj gönderdi: {user_message}")

            bot_reply = get_bot_response(user_message)
            chatLogger.info("LLM yanıtı gönderildi: {bot_reply}")
            return JsonResponse({'response': bot_reply})

        except Exception as e:
            chatLogger.exception("chatbot_api endpointinde hata oluştu!")
            return JsonResponse({'error':'Sunucu hatası!'}, status=500)

    return JsonResponse({'error': 'POST method required'}, status=400)
