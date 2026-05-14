package co.ucc.apicitasmedicas.dto;

/** DTO que recibe el refreshToken para renovar el token de acceso. */
public class RefreshTokenRequestDTO {

    private String refreshToken;

    public RefreshTokenRequestDTO() {}

    public String getRefreshToken()                    { return refreshToken; }
    public void   setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
