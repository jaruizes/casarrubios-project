import {Component, OnInit} from '@angular/core'
import {Router, RouterModule} from '@angular/router'
import {NgbDropdownModule, NgbNavModule} from '@ng-bootstrap/ng-bootstrap'
import {NotificationsService} from "../../services/notifications/notifications.service";

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
export class HeaderComponent implements OnInit {
  scrollY = 0;
  newNotification: boolean = false;

  constructor(private router: Router, private notificationsService: NotificationsService) {
    window.addEventListener('scroll', this.handleScroll, { passive: true });
    this.handleScroll();
    this.router = router;
  }

  ngOnInit() {
    this.notificationsService.notifications$.subscribe(notification => {
      this.newNotification = true;
    });
  }

  handleScroll = () => {
    this.scrollY = window.scrollY
  }

  goHome() {
    this.router.navigate(['/private/home', { }]);
  }

  resetNotificationsBadge() {
    this.newNotification = false;
  }

}
