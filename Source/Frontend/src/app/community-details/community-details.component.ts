import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import { PublicChatMessage } from '../model/PublicChatMessage';
import { jwtDecode, JwtPayload } from 'jwt-decode';
import { CustomJwtPayload } from '../../CustomJwtPayload';
import { ShareRefService } from '../service/share-ref.service';

@Component({
  selector: 'app-community-details',
  templateUrl: './community-details.component.html',
  styleUrls: ['./community-details.component.css'],
  standalone: false
})
export class CommunityDetailsComponent implements OnInit, OnDestroy {
  private webSocketEndpoint = 'http://localhost:8085/ws';
  private topic: string = '/topic/math';
  private stompClient: any;
  private jwt: string = '';
  messages: PublicChatMessage[] = [];
  messageInput: string = '';
  username: string = '';

  navHeight: number;
  containerHeight: number;
  messageContainerHeight: number;
  
  @ViewChild('messageContainer', { static: false }) messageContainer!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private shareRefSerivce: ShareRefService,
  ) { }

  ngOnInit(): void {
    this.navHeight = this.shareRefSerivce.navHeight;
    this.containerHeight = window.innerHeight - this.navHeight;
    this.messageContainerHeight = this.containerHeight * 0.8;

    this.topic = this.route.snapshot.paramMap.get('id') || '/topic/math';
    console.log('Inside ' + this.topic);
    this.jwt = localStorage.getItem('token')?.trim() || '';

    let ws = new SockJS(this.webSocketEndpoint);
    this.stompClient = Stomp.over(ws);
    this.stompClient.heartbeat.outgoing = 0;  // Disable heartbeat
    this.stompClient.heartbeat.incoming = 0;  // Disable heartbeat

    const _this = this;
    this.stompClient.connect({ 'Authorization': this.jwt }, function (frame: any) {
      console.log('Connected: ' + frame);
      _this.stompClient.subscribe(_this.topic, function (event: any) {
        console.log('Received: ' + event.body);
        const message = JSON.parse(event.body);
        _this.onReceiveMessage(message);
      }, { 'Authorization': _this.jwt });
    }, (error: any) => {
      console.error('WebSocket connection error: ', error);
    });

    const decodedToken: CustomJwtPayload = jwtDecode(this.jwt);
    this.username = decodedToken.username?.trim() || '';
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket');
      });
    }
  }

  onReceiveMessage(msg: PublicChatMessage): void {
    this.messages.push(msg);
    setTimeout(() => {
      if (this.messageContainer) {
        this.messageContainer.nativeElement.scrollTop = this.messageContainer.nativeElement.scrollHeight;
      }
    }, 0); // Scroll to the bottom after message is added
  }

  public sendMessage(): void {
    if (this.messageInput.trim()) {
      const publicChatMsg = new PublicChatMessage(this.username, this.messageInput, new Date());
      const actualRoomName = this.topic.replace('/topic/', '').trim();
      console.log("Sending to /app/chat/" + actualRoomName);
      this.stompClient.send('/app/chat/' + actualRoomName, { 'Authorization': this.jwt }, JSON.stringify(publicChatMsg));
      this.messageInput = '';
    }
  }

  isMyMessage(msg: PublicChatMessage): boolean {
    return msg.sender === this.username;
  }
}
