import React, { useState, useRef, useEffect } from 'react';

const Chatbot = () => {
    const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([]); 
  const chatboxRef = useRef(null);

  useEffect(() => {
    if (chatboxRef.current) {
      chatboxRef.current.scrollTop = chatboxRef.current.scrollHeight;
    }
  }, [messages]);

  const toggleChat = () => {
    setIsOpen(!isOpen);
  };

  const sendMessage = async () => {
    const trimmed = input.trim();
    if (!trimmed) return;

    setMessages(prev => [...prev, { sender: 'user', text: trimmed }]);
    setInput('');

    try {
      const response = await fetch('/api/chat/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: input }),
      });

      const data = await response.json();

      setMessages(prev => [...prev, { sender: 'bot', text: data.response }]);
    } catch (error) {
      setMessages(prev => [...prev, { sender: 'bot', text: 'Bir hata oluştu.' }]);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  return (
    <>
      <button
        onClick={toggleChat}
        style={{
          position: 'fixed',
          bottom: 20,
          right: 20,
          zIndex: 1001,
          backgroundColor: '#17313E',
          color: 'white',
          border: 'none',
          padding: '14px 16px',
          borderRadius: '50%',
          fontSize: 20,
          cursor: 'pointer',
          boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
        }}
        aria-label="Chat Aç/Kapat"
      >
        💬
      </button>

      {isOpen && (
        <div
          style={{
            position: 'fixed',
            bottom: 80,
            right: 20,
            width: 350,
            height: 500,
            background: '#ffffff',
            border: '1px solid #ddd',
            borderRadius: 16,
            boxShadow: '0 6px 18px rgba(0,0,0,0.25)',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
            zIndex: 1000,
          }}
        >
          <div class='you-text margin: 10'> Merhaba, ben HisseNet hisse alım satım platformu asistanıyım. Size nasıl yardımcı olabilirim?</div>
          <div
            ref={chatboxRef}
            style={{
              flex: 1,
              padding: 16,
              overflowY: 'auto',
              fontSize: 15,
            }}
          >
            {messages.map((msg, i) => (
              <div key={i} style={{ marginBottom: 10 }}>
                <strong style={{ color: msg.sender === 'user' ? '#447D9B' : '#415E72' }}>
                  {msg.sender === 'user' ? 'Sen:': 'Asistan:'}
                </strong>{' '}
                {msg.text}
              </div>
            ))}
          </div>
          <div
            style={{
              display: 'flex',
              borderTop: '1px solid #eee',
              padding: 10,
            }}
          >
            <input
              type="text"
              placeholder="Asistan'a sor"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              style={{
                flex: 1,
                padding: 10,
                borderRadius: 8,
                border: '1px solid #ccc',
                outline: 'none',
              }}
            />
            <button
              onClick={sendMessage}
              style={{
                backgroundColor: '#415E72',
                color: 'white',
                border: 'none',
                padding: '10px 14px',
                marginLeft: 8,
                borderRadius: 8,
                cursor: 'pointer',
              }}
            >
              Gönder
            </button>
          </div>
        </div>
      )}
    </>
  );
};

export default Chatbot;
