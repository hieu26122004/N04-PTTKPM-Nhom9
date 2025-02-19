import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { ShareRefService } from './service/share-ref.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'], 
  standalone: false
})
export class AppComponent implements AfterViewInit {
  title = 'pratice-make-perfect';

  @ViewChild('navComponent', { static: false }) navComponent!: ElementRef;

  constructor(private shareRefService: ShareRefService) {}

  ngAfterViewInit(): void {
    const navHeight = this.navComponent.nativeElement.offsetHeight;
    console.log('Chiều cao của app-nav:', navHeight);
    this.shareRefService.navHeight = navHeight;
  }
}
