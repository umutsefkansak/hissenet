import { Client } from '@stomp/stompjs';

const NS = 'WS/BORSA';
const styleNs = 'color:#6b7280;font-weight:bold';
const styleOk = 'color:#10b981';
const styleWarn = 'color:#f59e0b';
const styleErr = 'color:#ef4444';

function sizeOf(objOrStr) {
  try {
    const str = typeof objOrStr === 'string' ? objOrStr : JSON.stringify(objOrStr);
    return str.length;
  } catch {
    return -1;
  }
}

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
    this.baglantiDenemesi = 0;
    this.mesajSayaci = {};

    this.client.debug = (str) => console.log('%s %c[STOMP]%c %s', NS, styleNs, '', str);

    this.client.onConnect = (frame) => {
      this.isConnected = true;
      this.baglantiDenemesi = 0;
      console.log('%s %cBAĞLANTI SAĞLANDI%c oturum=%s', NS, styleOk, '', frame?.headers?.session || '(yok)');

      if (this.pending.length) {
        console.log('%s %cBEKLEYEN ABONELİKLER%c adet=%d', NS, styleWarn, '', this.pending.length);
      }
      this.pending.forEach(({ topic, onMessage }) => this._doSubscribe(topic, onMessage));
      this.pending = [];
    };

    this.client.onStompError = (frame) => {
      console.error('%s %cBROKER HATASI%c %s\n%s', NS, styleErr, '', frame?.headers?.message, frame?.body || '');
    };

    this.client.onWebSocketError = (evt) => {
      console.error('%s %cWEBSOCKET HATASI%c', NS, styleErr, '', evt);
    };

    this.client.onWebSocketClose = (evt) => {
      this.isConnected = false;
      console.warn('%s %cBAĞLANTI KAPANDI%c kod=%s sebep=%s', NS, styleWarn, '', evt?.code, evt?.reason);
    };

    this.client.onUnhandledMessage = (message) => {
      console.warn('%s %cİŞLENMEYEN MESAJ%c %o', NS, styleWarn, '', message);
    };
  }

  activate() {
    if (!this.client.active) {
      this.baglantiDenemesi += 1;
      console.log('%s %cBAĞLANIYOR%c deneme=%d url=%s', NS, styleNs, '', this.baglantiDenemesi, this.client.brokerURL);
      this.client.activate();
    } else {
      console.log('%s %cZATEN BAĞLI%c', NS, styleNs, '');
    }
  }

  _doSubscribe(topic, onMessage) {
    if (this.subscriptions[topic]) {
      console.log('%s %cABONELİK ATLANDI%c zaten abone olunan konu=%s', NS, styleWarn, '', topic);
      return;
    }

    console.log('%s %cABONE OLUNUYOR%c konu=%s', NS, styleOk, '', topic);

    this.mesajSayaci[topic] = 0;

    const sub = this.client.subscribe(topic, (msg) => {
      if (!msg.body) {
        console.warn('%s %cBOŞ MESAJ%c konu=%s', NS, styleWarn, '', topic);
        return;
      }
      let data;
      try {
        data = JSON.parse(msg.body);
      } catch (err) {
        console.error('%s %cJSON PARSE HATASI%c konu=%s hata=%o', NS, styleErr, '', topic, err);
        return;
      }

      this.mesajSayaci[topic] += 1;
      const sayac = this.mesajSayaci[topic];
      const boyut = sizeOf(msg.body);

      console.log('%s %cMESAJ ALINDI%c konu=%s adet=%d boyut=%d karakter', NS, styleNs, '', topic, sayac, boyut);

      try {
        onMessage(data);
      } catch (cbErr) {
        console.error('%s %cMESAJ İŞLEME HATASI%c konu=%s hata=%o', NS, styleErr, '', topic, cbErr);
      }
    });

    this.subscriptions[topic] = sub;
    console.log('%s %cABONELİK TAMAMLANDI%c konu=%s id=%s', NS, styleOk, '', topic, sub?.id);
  }

  subscribe(topic, onMessage) {
    this.activate();
    console.log('%s %cABONE OL TALEBİ%c konu=%s bağlı=%s', NS, styleNs, '', topic, this.isConnected);

    if (this.isConnected) {
      this._doSubscribe(topic, onMessage);
    } else {
      console.log('%s %cBEKLEMEYE ALINDI%c konu=%s', NS, styleWarn, '', topic);
      this.pending.push({ topic, onMessage });
    }
  }

  unsubscribe(topic) {
    const sub = this.subscriptions[topic];
    if (sub) {
      console.log('%s %cABONELİK İPTAL%c konu=%s id=%s', NS, styleWarn, '', topic, sub?.id);
      sub.unsubscribe();
      delete this.subscriptions[topic];
      delete this.mesajSayaci[topic];
    } else {
      console.log('%s %cABONELİK BULUNAMADI%c konu=%s', NS, styleWarn, '', topic);
    }
  }

  disconnect() {
    Object.keys(this.subscriptions).forEach((t) => this.unsubscribe(t));
    if (this.client.active) {
      console.log('%s %cBAĞLANTI KAPATILIYOR%c', NS, styleWarn, '');
      this.client.deactivate();
    }
    this.isConnected = false;
  }
}

export default new StockService();
