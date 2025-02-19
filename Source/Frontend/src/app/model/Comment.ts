export interface Comment {
    id: string;
    author: string;
    examId: string;
    content: string;
    createdAt: Date;
    lastUpdated: Date;
    parentId?: string;
    replies: number;
    deleted: boolean;
  }
  