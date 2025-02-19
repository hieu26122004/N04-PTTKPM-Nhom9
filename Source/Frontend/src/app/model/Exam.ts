import { Vote } from "./Vote";

export interface Exam {
    examId:string;
    name: string;
    classId:string;
    subject: string;
    duration: number;
    provider: string;
    numberOfQuestion: number;
    difficultyLevel: string;
    createdDate: string;
    lastUpdatedDate: string;
    due: string;
    maxAttempts:number;
    status: string;
    type: string;
    id: string;
    new: boolean;
    votes:Vote[];
    upvoteCount:number;
    downvoteCount:number;
    commentCount:number;
}