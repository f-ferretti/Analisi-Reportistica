package it.unimol.report_management.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Corso")
public class CourseDTO {
    @Schema(example = "ST101")
    private String codice;
    @Schema(example = "Programmazione 1")
    private String nome;
    @Schema(example = "9")
    private int cfu;
    private List<String> docentiIds;

    public String getCodice() { return codice; }
    public void setCodice(String codice) { this.codice = codice; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getCfu() { return cfu; }
    public void setCfu(int cfu) { this.cfu = cfu; }
    public List<String> getDocentiIds() { return docentiIds; }
    public void setDocentiIds(List<String> docentiIds) { this.docentiIds = docentiIds; }
}
