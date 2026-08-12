import {AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators} from '@angular/forms';
import {Component, inject, signal} from '@angular/core';
import {PasswordModule} from 'primeng/password';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from "primeng/button";
import {AuthControllerService} from '@app/api/api/authController.service';
import {Router, RouterLink} from "@angular/router";
import {HttpErrorResponse} from '@angular/common/http';

const passwordsMatch = (group: AbstractControl): ValidationErrors | null => {
  const {password, confirmPassword} = group.value;
  return password === confirmPassword ? null : {passwordMismatch: true};
};


@Component({
  selector: 'app-signup-page',
  templateUrl: './signup-page.html',
  imports: [ReactiveFormsModule,
    InputTextModule, PasswordModule,
    ButtonModule, RouterLink],
})
export class SignupPage {
  private readonly api = inject(AuthControllerService);
  private readonly router = inject(Router);

  protected readonly form = inject(FormBuilder).nonNullable.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  }, {validators: passwordsMatch});

  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);

  private readonly clearErrorOnEdit = this.form.valueChanges.subscribe(() => this.error.set(null));

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }


    this.loading.set(true);
    this.error.set(null);
    const {firstName, lastName, email, password} = this.form.getRawValue();

    this.api.register({firstName, lastName, email, password})
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: (err: HttpErrorResponse) => {
          console.log(err);
          this.error.set(
            err.error?.message ?? 'Something went wrong. Please try again.',
          );
          this.loading.set(false);
        },
      });
  }
}

