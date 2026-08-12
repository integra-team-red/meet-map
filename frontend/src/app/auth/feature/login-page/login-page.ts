import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {ButtonModule} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {PasswordModule} from 'primeng/password';
import {AuthControllerService} from '@app/api/api/authController.service';
import {InputTextModule} from 'primeng/inputtext';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.html',
  imports: [ReactiveFormsModule,
    InputTextModule, PasswordModule,
    CheckboxModule, ButtonModule, RouterLink],
})
export class LoginPage {
  private readonly api = inject(AuthControllerService)
  private readonly router = inject(Router);

  protected readonly form = inject(FormBuilder).nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    rememberMe: [true],
  });

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
    const {email, password, rememberMe} =
      this.form.getRawValue();

    this.api.login({email, password})
      .subscribe({
        next: ({token}) => {
          (rememberMe ? localStorage : sessionStorage).setItem('token', token!);
          this.loading.set(false);
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.error.set(
            err.status === 401 ? 'Invalid email or password.' : 'Something went wrong. Please try again.',
          );
          this.loading.set(false);
        },
      });
  }

}
