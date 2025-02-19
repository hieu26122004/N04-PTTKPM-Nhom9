import { Component, ElementRef, OnInit, AfterViewInit, ViewChild, HostListener } from '@angular/core';
import { ShareRefService } from '../service/share-ref.service';
import { ChatService } from '../service/chat.service';
import { AuthService } from '../service/auth-service.service';
import { PrivateChatMessage } from '../model/PrivateChatMessage';
import { MyStompService } from '../service/stomp.service';

@Component({
  selector: 'app-messages',
  standalone: false,
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.css']
})
export class MessagesComponent implements OnInit, AfterViewInit {
  navHeight: number;
  containerHeight: number;
  messageContainerWidth: number;
  inputWidth: number;


  participantsAndGroups: string[] = []; 
  messages: Map<string, PrivateChatMessage[]> = new Map<string, PrivateChatMessage[]>();
  currentTargetUserName = '';
  currentTargetUserId: string;

  lastTimeLoad = '2024-12-01';
  private jwt = '';
  @ViewChild('messageContainer', { static: false }) messageContainer!: ElementRef;




  // Thêm biến cho nội dung tin nhắn
  newMessage: string = '';

  constructor(
    private shareRefSerivce: ShareRefService,
    private chatService: ChatService,
    private authService: AuthService,
    private stompService: MyStompService
  ) {}

  ngOnInit() {
    this.jwt = localStorage.getItem('token')?.trim() || '';
    this.navHeight = this.shareRefSerivce.navHeight;
    this.containerHeight = window.innerHeight - this.navHeight;

    this.chatService.getParticipantsAndGroups(this.lastTimeLoad).subscribe(
      (data) => {
        this.participantsAndGroups = data.pag;
        this.lastTimeLoad = data.lastTimeLoad;
        if (this.participantsAndGroups.length > 0) {
          this.currentTargetUserName = this.participantsAndGroups[0];
          this.loadMessages();
        }
      },
      (error) => {
        console.error('Error fetching participants and groups:', error);
      }
    );

    this.stompService.connect(this.successCallback.bind(this), (error) => {
      console.log("Something went wrong", error);
    });
  }

  successCallback(frame) {
    this.stompService.subscribe('/user/queue/messages', this.onMessageReceived.bind(this), {});
  }

  ngAfterViewInit() {
    this.updateMessageContainerWidth();
    this.adjustMessageInputWidth();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.updateMessageContainerWidth();
    this.adjustMessageInputWidth();
  }

  updateMessageContainerWidth() {
    if (this.messageContainer) {
      this.messageContainerWidth = this.messageContainer.nativeElement.offsetWidth;
      this.adjustMessageInputWidth();
    }
  }

  loadMessages() {
    if (this.currentTargetUserName) {
      this.chatService.getMessages(this.lastTimeLoad, this.currentTargetUserName).subscribe(
        (messages) => {
          this.messages.set(this.currentTargetUserName, messages);
        },
        (error) => {
          console.error('Error fetching messages:', error);
        }
      );
    }
  }

  isMyMessage(message: PrivateChatMessage): boolean {
    return message.sender !== this.currentTargetUserName;
  }

  switchTargetUser(userName: string) {
    this.currentTargetUserName = userName;
    this.loadMessages();
  }

  adjustMessageInputWidth() {
    if (this.messageContainerWidth) {
      this.inputWidth = this.messageContainerWidth * 0.9;
    }
  }

  sentMessage() {
    if (this.newMessage.trim()) {
      let newMessage = new PrivateChatMessage('', this.currentTargetUserName, this.newMessage, null, null, null);
      this.stompService.send('/app/chat.private', newMessage, {'Authorization': this.jwt} );
      this.messages.get(this.currentTargetUserName)?.push(newMessage);
      this.newMessage = '';

    }
  }

  onMessageReceived(message) {
    const parsedMessage = JSON.parse(message);
    if (this.messages.has(this.currentTargetUserName)) {
      this.messages.get(this.currentTargetUserName)?.push(parsedMessage);
    } else {
      this.messages.set(this.currentTargetUserName, [parsedMessage]);
    }
  }
}
