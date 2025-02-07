import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PositionsService} from "../../services/positions.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Position} from "../../model/position";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {NewApplicationComponent} from "../new-application/new-application.component";
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-position-view',
  standalone: true,
  imports: [
    DatePipe,
    NgForOf,
    NgIf,
    NewApplicationComponent
  ],
  templateUrl: './position-view.component.html',
  styleUrl: './position-view.component.scss'
})
export class PositionViewComponent implements OnInit {
  positionId!: number;
  position!: Position;
  @ViewChild('applyToPositionModal')
  applyToPositionModal!: TemplateRef<HTMLElement>;

  @ViewChild('newApplicationComponent')
  newApplicationComponent!: NewApplicationComponent;

  private positionService: PositionsService;
  private router: Router;
  private modalService: NgbModal;
  private modalRef!: NgbModalRef;

  constructor(private route: ActivatedRoute, router: Router, positionService: PositionsService, modalService: NgbModal) {
    this.positionService = positionService;
    this.router = router;
    this.modalService = modalService;
  }

  ngOnInit(): void {
    this.positionId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.positionId > 0) {
      this.positionService.getPositionById(this.positionId).subscribe((position) => {
        this.position = position;
      });
    }
  }

  back() {
    this.router.navigate(['home']);
  }

  closeModal() {
    this.modalRef.close()
  }

  openApplyForm() {
    this.modalRef = this.modalService.open(this.applyToPositionModal, {
      backdrop: 'static',
      keyboard: false,
    });
  }

  applyToPosition() {
    // this.applyToPositionModal.elementRef.nativeElement.submitApplication();
    this.newApplicationComponent.submitApplication();
  }


}
