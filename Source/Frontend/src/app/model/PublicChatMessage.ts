export class PublicChatMessage {
  sender: string;
  content: string;
  timestamp: Date;

  constructor(sender: string, content: string, timestamp: Date) {
    this.sender = sender;
    this.content = content;
    this.timestamp = timestamp;
  }
}

