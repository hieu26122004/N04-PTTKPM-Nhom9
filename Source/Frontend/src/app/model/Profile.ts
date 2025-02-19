import { Preferences } from "./Preferences";
import { SecuritySettings } from "./SecuritySettings";
import { UserStatistics } from "./UserStatistics";

export interface Profile{
    userId:string;
    userName:string;
    email:string;
    birthDate:Date;
    bio:string;
    profilePicture:string;
    roles:string[];
    status:string;
    createdAt:Date;
    lastLogin:Date;
    securitySettings:SecuritySettings;
    preferences:Preferences;
    userStatistics:UserStatistics;
    friends:Profile[];
}