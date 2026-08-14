import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from '../auth/auth-service';
import {catchError, throwError} from 'rxjs';
import {Router} from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(AuthService).getToken();
  const router = inject(Router);

  if (!token) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: {Authorization: `Bearer ${token}`},
  })).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401 || error.status === 403) {
      router.navigate(['/login']);
    }
    return throwError(() => error);
  }));
};
