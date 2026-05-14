import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { UsuariosService } from '../usuarios.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  // ── Estado del dropdown ───────────────────────────────────
  dropdownAbierto = false;
  mostrarPerfil   = false;

  // ── Formulario perfil paciente ────────────────────────────
  perfilForm       = { telefono: '', genero: '' };
  editandoTelefono = false;
  editandoGenero   = false;
  guardandoPerfil  = false;
  successPerfilMsg = '';
  errorPerfilMsg   = '';

  constructor(
    public readonly authService: AuthService,
    private readonly usuariosService: UsuariosService
  ) {}

  // ── Getters ───────────────────────────────────────────────

  get usuario() { return this.authService.getUsuario(); }

  get rol(): string { return this.authService.getRol() ?? ''; }

  get rolLabel(): string {
    const labels: Record<string, string> = {
      PACIENTE:      'Paciente',
      PROFESIONAL:   'Profesional',
      ADMINISTRADOR: 'Administrador'
    };
    return labels[this.rol] ?? '';
  }

  // ── Acciones navbar ───────────────────────────────────────

  logout(): void {
    this.authService.logout();
  }

  toggleDropdown(): void {
    this.dropdownAbierto = !this.dropdownAbierto;
  }

  // ── Perfil paciente ───────────────────────────────────────

  abrirPerfil(): void {
    this.dropdownAbierto = false;
    this.successPerfilMsg = '';
    this.errorPerfilMsg   = '';
    this.editandoTelefono = false;
    this.editandoGenero   = false;

    // Cargar datos actuales desde localStorage
    const u = this.authService.getUsuario() as any;
    this.perfilForm.telefono = u?.telefono ?? '';
    this.perfilForm.genero   = u?.genero   ?? '';
    this.mostrarPerfil = true;
  }

  cerrarPerfil(): void {
    this.mostrarPerfil    = false;
    this.editandoTelefono = false;
    this.editandoGenero   = false;
    this.successPerfilMsg = '';
    this.errorPerfilMsg   = '';
  }

  guardarPerfil(): void {
    if (!this.editandoTelefono && !this.editandoGenero) {
      this.cerrarPerfil();
      return;
    }

    this.guardandoPerfil = true;
    const id = this.authService.getId();

    this.usuariosService.actualizarPerfilPaciente(id, this.perfilForm).subscribe({
      next: (res: any) => {
        this.guardandoPerfil  = false;
        this.editandoTelefono = false;
        this.editandoGenero   = false;
        this.successPerfilMsg = 'Perfil actualizado correctamente';

        // Actualizar los datos en localStorage para que persistan
        const usuarioActual = this.authService.getUsuario() as any;
        if (usuarioActual) {
          usuarioActual.telefono = this.perfilForm.telefono;
          usuarioActual.genero   = this.perfilForm.genero;
          localStorage.setItem('usuario', JSON.stringify(usuarioActual));
        }

        setTimeout(() => this.successPerfilMsg = '', 3000);
      },
      error: () => {
        this.guardandoPerfil = false;
        this.errorPerfilMsg  = 'Error al actualizar el perfil';
      }
    });
  }

  etiquetaGenero(g: string): string {
    const map: Record<string, string> = { M: 'Masculino', F: 'Femenino', OTRO: 'Otro' };
    return map[g] ?? 'No definido';
  }
}