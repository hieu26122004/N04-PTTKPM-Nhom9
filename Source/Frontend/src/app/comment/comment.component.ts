import { Component, Input } from '@angular/core';
import { Comment } from '../model/Comment';
import { CommentService } from '../service/comment.service';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css'],
  standalone: false,
})
export class CommentComponent {
  @Input() comment!: Comment;
  replies: Comment[] = [];
  showReplies: boolean = false;
  showEditor: boolean = false;
  replyContent: string = '';

  editorModules = {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      [{ script: 'sub' }, { script: 'super' }],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image', 'code-block'],
      [{ color: [] }, { background: [] }],
    ],
  };

  constructor(private commentService: CommentService) {}

  toggleReplies(): void {
    if (!this.showReplies) {
      const lastReply = this.replies[this.replies.length - 1];
      const lastUpdated = lastReply ? lastReply.lastUpdated : null;
      const lastId = lastReply ? lastReply.id : null;

      this.commentService
        .getCommentByExamId(this.comment.examId, this.comment.id, lastUpdated, lastId)
        .subscribe({
          next: (replies: Comment[]) => {
            this.replies.push(...replies);
            console.log('Replies loaded:', replies);
          },
          error: (err) => {
            console.error('Error fetching replies:', err);
          },
        });
    }
    this.showReplies = !this.showReplies;
  }

  toggleEditor(): void {
    this.showEditor = !this.showEditor;
  }

  submitReply(): void {
    if (!this.replyContent.trim()) {
      console.warn('Reply content is empty!');
      return;
    }

    const newComment: Comment = {
      id: null,
      author: '', // Set author if available
      examId: this.comment.examId,
      parentId: this.comment.id,
      content: this.replyContent,
      createdAt: new Date(),
      lastUpdated: new Date(),
      replies: 0,
      deleted: false,
    };

    this.addReply(newComment);
    console.log('Reply submitted:', newComment);
    this.replyContent = '';
    this.showEditor = false;
  }

  addReply(newComment: Comment): void {
    this.commentService.addReply(this.comment.id, newComment).subscribe({
      next: (comment: Comment) => {
        console.log('Reply added successfully:', comment);
        this.replies.unshift(comment);
        this.comment.replies = (this.comment.replies || 0) + 1;
      },
      error: (err) => {
        console.error('Error adding reply:', err);
      },
    });
  }
}
