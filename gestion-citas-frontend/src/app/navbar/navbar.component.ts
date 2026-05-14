import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(public readonly authService: AuthService) {}

  logout(): void {
    this.authService.logout();
  }

  get usuario() {
    return this.authService.getUsuario();
  }

  get rolLabel(): string {
    const labels: Record<string, string> = {
      PACIENTE: 'Paciente',
      PROFESIONAL: 'Profesional',
      ADMINISTRADOR: 'Administrador'
    };
    return labels[this.authService.getRol() ?? ''] ?? '';
  }
}
