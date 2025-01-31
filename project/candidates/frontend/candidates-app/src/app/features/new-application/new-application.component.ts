import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DatePipe, NgIf} from "@angular/common";
import {Position} from "../../model/position";
import {Application, CandidateData} from "../../model/application";
import {ApplicationsService} from "../../services/applications.service";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-new-application',
  templateUrl: './new-application.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
    DatePipe
  ],
  styleUrls: ['./new-application.component.scss']
})
export class NewApplicationComponent {
  @Input() position!: Position;
  @Output() applicationFinished = new EventEmitter<void>();

  applicationForm: FormGroup;
  uploadedFile: File | null = null;

  private applicationService: ApplicationsService;

  constructor(private fb: FormBuilder, applicationService: ApplicationsService) {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
      cv: [null, Validators.required]
    });

    this.applicationService = applicationService;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.uploadedFile = file;
      this.convertFileToBase64(file);
    } else {
      alert('Only PDF files are allowed.');
    }
  }

  submitApplication() {
    if (this.applicationForm.valid) {
      this.applicationService.applyToPosition(this.buildApplicationData()).subscribe(
        {
          next: (response) => {
            Swal.fire('Application submitted successfully!', 'We will contact you soon.', 'success').then(() => {
              this.applicationFinished.emit();
            });

          },
          error: (error) => {
            Swal.fire({
              icon: 'error',
              title: 'Oops...',
              text: 'Something went wrong!',
            })
          }
        }
      );
    }
  }

  private buildApplicationData(): Application {
    return {
      positionId:  this.position.id,
      candidate: {
        name: this.applicationForm.get('name')?.value,
        email: this.applicationForm.get('email')?.value,
        phone: this.applicationForm.get('phone')?.value,
        cv: this.applicationForm.get('cv')?.value
      }
    };
  }

  private convertFileToBase64(file: File) {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      this.applicationForm.patchValue({ cv: reader.result?.toString() }); // base64 string
    };
    reader.onerror = (error) => {
      console.error('Error converting file:', error);
    };
  }
}
