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

  form = {
    nombre: '',
    correo: '',
    contrasena: '',
    confirmarContrasena: '',
    telefono: '',
    tipoDocumento: 'CEDULA',
    numeroDocumento: '',
    genero: '',
    edad: null as number | null
  };

  errorMessage = '';
  loading = false;
  mostrarPassword = false;
  mostrarConfirmar = false;

  edades = Array.from({ length: 100 }, (_, i) => i + 1);

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.errorMessage = '';

    if (this.form.contrasena !== this.form.confirmarContrasena) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }
    if (this.form.contrasena.length < 6) {
      this.errorMessage = 'La contraseña debe tener mínimo 6 caracteres.';
      return;
    }

    this.loading = true;

    const payload = {
      nombre:          this.form.nombre,
      correo:          this.form.correo,
      contrasena:      this.form.contrasena,
      telefono:        this.form.telefono,
      tipoDocumento:   this.form.tipoDocumento,
      numeroDocumento: this.form.numeroDocumento,
      genero:          this.form.genero,
      edad:            this.form.edad
    };

    this.authService.registro(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/paciente']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = typeof err.error === 'string'
          ? err.error
          : 'Error al registrarse. Intenta de nuevo.';
      }
    });
  }
}