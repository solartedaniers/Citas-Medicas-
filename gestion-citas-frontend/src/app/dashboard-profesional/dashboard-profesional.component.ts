import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { CitasService } from '../citas.service';
import { EspecialidadesService } from '../especialidades.service';
import { UsuariosService } from '../usuarios.service';
import { AuthService } from '../auth.service';
import { CitaResponse } from '../models/cita.model';
import { HorarioRequest, HorarioResponse } from '../models/especialidad.model';

@Component({
  selector: 'app-dashboard-profesional',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './dashboard-profesional.component.html',
  styleUrl: './dashboard-profesional.component.css'
})
export class DashboardProfesionalComponent implements OnInit {

  citas: CitaResponse[] = [];
  horarios: HorarioResponse[] = [];
  loading = false;
  successMsg = '';
  errorMsg = '';

  vistaActiva: 'agenda' | 'horarios' | 'perfil' = 'agenda';

  diasSemana = [
    { valor: 'MONDAY',    etiqueta: 'Lunes' },
    { valor: 'TUESDAY',   etiqueta: 'Martes' },
    { valor: 'WEDNESDAY', etiqueta: 'Miércoles' },
    { valor: 'THURSDAY',  etiqueta: 'Jueves' },
    { valor: 'FRIDAY',    etiqueta: 'Viernes' },
    { valor: 'SATURDAY',  etiqueta: 'Sábado' },
  ];

  tiposProfesional = [
    'Médico General',
    'Médico Especialista',
    'Odontólogo',
    'Psicólogo',
    'Fisioterapeuta',
    'Nutricionista',
    'Enfermero/a',
    'Optómetra',
    'Fonoaudiólogo',
    'Otro'
  ];

  horariosForm: HorarioRequest[] = [];
  tipoProfesionalForm = '';
  guardandoPerfil = false;

  constructor(
    private readonly citasService: CitasService,
    private readonly especialidadesService: EspecialidadesService,
    private readonly usuariosService: UsuariosService,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarCitas();
    this.cargarHorarios();
    const usuario = this.authService.getUsuario() as any;
    this.tipoProfesionalForm = usuario?.tipoProfesional ?? '';
  }

  get profesionalId(): number { return this.authService.getId(); }

  setVista(v: 'agenda' | 'horarios' | 'perfil'): void { this.vistaActiva = v; }

  cargarCitas(): void {
    this.loading = true;
    this.citasService.obtenerCitasDeProfesional(this.profesionalId).subscribe({
      next: data => { this.citas = data; this.loading = false; },
      error: ()  => { this.loading = false; }
    });
  }

  cargarHorarios(): void {
    this.especialidadesService.obtenerHorarios(this.profesionalId).subscribe({
      next: data => {
        this.horarios = data;
        this.horariosForm = data.map(h => ({
          diaSemana: h.diaSemana,
          horaInicio: h.horaInicio,
          horaFin: h.horaFin
        }));
      }
    });
  }

  agregarHorario(): void {
    this.horariosForm.push({ diaSemana: 'MONDAY', horaInicio: '08:00', horaFin: '12:00' });
  }

  quitarHorario(idx: number): void { this.horariosForm.splice(idx, 1); }

  guardarHorarios(): void {
    this.especialidadesService.definirHorarios(this.profesionalId, this.horariosForm).subscribe({
      next: () => this.mostrarExito('Horarios guardados correctamente'),
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : 'Error al guardar los horarios';
        this.mostrarError(msg);
      }
    });
  }

  guardarTipoProfesional(): void {
    this.guardandoPerfil = true;
    this.usuariosService.actualizarTipoProfesional(this.profesionalId, this.tipoProfesionalForm).subscribe({
      next: () => {
        this.guardandoPerfil = false;
        this.mostrarExito('Tipo de profesional actualizado');
      },
      error: () => {
        this.guardandoPerfil = false;
        this.mostrarError('Error al actualizar');
      }
    });
  }

  getBadgeCita(estado: string): string {
    const m: Record<string, string> = {
      PENDIENTE:    'bg-amber-100 text-amber-700',
      CONFIRMADA:   'bg-emerald-100 text-emerald-700',
      CANCELADA:    'bg-red-100 text-red-700',
      REPROGRAMADA: 'bg-blue-100 text-blue-700',
      COMPLETADA:   'bg-slate-100 text-slate-600',
    };
    return `px-2.5 py-0.5 rounded-full text-xs font-semibold ${m[estado] ?? 'bg-slate-100 text-slate-600'}`;
  }

  formatFecha(fechaHora: string): string {
    return new Date(fechaHora).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
  }

  etiquetaDia(valor: string): string {
    return this.diasSemana.find(d => d.valor === valor)?.etiqueta ?? valor;
  }

  private mostrarExito(msg: string): void {
    this.successMsg = msg; setTimeout(() => this.successMsg = '', 4000);
  }
  private mostrarError(msg: string): void {
    this.errorMsg = msg; setTimeout(() => this.errorMsg = '', 5000);
  }
}