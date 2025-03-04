import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DatePipe, Location} from "@angular/common";
import {ApplicationDetail} from "../../model/application";
import {Position} from "../../model/position";
import {ApplicationsService} from "../../services/applications/applications.service";

@Component({
  selector: 'app-application-detail',
  templateUrl: './application-detail.component.html',
  standalone: true,
  imports: [
    DatePipe,
  ],
  styleUrls: ['./application-detail.component.scss']
})
export class ApplicationDetailComponent implements OnInit {
  application!: ApplicationDetail;
  position!: Position;

  private applicationId!: string;

  constructor(private location: Location, private route: ActivatedRoute, private applicationsService: ApplicationsService) {
  }

  ngOnInit(): void {
    this.applicationId = this.route.snapshot.paramMap.get('id') || '';
    this.applicationsService.getApplicationDetail(this.applicationId).subscribe((application: ApplicationDetail) => {
      this.application = application;
    });
  }

  back(): void {
    this.location.back();
  }

  downloadCV() {
    const link = document.createElement('a');
    link.href = this.application.cvFile;
    link.download = this.application.id + '.pdf';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
