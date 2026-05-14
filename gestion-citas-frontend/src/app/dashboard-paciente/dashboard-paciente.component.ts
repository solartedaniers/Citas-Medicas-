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
import { Especialidad } from '../models/especialidad.model';

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
  especialidades: Especialidad[] = [];

  loading = false;
  errorMsg = '';
  successMsg = '';

  // Modal agendar
  modalAgendar = false;
  nuevaCita: CitaRequest = { profesionalId: 0, fechaHora: '', motivo: '' };

  // Modal reprogramar
  modalReprogramar = false;
  citaAReprogramar: CitaResponse | null = null;
  nuevaFechaHora = '';

  // Filtro
  filtroEstado = 'TODAS';

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
    return (this.authService.getUsuario() as any)?.id ?? 0;
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
      next: data => this.profesionales = data.filter(u => u.rol === 'PROFESIONAL' && u.activo)
    });
  }

  cargarEspecialidades(): void {
    this.especialidadesService.obtenerTodas().subscribe({
      next: data => this.especialidades = data
    });
  }

  abrirModalAgendar(): void {
    this.nuevaCita = { profesionalId: 0, fechaHora: '', motivo: '' };
    this.modalAgendar = true;
  }

  agendarCita(): void {
    if (!this.nuevaCita.profesionalId || !this.nuevaCita.fechaHora) return;
    this.citasService.agendar(this.pacienteId, this.nuevaCita).subscribe({
      next: () => {
        this.modalAgendar = false;
        this.mostrarExito('Cita agendada correctamente');
        this.cargarCitas();
      },
      error: (err) => this.mostrarError(err.error || 'Error al agendar la cita')
    });
  }

  cancelarCita(cita: CitaResponse): void {
    if (!confirm(`¿Cancelar la cita con ${cita.profesionalNombre}?`)) return;
    this.citasService.cancelar(cita.id, this.pacienteId).subscribe({
      next: () => { this.mostrarExito('Cita cancelada'); this.cargarCitas(); },
      error: (err) => this.mostrarError(err.error || 'Error al cancelar')
    });
  }

  abrirReprogramar(cita: CitaResponse): void {
    this.citaAReprogramar = cita;
    this.nuevaFechaHora = '';
    this.modalReprogramar = true;
  }

  reprogramarCita(): void {
    if (!this.citaAReprogramar || !this.nuevaFechaHora) return;
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
      error: (err) => this.mostrarError(err.error || 'Error al reprogramar')
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
