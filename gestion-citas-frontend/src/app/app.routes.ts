import { Routes } from '@angular/router';
import { authGuard, rolGuard } from './auth.guard';

export const routes: Routes = [
  // Redirigir raíz al login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Rutas públicas
  {
    path: 'login',
    loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'registro',
    loadComponent: () => import('./registro/registro.component').then(m => m.RegistroComponent)
  },

  // Dashboard paciente (requiere auth + rol PACIENTE)
  {
    path: 'paciente',
    canActivate: [rolGuard('PACIENTE')],
    loadComponent: () =>
      import('./dashboard-paciente/dashboard-paciente.component')
        .then(m => m.DashboardPacienteComponent)
  },

  // Dashboard profesional
  {
    path: 'profesional',
    canActivate: [rolGuard('PROFESIONAL')],
    loadComponent: () =>
      import('./dashboard-profesional/dashboard-profesional.component')
        .then(m => m.DashboardProfesionalComponent)
  },

  // Dashboard admin
  {
    path: 'admin',
    canActivate: [rolGuard('ADMINISTRADOR')],
    loadComponent: () =>
      import('./dashboard-admin/dashboard-admin.component')
        .then(m => m.DashboardAdminComponent)
  },

  // Cualquier ruta desconocida → login
  { path: '**', redirectTo: 'login' }
];
