import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { UsuariosService } from '../usuarios.service';
import { EspecialidadesService } from '../especialidades.service';
import { CitasService } from '../citas.service';
import { Usuario } from '../models/usuario.model';
import { Especialidad, EspecialidadRequest } from '../models/especialidad.model';
import { CitaResponse } from '../models/cita.model';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './dashboard-admin.component.html',
  styleUrl: './dashboard-admin.component.css'
})
export class DashboardAdminComponent implements OnInit {

  vistaActiva: 'usuarios' | 'especialidades' | 'citas' = 'usuarios';

  usuarios: Usuario[] = [];
  loadingUsuarios = false;

  especialidades: Especialidad[] = [];
  modalEspecialidad = false;
  modoEdicion = false;
  especialidadForm: EspecialidadRequest = { nombre: '', descripcion: '' };
  especialidadEditandoId: number | null = null;

  citas: CitaResponse[] = [];
  loadingCitas = false;

  successMsg = '';
  errorMsg = '';

  constructor(
    private readonly usuariosService: UsuariosService,
    private readonly especialidadesService: EspecialidadesService,
    private readonly citasService: CitasService
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
    this.cargarEspecialidades();
    this.cargarCitas();
  }

  // Método explícito para cambiar vista — evita el problema de tipos en template
  setVista(vista: 'usuarios' | 'especialidades' | 'citas'): void {
    this.vistaActiva = vista;
  }

  // Devuelve la clase CSS del badge según el estado de la cita
  getBadgeClase(estado: string): string {
    const mapa: Record<string, string> = {
      PENDIENTE:    'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-100 text-amber-700',
      CONFIRMADA:   'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-700',
      CANCELADA:    'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-100 text-red-700',
      REPROGRAMADA: 'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-100 text-blue-700',
      COMPLETADA:   'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600',
    };
    return mapa[estado] ?? 'px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-100 text-slate-600';
  }

  // ── Usuarios ──────────────────────────────────────────────

  cargarUsuarios(): void {
    this.loadingUsuarios = true;
    this.usuariosService.obtenerTodos().subscribe({
      next: data => { this.usuarios = data; this.loadingUsuarios = false; },
      error: ()  => { this.loadingUsuarios = false; }
    });
  }

  asignarRol(usuario: Usuario, rol: string): void {
    if (usuario.rol === rol) return;
    this.usuariosService.asignarRol(usuario.id, { rol }).subscribe({
      next: () => {
        this.mostrarExito('Rol actualizado correctamente');
        this.cargarUsuarios();
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : 'Error al asignar rol';
        this.mostrarError(msg);
        this.cargarUsuarios();
      }
    });
  }

  asignarEspecialidadAProfesional(profesionalId: number, especialidadId: number): void {
    if (!especialidadId) return;
    this.usuariosService.asignarEspecialidad(profesionalId, especialidadId).subscribe({
      next: () => { this.mostrarExito('Especialidad asignada'); this.cargarUsuarios(); },
      error: (err) => this.mostrarError(err.error || 'Error al asignar especialidad')
    });
  }

  cambiarEstado(usuario: Usuario): void {
    this.usuariosService.cambiarEstado(usuario.id, !usuario.activo).subscribe({
      next: () => { this.mostrarExito('Estado actualizado'); this.cargarUsuarios(); },
      error: () => this.mostrarError('Error al cambiar estado')
    });
  }

  eliminarUsuario(usuario: Usuario): void {
    if (!confirm(`¿Eliminar a ${usuario.nombre}?`)) return;
    this.usuariosService.eliminar(usuario.id).subscribe({
      next: () => { this.mostrarExito('Usuario eliminado'); this.cargarUsuarios(); },
      error: () => this.mostrarError('Error al eliminar')
    });
  }

  // ── Especialidades ────────────────────────────────────────

  cargarEspecialidades(): void {
    this.especialidadesService.obtenerTodas().subscribe({
      next: data => this.especialidades = data
    });
  }

  abrirCrearEspecialidad(): void {
    this.modoEdicion = false;
    this.especialidadEditandoId = null;
    this.especialidadForm = { nombre: '', descripcion: '' };
    this.modalEspecialidad = true;
  }

  abrirEditarEspecialidad(esp: Especialidad): void {
    this.modoEdicion = true;
    this.especialidadEditandoId = esp.id;
    this.especialidadForm = { nombre: esp.nombre, descripcion: esp.descripcion ?? '' };
    this.modalEspecialidad = true;
  }

  guardarEspecialidad(): void {
    if (this.modoEdicion && this.especialidadEditandoId !== null) {
      this.especialidadesService.actualizar(this.especialidadEditandoId, this.especialidadForm).subscribe({
        next: () => { this.modalEspecialidad = false; this.mostrarExito('Actualizada'); this.cargarEspecialidades(); },
        error: (err) => this.mostrarError(err.error || 'Error')
      });
    } else {
      this.especialidadesService.crear(this.especialidadForm).subscribe({
        next: () => { this.modalEspecialidad = false; this.mostrarExito('Creada'); this.cargarEspecialidades(); },
        error: (err) => this.mostrarError(err.error || 'Error')
      });
    }
  }

  eliminarEspecialidad(esp: Especialidad): void {
    if (!confirm(`¿Eliminar "${esp.nombre}"?`)) return;
    this.especialidadesService.eliminar(esp.id).subscribe({
      next: () => { this.mostrarExito('Eliminada'); this.cargarEspecialidades(); },
      error: () => this.mostrarError('Error al eliminar')
    });
  }

  // ── Citas ─────────────────────────────────────────────────

  cargarCitas(): void {
    this.loadingCitas = true;
    this.citasService.obtenerTodasLasCitas().subscribe({
      next: data => { this.citas = data; this.loadingCitas = false; },
      error: ()  => { this.loadingCitas = false; }
    });
  }

  formatFecha(fechaHora: string): string {
    return new Date(fechaHora).toLocaleString('es-CO', {
      dateStyle: 'medium', timeStyle: 'short'
    });
  }

  private mostrarExito(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => this.successMsg = '', 4000);
  }

  private mostrarError(msg: string): void {
    this.errorMsg = msg;
    setTimeout(() => this.errorMsg = '', 5000);
  }
}
