import { Class } from "../Class";

export interface Announcement {
    announcementId: string;
    content:string;
    date:Date;
    type:string;
    title:string;
    expiresAt:Date;
}