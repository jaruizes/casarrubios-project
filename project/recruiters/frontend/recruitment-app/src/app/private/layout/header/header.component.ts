import {Component, OnInit} from '@angular/core'
import {Router, RouterModule} from '@angular/router'
import {NgbDropdownModule, NgbNavModule} from '@ng-bootstrap/ng-bootstrap'
import {NotificationsService} from "../../services/notifications/notifications.service";
import {Notification} from '../../model/notification';

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
  notifications: Notification [] = [];

  constructor(private router: Router, private notificationsService: NotificationsService) {
    window.addEventListener('scroll', this.handleScroll, { passive: true });
    this.handleScroll();
    this.router = router;
  }

  ngOnInit() {
    this.notificationsService.notifications$.subscribe(notification => {
      this.newNotification = true;
      this.notifications.push(notification);
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
    this.notifications.forEach(notification => {
      notification.seen = true;
    })
  }

}
