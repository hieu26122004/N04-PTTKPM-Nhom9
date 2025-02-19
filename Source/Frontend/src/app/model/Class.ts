import { Announcement } from "./class/Announcement";
import { ClassUser } from "./class/ClassUser";
import { Material } from "./class/Material";

export interface Class {
    classId:string;
    className:string;
    description:string;
    createdDate:Date;
    participants:ClassUser[];
    materials:Material[],
    announcements:Announcement[]
}