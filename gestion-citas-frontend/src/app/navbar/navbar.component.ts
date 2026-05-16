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

  dropdownAbierto = false;
  mostrarPerfil   = false;

  perfilForm = { nombre: '', genero: '', telefono: '', fotoPerfil: '' };
  editandoNombre   = false;
  editandoTelefono = false;
  editandoGenero   = false;
  guardandoPerfil  = false;
  successPerfilMsg = '';
  errorPerfilMsg   = '';

  constructor(
    public readonly authService: AuthService,
    private readonly usuariosService: UsuariosService
  ) {}

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

  logout(): void { this.authService.logout(); }

  toggleDropdown(): void { this.dropdownAbierto = !this.dropdownAbierto; }

  abrirPerfil(): void {
    this.dropdownAbierto = false;
    this.successPerfilMsg = '';
    this.errorPerfilMsg   = '';
    this.editandoNombre   = false;
    this.editandoTelefono = false;
    this.editandoGenero   = false;

    const u = this.authService.getUsuario() as any;
    this.perfilForm.nombre     = u?.nombre     ?? '';
    this.perfilForm.genero     = u?.genero     ?? '';
    this.perfilForm.telefono   = u?.telefono   ?? '';
    this.perfilForm.fotoPerfil = u?.fotoPerfil ?? '';
    this.mostrarPerfil = true;
  }

  cerrarPerfil(): void {
    this.mostrarPerfil    = false;
    this.editandoNombre   = false;
    this.editandoTelefono = false;
    this.editandoGenero   = false;
    this.successPerfilMsg = '';
    this.errorPerfilMsg   = '';
  }

  onFotoSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const reader = new FileReader();
    reader.onload = () => { this.perfilForm.fotoPerfil = reader.result as string; };
    reader.readAsDataURL(input.files[0]);
  }

  guardarPerfil(): void {
    this.guardandoPerfil = true;
    const id = this.authService.getId();

    const data: Record<string, string | undefined> = {
      nombre:     this.perfilForm.nombre     || undefined,
      genero:     this.perfilForm.genero     || undefined,
      fotoPerfil: this.perfilForm.fotoPerfil || undefined
    };
    if (this.rol === 'PACIENTE') {
      data['telefono'] = this.perfilForm.telefono || undefined;
    }

    this.usuariosService.actualizarPerfilGeneral(id, data as any).subscribe({
      next: () => {
        this.guardandoPerfil  = false;
        this.editandoNombre   = false;
        this.editandoTelefono = false;
        this.editandoGenero   = false;
        this.successPerfilMsg = 'Perfil actualizado correctamente';

        const u = this.authService.getUsuario() as any;
        if (u) {
          u.nombre     = this.perfilForm.nombre;
          u.genero     = this.perfilForm.genero;
          u.fotoPerfil = this.perfilForm.fotoPerfil;
          if (this.rol === 'PACIENTE') u.telefono = this.perfilForm.telefono;
          localStorage.setItem('usuario', JSON.stringify(u));
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
