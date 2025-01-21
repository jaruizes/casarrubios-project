import { Component, OnInit, inject } from '@angular/core'
import {
  FormsModule,
  ReactiveFormsModule,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms'
import { Router, RouterLink} from '@angular/router'

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [RouterLink, FormsModule, ReactiveFormsModule],
    templateUrl: './login.component.html',
    styles: ``
})
export class LoginComponent implements OnInit {
  signInForm!: UntypedFormGroup;
  submitted: boolean = false;
  private fb: UntypedFormBuilder;
  private router: Router;

  constructor(fb: UntypedFormBuilder, router: Router) {
    this.fb = fb;
    this.router = router;
  }

  ngOnInit(): void {
    this.signInForm = this.fb.group({
      email: ['user@demo.com', [Validators.required, Validators.email]],
      password: ['123456', [Validators.required]],
    })
  }

  get formValues() {
    return this.signInForm.controls
  }

  login() {
    this.submitted = true
    if (this.signInForm.valid) {
      const email = this.formValues['email'].value
      const password = this.formValues['password'].value

      this.router.navigate(['/private', { }]);

    }
  }
}
