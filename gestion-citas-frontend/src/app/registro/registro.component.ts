import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registro.component.html',
  styleUrl: './registro.component.css'
})
export class RegistroComponent {

  form = { nombre: '', correo: '', contrasena: '', telefono: '' };
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

    this.authService.registro(this.form).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/paciente']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error || 'Error al registrarse. Intenta de nuevo.';
      }
    });
  }
}
