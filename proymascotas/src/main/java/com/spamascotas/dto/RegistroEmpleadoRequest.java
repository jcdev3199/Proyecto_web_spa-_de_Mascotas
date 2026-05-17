package com.spamascotas.dto;

import lombok.Data;



public class RegistroEmpleadoRequest {
    private String username;
    private String email;
    private String password;
    private String rol;
    private String telefono;
    private String especialidad;
    private String turno;
    private String horaEntrada; 
    private String horaSalida;
    private String nombres;
    private String apellidos;

    private Integer capacidadSimultanea;

    public Integer getCapacidadSimultanea() { return capacidadSimultanea; }
    public void setCapacidadSimultanea(Integer capacidadSimultanea) { this.capacidadSimultanea = capacidadSimultanea; }



    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }
}