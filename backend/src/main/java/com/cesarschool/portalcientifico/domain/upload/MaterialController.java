package com.cesarschool.portalcientifico.domain.upload;

import com.cesarschool.portalcientifico.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/materials")
@RequiredArgsConstructor
@Tag(name = "Materiais", description = "Endpoints para gerenciamento de materiais")
public class MaterialController {

    private final MaterialService materialService;

    @Operation(
            summary = "Faz o upload de um material",
            description = "Permite que um usuário envie um material com seus metadados e um arquivo.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do material e arquivo a ser enviado",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Material criado com sucesso",
                    content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaterialResponseDTO> uploadMaterial(
            @Valid @RequestPart MaterialRequestDTO materialRequestDTO,
            @RequestPart("file") @Schema(type = "string", format = "binary")  MultipartFile file,
            @Parameter(hidden = true) Authentication authentication
    ) throws IOException {
        User user = (User) authentication.getPrincipal();
        MaterialResponseDTO materialResponse = materialService.uploadMaterial(materialRequestDTO, user, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialResponse);
    }

    @Operation(
            summary = "Obtém um material pelo ID",
            description = "Retorna os detalhes de um material existente a partir de seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = MaterialResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Material não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> getMaterialDetails(
            @Parameter(description = "ID do material", required = true) @PathVariable Long id) {
        MaterialResponseDTO materialResponse = materialService.getMaterialDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(materialResponse);
    }

    @Operation(
            summary = "Obtém todos os materiais",
            description = "Retorna a lista completa de materiais cadastrados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de materiais obtida com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MaterialResponseDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> getAllMaterials() {
        List<MaterialResponseDTO> materialResponse = materialService.getAllMaterials();
        return ResponseEntity.status(HttpStatus.OK).body(materialResponse);
    }

    @Operation(
            summary = "Gera uma URL temporária para download do material",
            description = "Retorna uma URL temporária (presigned) para download direto do S3."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL gerada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Material não encontrado")
    })
    @GetMapping("/{id}/download-url")
    public ResponseEntity<DownloadUrlResponse> generateDownloadUrl(@PathVariable Long id) {
        return ResponseEntity.ok(materialService.getFileNameByMaterialId(id));
    }
}
