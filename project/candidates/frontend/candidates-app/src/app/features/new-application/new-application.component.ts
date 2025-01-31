import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import {DatePipe, NgIf} from "@angular/common";
import {Position} from "../../model/position";

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

  constructor(private fb: FormBuilder, private router: Router) {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
      cv: [null, Validators.required]
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.uploadedFile = file;
      this.applicationForm.patchValue({ cv: file });
    } else {
      alert('Only PDF files are allowed.');
    }
  }

  submitApplication() {
    if (this.applicationForm.valid) {
      console.log('Application submitted:', this.applicationForm.value);
      alert('Application submitted successfully!');
      this.applicationFinished.emit();
    }
  }
}
