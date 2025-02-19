export class PrivateChatMessage{
    sender:string;
    recipient:string;
    content:string;
    timestamp:Date;
    attachment:any;
    conversation:string;

    constructor(sender:string, recipient:string, content:string, timestamp:Date, attachment:any, conversation:string){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.attachment = attachment;
        this.conversation = conversation;
    }

}