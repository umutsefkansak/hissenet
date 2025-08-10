import React, { useState, useRef, useEffect } from 'react';
import styles from './Chatbot.module.css';
import ChatIcon from '../Icons/ChatIcon';
import RefreshIcon from '../Icons/RefreshIcon';
import SendIcon from '../Icons/SendIcon';
import AIRobotIcon from '../Icons/AIRobotIcon';
import ArrowRightIcon from '../Icons/ArrowRightIcon';

const Chatbot = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [input, setInput] = useState('');
    const [messages, setMessages] = useState([
        {
            sender: 'bot',
            text: 'Merhaba! Ben HisseNet hisse alım satım platformu asistanıyım. Size nasıl yardımcı olabilirim?'
        }
    ]);
    const [isLoading, setIsLoading] = useState(false);
    const [showSuggestions, setShowSuggestions] = useState(true);
    const chatboxRef = useRef(null);
    const inputRef = useRef(null);

    const suggestions = [
        "Bugünün en çok yükselen hisse senetleri hangileri?",
        "BIST 100 endeksi nasıl performans gösteriyor?",
        "Nasıl hisse senedi alım emri verebilirim?",
        "Portföyümde hangi hisseler var?",
        "Bu hafta hangi şirketler temettü dağıtacak?",
        "Dolar/TL kuru bugün nasıl?"
    ];

    useEffect(() => {
        if (chatboxRef.current) {
            chatboxRef.current.scrollTop = chatboxRef.current.scrollHeight;
        }
    }, [messages]);

    const toggleChat = () => {
        setIsOpen(!isOpen);
        if (!isOpen) {
            setTimeout(() => {
                if (chatboxRef.current) {
                    chatboxRef.current.scrollTop = chatboxRef.current.scrollHeight;
                }
            }, 50);
        }
    };

    const sendMessage = async (messageText = null) => {
        const messageToSend = messageText || input.trim();
        if (!messageToSend || isLoading) return;

        if (messageText) {
            setShowSuggestions(false);
        }

        setMessages(prev => [...prev, { sender: 'user', text: messageToSend }]);
        setInput('');
        setIsLoading(true);

        setTimeout(() => {
            if (inputRef.current) {
                inputRef.current.focus();
            }
        }, 0);

        try {
            const url = "http://localhost:8000/api/chat/";

            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: messageToSend }),
            });

            const data = await response.json();

            setMessages(prev => [...prev, { sender: 'bot', text: data.response }]);
        } catch (error) {
            setMessages(prev => [...prev, { sender: 'bot', text: 'Bir hata oluştu. Lütfen tekrar deneyin.' }]);
        } finally {
            setIsLoading(false);
            setTimeout(() => {
                if (inputRef.current) {
                    inputRef.current.focus();
                }
            }, 100);
        }
    };

    const clearChat = () => {
        setMessages([
            {
                sender: 'bot',
                text: 'Merhaba! Ben HisseNet hisse alım satım platformu asistanıyım. Size nasıl yardımcı olabilirim?'
            }
        ]);
        setShowSuggestions(true);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    const handleSuggestionClick = (suggestion) => {
        sendMessage(suggestion);
    };

    return (
        <div className={styles.chatbotContainer}>
            <button
                onClick={toggleChat}
                className={styles.toggleButton}
                aria-label="Chat Aç/Kapat"
            >
                <ChatIcon className={styles.toggleButtonIcon} />
            </button>

            {isOpen && (
                <div className={styles.chatContainer}>
                    <div className={styles.chatHeader}>
                        <span>HisseNet Asistan</span>
                        <button
                            onClick={clearChat}
                            className={styles.clearButton}
                            aria-label="Sohbeti Temizle"
                            title="Sohbeti Temizle"
                        >
                            <RefreshIcon className={styles.refreshIcon} />
                        </button>
                    </div>

                    <div ref={chatboxRef} className={styles.chatBox}>
                        {messages.map((msg, i) => (
                            <div key={i} className={styles.message}>
                                <div className={msg.sender === 'user' ? styles.userMessage : styles.botMessage}>
                                    <div className={`${styles.messageContent} ${msg.sender === 'user' ? styles.userMessageContent : styles.botMessageContent}`}>
                                        {msg.sender === 'bot' && <AIRobotIcon className={styles.aiIcon} />}
                                        <span>{msg.text}</span>
                                    </div>
                                </div>
                            </div>
                        ))}

                        {/* Suggestions - Sadece ilk mesajdan sonra ve henüz kullanıcı mesaj göndermemişse göster */}
                        {showSuggestions && messages.length === 1 && (
                            <div className={styles.suggestionsContainer}>
                                {suggestions.slice(0, 3).map((suggestion, index) => (
                                    <button
                                        key={index}
                                        onClick={() => handleSuggestionClick(suggestion)}
                                        className={styles.suggestionButton}
                                        style={{
                                            animationDelay: `${index * 100}ms`
                                        }}
                                    >
                                        <span className={styles.suggestionText}>
                                            {suggestion}
                                        </span>
                                        <ArrowRightIcon className={styles.suggestionArrow} />
                                    </button>
                                ))}
                            </div>
                        )}

                        {isLoading && (
                            <div className={styles.message}>
                                <div className={styles.botMessage}>
                                    <div className={`${styles.messageContent} ${styles.botMessageContent}`}>
                                        <AIRobotIcon className={styles.aiIcon} />
                                        <div className={styles.loadingContainer}>
                                            <div className={styles.loadingDots}>
                                                <div className={styles.loadingDot}></div>
                                                <div className={styles.loadingDot}></div>
                                                <div className={styles.loadingDot}></div>
                                            </div>
                                            Yazıyor...
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className={styles.inputContainer}>
                        <input
                            ref={inputRef}
                            type="text"
                            placeholder="Mesajınızı yazın..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={handleKeyDown}
                            className={styles.textInput}
                            disabled={isLoading}
                        />
                        <button
                            onClick={() => sendMessage()}
                            className={styles.sendButton}
                            disabled={!input.trim() || isLoading}
                            aria-label="Mesaj Gönder"
                        >
                            <SendIcon className={styles.sendButtonIcon} />
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Chatbot;