import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { CitasService } from '../citas.service';
import { EspecialidadesService } from '../especialidades.service';
import { UsuariosService } from '../usuarios.service';
import { AuthService } from '../auth.service';
import { CitaResponse, CitaRequest } from '../models/cita.model';
import { Usuario } from '../models/usuario.model';
import { Especialidad, HorarioResponse } from '../models/especialidad.model';

@Component({
  selector: 'app-dashboard-paciente',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './dashboard-paciente.component.html',
  styleUrl: './dashboard-paciente.component.css'
})
export class DashboardPacienteComponent implements OnInit {

  citas: CitaResponse[] = [];
  profesionales: Usuario[] = [];
  profesionalesFiltrados: Usuario[] = [];
  especialidades: Especialidad[] = [];
  horariosDisponibles: HorarioResponse[] = [];

  loading = false;
  errorMsg = '';
  successMsg = '';

  // Modal agendar
  modalAgendar = false;
  tipoAtencion = '';
  especialidadSeleccionada = '';
  nuevaCita: CitaRequest = { profesionalId: 0, fechaHora: '', motivo: '' };

  // Modal reprogramar
  modalReprogramar = false;
  citaAReprogramar: CitaResponse | null = null;
  nuevaFechaHora = '';

  filtroEstado = 'TODAS';

  // Fecha mínima para el datepicker (hoy)
  get fechaMinima(): string {
    const ahora = new Date();
    ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
    return ahora.toISOString().slice(0, 16);
  }

  constructor(
    private readonly citasService: CitasService,
    private readonly especialidadesService: EspecialidadesService,
    private readonly usuariosService: UsuariosService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarCitas();
    this.cargarProfesionales();
    this.cargarEspecialidades();
  }

  get pacienteId(): number {
    return this.authService.getId();
  }

  get citasFiltradas(): CitaResponse[] {
    if (this.filtroEstado === 'TODAS') return this.citas;
    return this.citas.filter(c => c.estado === this.filtroEstado);
  }

  cargarCitas(): void {
    this.loading = true;
    this.citasService.obtenerCitasDePaciente(this.pacienteId).subscribe({
      next: data => { this.citas = data; this.loading = false; },
      error: ()  => { this.loading = false; }
    });
  }

  cargarProfesionales(): void {
    this.usuariosService.obtenerTodos().subscribe({
      next: data => {
        this.profesionales = data.filter(u => u.rol === 'PROFESIONAL' && u.activo);
        this.profesionalesFiltrados = [...this.profesionales];
      }
    });
  }

  cargarEspecialidades(): void {
    this.especialidadesService.obtenerTodas().subscribe({
      next: data => this.especialidades = data
    });
  }

  abrirModalAgendar(): void {
    this.tipoAtencion = '';
    this.especialidadSeleccionada = '';
    this.profesionalesFiltrados = [];
    this.horariosDisponibles = [];
    this.nuevaCita = { profesionalId: 0, fechaHora: '', motivo: '' };
    this.modalAgendar = true;
  }

  cerrarModalAgendar(): void {
    this.modalAgendar = false;
  }

  onTipoAtencionChange(): void {
    this.especialidadSeleccionada = '';
    this.nuevaCita.profesionalId = 0;
    this.horariosDisponibles = [];

    if (this.tipoAtencion === 'MEDICO_GENERAL') {
      // Profesionales sin especialidad asignada
      this.profesionalesFiltrados = this.profesionales.filter(p => !p.especialidad);
      if (this.profesionalesFiltrados.length === 0) {
        // Si todos tienen especialidad, mostrar todos
        this.profesionalesFiltrados = [...this.profesionales];
      }
    } else {
      this.profesionalesFiltrados = [];
    }
  }

  filtrarProfesionalesPorEspecialidad(): void {
    this.nuevaCita.profesionalId = 0;
    this.horariosDisponibles = [];
    if (!this.especialidadSeleccionada) {
      this.profesionalesFiltrados = [];
      return;
    }
    this.profesionalesFiltrados = this.profesionales.filter(
      p => p.especialidad === this.especialidadSeleccionada
    );
  }

  cargarHorariosProfesional(): void {
    if (!this.nuevaCita.profesionalId) return;
    this.especialidadesService.obtenerHorarios(this.nuevaCita.profesionalId).subscribe({
      next: data => this.horariosDisponibles = data
    });
  }

  agendarCita(): void {
    if (!this.nuevaCita.profesionalId || !this.nuevaCita.fechaHora) return;

    // Validar que no sea fecha pasada
    if (new Date(this.nuevaCita.fechaHora) <= new Date()) {
      this.mostrarError('No puedes agendar una cita en el pasado');
      return;
    }

    this.citasService.agendar(this.pacienteId, this.nuevaCita).subscribe({
      next: () => {
        this.modalAgendar = false;
        this.mostrarExito('Cita agendada correctamente');
        this.cargarCitas();
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : 'Error al agendar la cita';
        this.mostrarError(msg);
      }
    });
  }

  cancelarCita(cita: CitaResponse): void {
    if (!confirm(`¿Cancelar la cita con ${cita.profesionalNombre}?`)) return;
    this.citasService.cancelar(cita.id, this.pacienteId).subscribe({
      next: () => { this.mostrarExito('Cita cancelada'); this.cargarCitas(); },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : 'Error al cancelar';
        this.mostrarError(msg);
      }
    });
  }

  abrirReprogramar(cita: CitaResponse): void {
    this.citaAReprogramar = cita;
    this.nuevaFechaHora = '';
    this.modalReprogramar = true;
  }

  reprogramarCita(): void {
    if (!this.citaAReprogramar || !this.nuevaFechaHora) return;

    if (new Date(this.nuevaFechaHora) <= new Date()) {
      this.mostrarError('No puedes reprogramar a una fecha pasada');
      return;
    }

    const req: CitaRequest = {
      profesionalId: 0,
      fechaHora: this.nuevaFechaHora,
      motivo: this.citaAReprogramar.motivo
    };
    this.citasService.reprogramar(this.citaAReprogramar.id, this.pacienteId, req).subscribe({
      next: () => {
        this.modalReprogramar = false;
        this.mostrarExito('Cita reprogramada');
        this.cargarCitas();
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : 'Error al reprogramar';
        this.mostrarError(msg);
      }
    });
  }

  traducirDia(dia: string): string {
    const dias: Record<string, string> = {
      MONDAY: 'Lun', TUESDAY: 'Mar', WEDNESDAY: 'Mié',
      THURSDAY: 'Jue', FRIDAY: 'Vie', SATURDAY: 'Sáb', SUNDAY: 'Dom'
    };
    return dias[dia] ?? dia;
  }

  formatFecha(fechaHora: string): string {
    return new Date(fechaHora).toLocaleString('es-CO', {
      dateStyle: 'medium', timeStyle: 'short'
    });
  }

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

  private mostrarExito(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => this.successMsg = '', 4000);
  }

  private mostrarError(msg: string): void {
    this.errorMsg = msg;
    setTimeout(() => this.errorMsg = '', 5000);
  }
}