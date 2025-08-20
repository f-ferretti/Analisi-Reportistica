package it.unimol.report_management.service;

import it.unimol.report_management.dto.teacher.TeacherConsistencyDTO;

public interface TeacherReportService {
    /** PDF della distribuzione voti del docente in un dato anno accademico. */
    byte[] gradesDistributionPdf(String teacherId, Integer aa);

    /** Consistenza anno-su-anno per docente/corso nell’intervallo [from..to]. */
    TeacherConsistencyDTO consistency(String teacherId, String courseCode, Integer from, Integer to);
}