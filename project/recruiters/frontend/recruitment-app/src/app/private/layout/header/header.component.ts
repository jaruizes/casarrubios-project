import {Component} from '@angular/core'
import {Router, RouterModule} from '@angular/router'
import {NgbDropdownModule, NgbNavModule} from '@ng-bootstrap/ng-bootstrap'

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

  private router:Router;

  constructor(router: Router) {
    window.addEventListener('scroll', this.handleScroll, { passive: true });
    this.handleScroll();
    this.router = router;
  }

  handleScroll = () => {
    this.scrollY = window.scrollY
  }

  goHome() {
    this.router.navigate(['/private/home', { }]);
  }

}
