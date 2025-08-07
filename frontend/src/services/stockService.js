import { Client } from '@stomp/stompjs';

class StockService {
    constructor() {
        this.client = new Client({
            brokerURL: 'ws://localhost:8080/ws-stock',
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
        });
        this.subscriptions = {};
        this.pending = [];
        this.isConnected = false;

        this.client.onConnect = frame => {
            this.isConnected = true;
            // bekleyen tüm talepleri işleyelim
            this.pending.forEach(({ topic, onMessage }) => {
                this._doSubscribe(topic, onMessage);
            });
            this.pending = [];
        };

        // Bağlantı koptuğunda bayrağı resetle
        this.client.onWebSocketClose = () => {
            this.isConnected = false;
        };
    }


    activate() {
        if (!this.client.active) {
            this.client.activate();
        }
    }

    _doSubscribe(topic, onMessage) {
    const sub = this.client.subscribe(topic, msg => {
      if (!msg.body) return;
      let data;
      try { data = JSON.parse(msg.body); }
      catch (err) { console.error('JSON parse error:', err); return; }
      onMessage(data);
    });
    this.subscriptions[topic] = sub;
  }

  subscribe(topic, onMessage) {
    // Her zaman client’ı aktifleştir
    this.activate();

    if (this.isConnected) {
      // gerçek bağlantı varsa doğrudan abone ol
      this._doSubscribe(topic, onMessage);
    } else {
      // değilse “pending” listesine ekle
      this.pending.push({ topic, onMessage });
    }
  }

    /**
    * topic aboneliğini iptal et.
    */
    unsubscribe(topic) {
        const sub = this.subscriptions[topic];
        if (sub) {
            sub.unsubscribe();
            delete this.subscriptions[topic];
        }
    }

    disconnect() {
        Object.keys(this.subscriptions).forEach(t => this.unsubscribe(t));
        if (this.client.active) this.client.deactivate();
         this.isConnected = false;
    }

    /*
      connect(onMessage) {
          this.client.onConnect = () => {
              this.client.subscribe('/topic/prices', msg => {
                  // 1) Mesajın ve body’sinin varlığını kontrol et
                  if (!msg || !msg.body) {
                      console.warn('Empty STOMP message or body – skipping');
                      return;
                  }
  
                  // 2) JSON.parse sırasında patlamayı önlemek için try/catch
                  let data;
                  try {
                      data = JSON.parse(msg.body);
                  } catch (err) {
                      console.error('JSON parse error:', err);
                      return;
                  }
  
                  // 3) Gelen verinin gerçekten bir dizi olup olmadığını kontrol et
                  if (!Array.isArray(data)) {
                      console.warn('Expected an array of stocks but got:', data);
                      return;
                  }
  
                  // 4) Nihayet geçerli dizi geldiğinde callback’i çağır
                  onMessage(data);
              });
          };
          this.client.activate();
      }
  
      disconnect() {
          if (this.client.active) {
              this.client.deactivate();
          }
      }*/
}

export default new StockService();
