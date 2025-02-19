import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class MyStompService {
    private webSocketEndpoint = "http://localhost:8085/ws";
    private stompClient:any;
    private jwt = '';
    private connected = false;

  constructor() {
    this.jwt = localStorage.getItem('token')?.trim() || '';
    let ws = new SockJS(this.webSocketEndpoint);
    this.stompClient = Stomp.over(ws);
    this.stompClient.heartbeat.outgoing = 0;
    this.stompClient.heartbeat.incoming = 0;
  }

  connect(successCallback: Function, errorCallback: Function) {
    const _this = this;
    _this.stompClient.connect({ 'Authorization': _this.jwt }, 
      function (frame) {
        _this.connected = true; // Sửa this thành _this
        successCallback(frame);
      },
      function (error) {
        errorCallback(error);
      }
    );
  }
  

  subscribe(destination: string, onMessageCallback: (message: any) => void, headers: {} = {}) {
    if (this.connected) {
      this.stompClient.subscribe(destination, function (event){
        const message = event.body
        console.log('Message received: ' + message);
        
        onMessageCallback(message);
      },{'Authorization': this.jwt})
    } else {
      console.error('Not connected to STOMP server.');
    }
  }

  send(topic: string, message: any, headers?: any) {
    if (this.connected) {
      this.stompClient.send(topic, headers, JSON.stringify(message));
    } else {
      console.error('Not connected to STOMP server. Cannot send message.');
    }
  }
  disconnect() {
    if (this.connected) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from STOMP server');
        this.connected = false;
      });
    }
  }
}
