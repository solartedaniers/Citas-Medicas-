import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';
import { LoginCredentials } from '../models/login.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  credentials: LoginCredentials = { correo: '', contrasena: '', recordarme: false };
  errorMessage = '';
  loading = false;
  mostrarPassword = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.loading = true;

    this.authService.login({
      correo:    this.credentials.correo,
      contrasena: this.credentials.contrasena
    }).subscribe({
      next: (res) => {
        this.loading = false;
        // Redirigir según rol
        switch (res.rol) {
          case 'PACIENTE':      this.router.navigate(['/paciente']);      break;
          case 'PROFESIONAL':   this.router.navigate(['/profesional']);   break;
          case 'ADMINISTRADOR': this.router.navigate(['/admin']);         break;
          default:              this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = typeof err.error === 'string'
          ? err.error
          : 'Correo o contraseña incorrectos.';
      }
    });
  }
}
