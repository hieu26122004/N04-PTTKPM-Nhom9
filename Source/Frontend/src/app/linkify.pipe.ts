import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'linkify',
  standalone: false
})
export class LinkifyPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): SafeHtml {
    const urlRegex = /(https?:\/\/[^\s]+)/g; 
    const contentWithLinks = value.replace(
      urlRegex,
      '<a href="$1" target="_blank" rel="noopener noreferrer">$1</a>'
    );
    return this.sanitizer.bypassSecurityTrustHtml(contentWithLinks);
  }
}
