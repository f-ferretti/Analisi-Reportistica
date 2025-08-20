package it.unimol.report_management.dto.teacher;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Docente")
public class TeacherDTO {
    private String id;
    private String nome;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
