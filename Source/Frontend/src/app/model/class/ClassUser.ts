import { Class } from "../Class";

export interface ClassUser{
    userId:string,
    username:string,
    email:string,
    classes:Class[],
}