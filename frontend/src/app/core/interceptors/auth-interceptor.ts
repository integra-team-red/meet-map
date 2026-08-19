import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from '../auth/auth-service';
import {catchError, throwError} from 'rxjs';
import {Router} from '@angular/router';


const EXCLUDED_PATHS = ['/api/auth/login', '/api/auth/register'];


export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (EXCLUDED_PATHS.some(path => req.url.includes(path))) {
    return next(req);
  }

  const authService = inject(AuthService);
  const token = authService.getToken();
  const router = inject(Router);

  if (!token) {
      return next(req);
  }

  return next(req.clone({
    setHeaders: {Authorization: `Bearer ${token}`},
  })).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401 || error.status === 403) {
      authService.clearToken();
      router.navigate(['/login']);
    }
    return throwError(() => error);
  }));
};
