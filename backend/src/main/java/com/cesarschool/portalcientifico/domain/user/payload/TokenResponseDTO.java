package com.cesarschool.portalcientifico.domain.user.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

    String accessToken;
    String refreshToken;
    UserResponseDTO user;
}
