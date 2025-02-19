import { Component, OnDestroy, OnInit } from '@angular/core';
import { MyStompService } from '../service/stomp.service';
import { PrivateChatMessage } from '../model/PrivateChatMessage';
import { ShareRefService } from '../service/share-ref.service';
import { Class } from '../model/Class';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css',
  standalone: false
})
export class NavComponent implements OnInit, OnDestroy {
  appName = 'Practice Make Perfect';

  notifications:PrivateChatMessage[] = [];
  clazzes:Class[] = [];

  constructor(
    private stompService: MyStompService,
    private shareRefService: ShareRefService) {
  }
  ngOnDestroy(): void {
    this.stompService.disconnect();
  }

  ngOnInit(): void {
    const savedNotifications = localStorage.getItem('notifications');
    if (savedNotifications) {
      this.notifications = JSON.parse(savedNotifications);
    }
    this.stompService.connect(this.onConnectStomp.bind(this), this.onDisconnectStomp.bind(this));2
    this.shareRefService.clazz$.subscribe((clazz) => {
      this.clazzes = clazz;
    })
    this.clazzes.forEach((clazz) => {
      this.stompService.subscribe("/app/class/" + clazz.classId,this.onMessageReceived.bind(this),{});
    })
  }
  

  onConnectStomp(frame){
    console.log('Connected: ' + frame);
    this.stompService.subscribe('/user/queue/messages',this.onMessageReceived.bind(this),{});
  }

  onDisconnectStomp(error){
    console.log('Disconnected');
  }

  onMessageReceived(message){
    const msg = JSON.parse(message);
    this.notifications.push(msg);
  }


  onClickNotification(){
  }

}
