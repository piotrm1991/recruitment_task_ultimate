package it.piotrmachnik.recruitmentTaskUltimateSystems.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorModel {
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;
    private String message;
    private List<String> details;
}
