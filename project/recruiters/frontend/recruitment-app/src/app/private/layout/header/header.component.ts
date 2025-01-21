import { Component, EventEmitter, Output, inject } from '@angular/core'
import { RouterModule } from '@angular/router'
import { NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap'

@Component({
  selector: 'app-header',
  imports: [
    NgbDropdownModule,
    NgbNavModule,
    RouterModule,
  ],
  templateUrl: './header.component.html',
  styles: ``,
  standalone: true
})
export class HeaderComponent {
  scrollY = 0
  @Output() mobileMenuButtonClicked = new EventEmitter()

  constructor() {
    window.addEventListener('scroll', this.handleScroll, { passive: true })
    this.handleScroll()
  }

  handleScroll = () => {
    this.scrollY = window.scrollY
  }
}
