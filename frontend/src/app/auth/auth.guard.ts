import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('authToken');
  const router = inject(Router);
  // If token exists, allow access; otherwise, redirect to /login.
  return token ? true : router.parseUrl('/login');
};

export const loginGuard: CanActivateFn = (route, state) => {
  const token = localStorage.getItem('authToken');
  const router = inject(Router);
  // If token exists, redirect to /home; otherwise, allow access.
  return token ? router.parseUrl('/home') : true;
};
