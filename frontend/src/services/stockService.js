import { Client } from '@stomp/stompjs';

class StockService {
    constructor() {
        this.client = new Client({
            brokerURL: 'ws://localhost:8080/ws-stock',
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
        });
    }

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
    }
}

export default new StockService();
